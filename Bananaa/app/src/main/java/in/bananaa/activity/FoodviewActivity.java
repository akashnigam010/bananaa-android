package in.bananaa.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.DishSearchAdapter;
import in.bananaa.object.DishSearchItem;
import in.bananaa.object.DishSearchResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MyItemFoodviewResponse;
import in.bananaa.object.StatusResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class FoodviewActivity extends AppCompatActivity {
    public final static String FOODVIEW_DETAILS = "foodviewDetails";
    public static final String RELOAD_FOODVIEWS = "reloadFoodviews";
    private ItemFoodViewDetails itemFoodViewDetails;
    private MyItemFoodviewResponse response;
    private Integer currentItemId;
    private AppCompatActivity mContext;

    private TextView foodViewModalTitle;
    private TextView tvRestName;
    private EditText etDishSearch;
    private EditText etFoodView;
    private TextView tvTextCount;
    private ProgressBar progress;
    private ImageView cancelIcon;

    private ListView lvDishSearchResults;
    private LinearLayout rateAndReviewLayout;
    private RatingBar dishRatingBar;
    private DishSearchAdapter dishSearchAdapter;

    private Float rating;
    private String foodview;
    private boolean canPerformSearch = true;
    private boolean isRatingOrFoodviewChanged = false;

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodview);
        itemFoodViewDetails = (ItemFoodViewDetails) getIntent().getSerializableExtra(FOODVIEW_DETAILS);
        currentItemId = itemFoodViewDetails.getItemId();
        initializeView();
        setItemDetails();
    }

    private void initializeView() {
        tvRestName = (TextView) findViewById(R.id.tvRestName);
        rateAndReviewLayout = (LinearLayout) findViewById(R.id.rateAndReviewLayout);
        dishRatingBar = (RatingBar) findViewById(R.id.dishRatingBar);
        etDishSearch = (EditText) findViewById(R.id.etDishSearch);
        cancelIcon = (ImageView) findViewById(R.id.dishCancelButton);
        progress = (ProgressBar) findViewById(R.id.dishSearchLoader);
        etFoodView = (EditText) findViewById(R.id.etFoodView);
        tvTextCount = (TextView) findViewById(R.id.tvTextCount);
        tvRestName.setText("At " + itemFoodViewDetails.getRestName() + ", " + itemFoodViewDetails.getLocality());
        customizeToolbar();
        customizeListView();
        customizeSearch();
        customizeFoodview();
        setFont();
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        foodViewModalTitle = (TextView) findViewById(R.id.foodViewModalTitle);
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuItems) {
        getMenuInflater().inflate(R.menu.foodview_modal_menu, menuItems);
        menu = menuItems;
        Utils.setMenuItemsFont(menuItems, Utils.getBold(this), this);
        return super.onCreateOptionsMenu(menuItems);
    }

    private void addDeleteMenu() {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            if (mi.getItemId() == R.id.action_delete) {
                mi.setVisible(true);
            }
        }

    }

    private void removeDeleteMenu() {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            if (mi.getItemId() == R.id.action_delete) {
                mi.setVisible(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishActivity();
        }

        if (id == R.id.action_delete) {
            alertForDelete(mContext.getString(R.string.sureToDelete));
        }

        if (id == R.id.action_save) {
            if (Utils.isEmpty(etDishSearch.getText().toString())) {
                Toast.makeText(mContext, "Please search for a dish", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (dishRatingBar.getRating() == 0.0f) {
                Toast.makeText(mContext, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (etFoodView.getText().length() > 0 && etFoodView.getText().length() < 50) {
                Toast.makeText(mContext, "Foodview must be minimum 50 characters long.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (rating != null && dishRatingBar.getRating() == rating && etFoodView.getText().toString().equals(foodview)) {
                finishActivity();
                return false;
            }
            if (!Utils.checkIfInternetConnectedAndToast(this)) {
                return false;
            } else {
                try {
                    asyncStart();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", currentItemId);
                    jsonObject.put("rating", dishRatingBar.getRating());
                    jsonObject.put("description", etFoodView.getText());
                    StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
                    client.setTimeout(Constant.TIMEOUT);
                    client.post(mContext, URLs.SAVE_FOODVIEW, entity, "application/json", new SaveFoodviewResponseHandler());
                } catch (Exception e) {
                    Utils.exceptionOccurred(mContext, e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void alertForDelete(String message) {
        final Dialog confirmDialog = new Dialog(mContext);
        confirmDialog.setCancelable(true);
        confirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmDialog.setContentView(R.layout.dialog_confirm);
        confirmDialog.getWindow().getAttributes().width = WindowManager.LayoutParams.MATCH_PARENT;
        TextView tvMessage = (TextView) confirmDialog.findViewById(R.id.tvMessage);
        AppCompatButton btnYes = (AppCompatButton) confirmDialog.findViewById(R.id.btnYes);
        AppCompatButton btnNo = (AppCompatButton) confirmDialog.findViewById(R.id.btnNo);

        tvMessage.setText(message);

        tvMessage.setTypeface(Utils.getRegularFont(mContext));
        btnYes.setTypeface(Utils.getRegularFont(mContext));
        btnNo.setTypeface(Utils.getRegularFont(mContext));

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
                    return;
                } else {
                    try {
                        asyncStart();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id", response.getRecommendation().getId());
                        StringEntity entity = new StringEntity(jsonObject.toString());
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
                        client.setTimeout(Constant.TIMEOUT);
                        client.post(mContext, URLs.DELETE_FOODVIEW, entity, "application/json", new DeleteFoodviewResponseHandler());
                    } catch (Exception e) {
                        Utils.exceptionOccurred(mContext, e);
                    }
                }
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.cancel();
            }
        });
        confirmDialog.show();
    }

    private class DeleteFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                Toast.makeText(mContext, "Your foodview has been deleted.", Toast.LENGTH_SHORT).show();
                isRatingOrFoodviewChanged = true;
                finishActivity();
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    private class SaveFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                Toast.makeText(mContext, "Your foodview has been saved.", Toast.LENGTH_SHORT).show();
                isRatingOrFoodviewChanged = true;
                finishActivity();
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            Utils.responseFailure(mContext);
        }
    }

    private void finishActivity() {
        if (isRatingOrFoodviewChanged) {
            Intent intent = getIntent();
            intent.putExtra(RELOAD_FOODVIEWS, true);
            setResult(Activity.RESULT_OK, intent);
        }
        finish();
    }

    private void setItemDetails() {
        if (itemFoodViewDetails.isSetDishDetails()) {
            canPerformSearch = false;
            etDishSearch.setText(itemFoodViewDetails.getItemName());
            lvDishSearchResults.setVisibility(View.GONE);
            rateAndReviewLayout.setVisibility(View.VISIBLE);
            getMyFoodviewDetails();
        } else {
            lvDishSearchResults.setVisibility(View.VISIBLE);
            rateAndReviewLayout.setVisibility(View.GONE);
        }
    }

    private void customizeSearch() {
        cancelIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etDishSearch.setText("");
                lvDishSearchResults.setVisibility(View.VISIBLE);
                rateAndReviewLayout.setVisibility(View.GONE);
                return true;
            }
        });

        etDishSearch.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() >= 2 && canPerformSearch) {
                            lvDishSearchResults.setVisibility(View.VISIBLE);
                            rateAndReviewLayout.setVisibility(View.GONE);
                            doSearch(s.toString());
                        } else {
                            canPerformSearch = true;
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (v == 0.0f) {
                //return;
            }
            if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
                return;
            } else {
                try {
                    asyncStart();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", currentItemId);
                    jsonObject.put("rating", v);
                    StringEntity entity = new StringEntity(jsonObject.toString());
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
                    client.setTimeout(Constant.TIMEOUT);
                    client.post(mContext, URLs.SAVE_RATING, entity, "application/json", new SaveRatingResponseHandler());
                } catch (Exception e) {
                    Utils.exceptionOccurred(mContext, e);
                }
            }
        }
    };

    private class SaveRatingResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                isRatingOrFoodviewChanged = true;
                Toast.makeText(mContext, "Your rating has been saved. Thank you!", Toast.LENGTH_SHORT).show();
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            Utils.responseFailure(mContext);
        }
    }

    private void customizeFoodview() {
        etFoodView.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        int foodviewLength = etFoodView.getText().length();
                        tvTextCount.setText(foodviewLength + "/50-200");
                    }
                };
                handler.postDelayed(runnable, 10);
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        } else {
            try {
                asyncStart();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("merchantId", itemFoodViewDetails.getMerchantId());
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(Constant.TIMEOUT);
                client.post(this, URLs.DISH_SEARCH, entity, "application/json", new DishSearchResponseHandler());
            } catch (Exception e) {
                Utils.exceptionOccurred(mContext, e);
            }
        }
    }

    public class DishSearchResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            DishSearchResponse response = new Gson().fromJson(new String(responseBody), DishSearchResponse.class);
            if (response.isResult()) {
                if (response.getItems().size() > 0) {
                    dishSearchAdapter.addAll(response.getItems());
                } else {
                    Utils.genericErrorToast(mContext, mContext.getString(R.string.noResultsFound));
                }
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            Utils.responseFailure(mContext);
        }
    }

    private void customizeListView() {
        lvDishSearchResults = (ListView) findViewById(R.id.lvDishSearchResults);
        dishSearchAdapter = new DishSearchAdapter(this);
        lvDishSearchResults.setAdapter(dishSearchAdapter);
        lvDishSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                canPerformSearch = false;
                DishSearchItem dish = dishSearchAdapter.getItem(position);
                currentItemId = dish.getId();
                etDishSearch.setText(dish.getName());
                etDishSearch.setSelection(etDishSearch.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                getMyFoodviewDetails();
            }
        });
    }

    private void getMyFoodviewDetails() {
        if(!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        }
        try {
            asyncStart();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", currentItemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(FoodviewActivity.this, URLs.GET_MY_ITEM_FOODVIEW, entity, "application/json", new MyFoodviewResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    private class MyFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            response = new Gson().fromJson(new String(responseBody), MyItemFoodviewResponse.class);
            if (response.isResult()) {
                if (response.isRecommended()) {
                    dishRatingBar.setOnRatingBarChangeListener(null);
                    rating = Float.parseFloat(response.getRecommendation().getRating());
                    dishRatingBar.setRating(rating);
                    if (!Utils.isEmpty(response.getRecommendation().getDescription())) {
                        foodview = response.getRecommendation().getDescription();
                        etFoodView.setText(foodview);
                    } else {
                        foodview = "";
                        etFoodView.setText("");
                    }
                    addDeleteMenu();
                } else {
                    rating = 0.0f;
                    dishRatingBar.setRating(0.0f);
                    foodview = "";
                    etFoodView.setText("");
                    dishRatingBar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                    removeDeleteMenu();
                }
                rateAndReviewLayout.requestFocus();
                lvDishSearchResults.setVisibility(View.GONE);
                rateAndReviewLayout.setVisibility(View.VISIBLE);
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            Utils.responseFailure(mContext);
        }
    }

    private void asyncStart() {
        cancelIcon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        cancelIcon.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    private void setFont() {
        foodViewModalTitle.setTypeface(Utils.getBold(this));
        tvRestName.setTypeface(Utils.getRegularFont(this));
        etDishSearch.setTypeface(Utils.getBold(this));
        etFoodView.setTypeface(Utils.getRegularFont(this));
        tvTextCount.setTypeface(Utils.getRegularFont(this));
    }
}

