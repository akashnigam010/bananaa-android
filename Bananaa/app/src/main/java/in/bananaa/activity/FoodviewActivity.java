package in.bananaa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
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

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.DishSearchAdapter;
import in.bananaa.object.DishSearchItem;
import in.bananaa.object.DishSearchResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MyItemFoodviewResponse;
import in.bananaa.object.StatusResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class FoodviewActivity extends AppCompatActivity {
    public final static String FOODVIEW_DETAILS = "foodviewDetails";
    public static final String RELOAD_FOODVIEWS = "reloadFoodviews";
    ItemFoodViewDetails itemFoodViewDetails;
    Integer currentItemId;
    AppCompatActivity mContext;

    TextView foodViewModalTitle;
    TextView tvRestName;
    EditText etDishSearch;
    EditText etFoodView;
    TextView tvTextCount;
    ProgressBar progress;
    ImageView cancelIcon;

    ListView lvDishSearchResults;
    LinearLayout rateAndReviewLayout;
    RatingBar dishRatingBar;
    DishSearchAdapter dishSearchAdapter;

    boolean itemNotSelected = true;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foodview_modal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_save) {
            if (dishRatingBar.getRating() == 0.0f) {
                Toast.makeText(mContext, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (etFoodView.getText().length() < 50) {
                Toast.makeText(mContext, "Foodview must be minimum 50 characters long.", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (!Utils.isInternetConnected(mContext)) {
                AlertMessages.noInternet(mContext);
                return false;
            } else {
                try {
                    asyncStart();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", currentItemId);
                    jsonObject.put("description", etFoodView.getText());
                    StringEntity entity = new StringEntity(jsonObject.toString());
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
                    client.setTimeout(Constant.TIMEOUT);
                    client.post(mContext, URLs.SAVE_FOODVIEW, entity, "application/json", new SaveFoodviewResponseHandler());
                } catch (UnsupportedEncodingException e) {
                    AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                    e.printStackTrace();
                } catch (Exception e) {
                    AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                    e.printStackTrace();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                Toast.makeText(mContext, "Your foodview has been saved.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                intent.putExtra(RELOAD_FOODVIEWS, true);
                setResult(1, intent);
                finish();
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void setItemDetails() {
        if (!itemFoodViewDetails.isNewFoodview()) {
            itemNotSelected = false;
            etDishSearch.setText(itemFoodViewDetails.getItemName());
            etDishSearch.setSelection(etDishSearch.getText().length());
            getMyFoodviewDetails();
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
                        if (s.toString().length() >= 2 && itemNotSelected) {
                            lvDishSearchResults.setVisibility(View.VISIBLE);
                            rateAndReviewLayout.setVisibility(View.GONE);
                            doSearch(s.toString());
                        } else {
                            itemNotSelected = true;
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
                return;
            }
            if (!Utils.isInternetConnected(mContext)) {
                AlertMessages.noInternet(mContext);
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
                } catch (UnsupportedEncodingException e) {
                    AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                    e.printStackTrace();
                } catch (Exception e) {
                    AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                    e.printStackTrace();
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
                Toast.makeText(mContext, "Your rating has been saved. Thank you!", Toast.LENGTH_SHORT).show();
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
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
        if (!Utils.isInternetConnected(this)) {
            AlertMessages.noInternet(this);
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
            } catch (UnsupportedEncodingException e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                e.printStackTrace();
            } catch (Exception e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                e.printStackTrace();
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
                    AlertMessages.showError(mContext, mContext.getString(R.string.noResultsFound));
                }
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void customizeListView() {
        lvDishSearchResults = (ListView) findViewById(R.id.lvDishSearchResults);
        dishSearchAdapter = new DishSearchAdapter(this);
        lvDishSearchResults.setAdapter(dishSearchAdapter);
        lvDishSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemNotSelected = false;
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
        try {
            asyncStart();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", currentItemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(FoodviewActivity.this, URLs.GET_MY_ITEM_FOODVIEW, entity, "application/json", new MyFoodviewResponseHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private class MyFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            MyItemFoodviewResponse response = new Gson().fromJson(new String(responseBody), MyItemFoodviewResponse.class);
            if (response.isResult()) {
                dishRatingBar.setOnRatingBarChangeListener(null);
                if (response.isRecommended()) {
                    dishRatingBar.setRating(Float.parseFloat(response.getRecommendation().getRating()));
                    if (!Utils.isEmpty(response.getRecommendation().getDescription())) {
                        etFoodView.setText(response.getRecommendation().getDescription());
                    } else {
                        etFoodView.setText("");
                    }
                } else {
                    dishRatingBar.setRating(0.0f);
                    etFoodView.setText("");
                }
                dishRatingBar.setOnRatingBarChangeListener(onRatingBarChangeListener);
                rateAndReviewLayout.requestFocus();
                lvDishSearchResults.setVisibility(View.GONE);
                rateAndReviewLayout.setVisibility(View.VISIBLE);
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
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

