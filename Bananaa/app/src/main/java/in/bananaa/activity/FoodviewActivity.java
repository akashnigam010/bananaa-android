package in.bananaa.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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
import in.bananaa.object.DishSearchResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class FoodviewActivity extends AppCompatActivity {
    public final static String FOODVIEW_DETAILS = "foodviewDetails";
    AppCompatActivity mContext;
    boolean itemNotSelected = true;
    boolean isRatingLoadedFromFoodview = false;

    AlertMessages messages;
    TextView foodViewModalTitle;
    TextView tvRestName;
    ItemFoodViewDetails itemFoodViewDetails;
    EditText etDishSearch;
    EditText etFoodView;
    TextView tvTextCount;
    ProgressBar progress;
    ImageView cancelIcon;

    ListView lvDishSearchResults;
    LinearLayout rateAndReviewLayout;
    RatingBar dishRatingBar;
    DishSearchAdapter dishSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodview);
        itemFoodViewDetails = (ItemFoodViewDetails) getIntent().getSerializableExtra(FOODVIEW_DETAILS);
        initializeView();
        setDetailsIfExistingFoodview();
    }

    private void initializeView() {
        customizeToolbar();
        messages = new AlertMessages(this);
        dishSearchAdapter = new DishSearchAdapter(this);
        tvRestName = (TextView) findViewById(R.id.tvRestName);
        customizeListView();
        rateAndReviewLayout = (LinearLayout) findViewById(R.id.rateAndReviewLayout);
        dishRatingBar = (RatingBar) findViewById(R.id.dishRatingBar);
        etDishSearch = (EditText) findViewById(R.id.etDishSearch);
        cancelIcon = (ImageView) findViewById(R.id.dishCancelButton);
        progress = (ProgressBar) findViewById(R.id.dishSearchLoader);
        etFoodView = (EditText) findViewById(R.id.etFoodView);
        tvTextCount = (TextView) findViewById(R.id.tvTextCount);
        dishRatingBar.setOnRatingBarChangeListener(onRatingBarChangeListener);

        tvRestName.setText("At " + itemFoodViewDetails.getRestName() + ", " + itemFoodViewDetails.getLocality());
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setDetailsIfExistingFoodview() {
        if (itemFoodViewDetails.isBlankFoodview()) {
            lvDishSearchResults.setVisibility(View.VISIBLE);
            rateAndReviewLayout.setVisibility(View.GONE);
        } else {
            itemNotSelected = false;
            etDishSearch.setText(itemFoodViewDetails.getItemName());
            lvDishSearchResults.setVisibility(View.GONE);
            rateAndReviewLayout.setVisibility(View.VISIBLE);
            etFoodView.setText(itemFoodViewDetails.getDesc());
            if (itemFoodViewDetails.getRating() == 0.0f) {
                isRatingLoadedFromFoodview = false;
            } else {
                isRatingLoadedFromFoodview = true;
            }
            dishRatingBar.setRating(itemFoodViewDetails.getRating());
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
                            isRatingLoadedFromFoodview = false;
                            dishRatingBar.setRating(0.0f);
                            etFoodView.setText("");
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
            if (!isRatingLoadedFromFoodview) {
                Toast.makeText(mContext, "Your rating has been saved. Thank you!", Toast.LENGTH_SHORT).show();
            } else {
                isRatingLoadedFromFoodview = false;
            }
        }
    };

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
                        tvTextCount.setText(foodviewLength + "/200");
                        if (foodviewLength <= 0) {
                            //menu.getItem(0).setVisible(false);
                        } else {
                            //menu.getItem(0).setVisible(true);
                        }
                    }
                };
                handler.postDelayed(runnable, 10);
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.isInternetConnected(this)) {
            messages.showCustomMessage("No Internet Connection");
            return;
        } else {
            try {
                asyncStart();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("merchantId", 1);
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                //client.addHeader("Authorization", "Bearer " + PreferenceManager.getToken());
                client.setTimeout(Constant.TIMEOUT);
                client.post(this, URLs.DISH_SEARCH, entity, "application/json", new DishSearchResponseHandler());
            } catch (UnsupportedEncodingException e) {
                messages.showCustomMessage("Something seems fishy! Please try again");
                e.printStackTrace();
            } catch (Exception e) {
                messages.showCustomMessage("Something seems fishy! Please try again");
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
                if (response.getItems() != null) {
                    // remove below extra logic to handle no results found
                    if (response.getItems().size() == 1) {
                        if (response.getItems().get(0).getId() != -999){
                            dishSearchAdapter.addAll(response.getItems());
                        } else {
                            messages.showCustomMessage("No results found");
                        }
                    } else {
                        dishSearchAdapter.addAll(response.getItems());
                    }

                } else {
                    messages.showCustomMessage("No results found");
                }
            } else {
                messages.showCustomMessage("Something seems fishy! Please try after some time.");
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            //messages.showCustomMessage("Something seems fishy! Please try after some time.");
        }
    }

    private void customizeListView() {
        lvDishSearchResults = (ListView) findViewById(R.id.lvDishSearchResults);
        lvDishSearchResults.setAdapter(dishSearchAdapter);
        lvDishSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemNotSelected = false;
                etDishSearch.setText(dishSearchAdapter.getItem(position).getName());
                etDishSearch.setSelection(etDishSearch.getText().length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                rateAndReviewLayout.requestFocus();
                lvDishSearchResults.setVisibility(View.GONE);
                rateAndReviewLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void asyncStart() {
        cancelIcon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        lvDishSearchResults.setVisibility(View.VISIBLE);
        rateAndReviewLayout.setVisibility(View.GONE);
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

