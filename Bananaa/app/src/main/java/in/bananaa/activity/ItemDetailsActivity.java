package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.UserFoodviewsListAdapter;
import in.bananaa.object.ItemDetailsResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MyItemFoodviewResponse;
import in.bananaa.object.RatingColorType;
import in.bananaa.object.StatusResponse;
import in.bananaa.object.UserFoodviewsResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static com.bumptech.glide.Glide.with;
import static in.bananaa.utils.Constant.ADD_SCROLL_HEIGHT;
import static in.bananaa.utils.Constant.ITEM_DETAILS_TO_FOODVIEW_REQ_CODE;

public class ItemDetailsActivity extends AppCompatActivity {
    public static final String ITEM_ID = "itemId";

    private Integer itemId;
    private AppCompatActivity mContext;
    private ScrollView itemDetailsView;
    private ProgressBar activityLoader;
    private ItemDetailsResponse itemDetails;

    private ImageView ivBack;
    private ImageView ivShare;
    private ImageView ivImage;
    private ProgressBar pbImageLoader;
    private TextView tvName;
    private TextView tvRestName;
    private TextView tvShortAddress;
    private TextView tvRating;
    private TextView tvTotalRatings;
    private TextView tvCost;
    private TextView tvCostDisclaimer;

    private TextView tvMyFoodViewsTxt;
    private ProgressBar pbFoodviewLoader;
    private LinearLayout llMyFoodview;
    private RatingBar rbMyRatings;
    private TextView tvMyFoodview;
    private TextView tvMyFoodviewTimeDiff;
    private Button btnAddFoodview;
    private TextView tvFoodviewsTxt;

    private TextView tvNoFoodviews;
    private ListView lvFoodviews;

    private UserFoodviewsListAdapter userFoodviewsListAdapter;
    private MyItemFoodviewResponse response;

    int pageFoodviews = 1;
    private boolean moreResultsAvailable = true;
    private boolean canLoadFoodviews = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        itemId = (Integer) getIntent().getSerializableExtra(ITEM_ID);
        if (itemId == null) {
            Utils.genericErrorToast(this, this.getString(R.string.genericError));
            finish();
            return;
        }
        itemDetailsView = (ScrollView) findViewById(R.id.itemDetailsView);
        activityLoader = (ProgressBar) findViewById(R.id.activityLoader);
        getItemDetails();
    }

    private void getItemDetails() {
        if (!Utils.checkIfInternetConnectedAndToast(this)) {
            finish();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("id", itemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.ITEM_DETAILS, entity, "application/json", new DetailsResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class DetailsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            itemDetails = new Gson().fromJson(new String(responseBody), ItemDetailsResponse.class);
            if (itemDetails.isResult()) {
                initializeView();
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

    private void initializeView() {
        setComponents();
        setFonts();
        setItemDetails();
        setMyFoodview();
        setUserFoodviews(1);
        initAutoFoodviewsLoad();
    }

    private void initAutoFoodviewsLoad() {
        itemDetailsView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (itemDetailsView != null) {
                    if (itemDetailsView.getChildAt(0).getBottom() <= (itemDetailsView.getHeight() + ADD_SCROLL_HEIGHT + itemDetailsView.getScrollY())) {
                        if (moreResultsAvailable && canLoadFoodviews) {
                            setUserFoodviews(++pageFoodviews);
                        }
                    }
                }
            }
        });
    }

    private void setComponents() {
        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivShare = (ImageButton) findViewById(R.id.ivShare);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        pbImageLoader = (ProgressBar) findViewById(R.id.pbImageLoader);
        tvName = (TextView) findViewById(R.id.tvName);
        tvRestName = (TextView) findViewById(R.id.tvRestName);
        tvShortAddress = (TextView) findViewById(R.id.tvShortAddress);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvTotalRatings = (TextView) findViewById(R.id.tvTotalRatings);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvCostDisclaimer = (TextView) findViewById(R.id.tvCostDisclaimer);
        tvMyFoodViewsTxt = (TextView) findViewById(R.id.tvMyFoodViewsTxt);
        pbFoodviewLoader = (ProgressBar) findViewById(R.id.pbFoodviewLoader);
        llMyFoodview = (LinearLayout) findViewById(R.id.llMyFoodview);
        rbMyRatings = (RatingBar) findViewById(R.id.rbMyRatings);
        tvMyFoodview = (TextView) findViewById(R.id.tvMyFoodview);
        tvMyFoodviewTimeDiff = (TextView) findViewById(R.id.tvMyFoodviewTimeDiff);
        btnAddFoodview = (Button) findViewById(R.id.btnAddFoodview);
        tvFoodviewsTxt = (TextView) findViewById(R.id.tvFoodviewsTxt);
        tvNoFoodviews = (TextView) findViewById(R.id.tvNoFoodviews);
        lvFoodviews = (ListView) findViewById(R.id.lvFoodviews);

        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.shareToOtherApps(mContext, itemDetails.getItemUrlAbsolute(), true);
            }
        });

        ivImage.setOnClickListener(onImageClickListener);
        ivBack.setOnClickListener(onClickBackListener);
        btnAddFoodview.setOnClickListener(onAddFoodviewClickListener);

        userFoodviewsListAdapter = new UserFoodviewsListAdapter(this);
        lvFoodviews.setAdapter(userFoodviewsListAdapter);

        tvRestName.setOnClickListener(onRestaurantDetailsClickListener);
        tvShortAddress.setOnClickListener(onRestaurantDetailsClickListener);
    }

    View.OnClickListener onClickBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener onImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(ItemDetailsActivity.this, ImageViewActivity.class);
            i.putExtra(ImageViewActivity.IMAGE_URL, itemDetails.getImageUrl());
            startActivity(i);
        }
    };

    private void setItemDetails() {
        setImage();
        tvName.setText(itemDetails.getName());
        tvRestName.setText("At " + itemDetails.getMerchantName());
        tvShortAddress.setText(itemDetails.getShortAddress());
        tvRating.setText(itemDetails.getRating());
        tvTotalRatings.setText(itemDetails.getTotalRatings() + " Ratings");
        if (Utils.isNotEmpty(itemDetails.getCost())) {
            tvCost.setText(getResources().getString(R.string.rupees)+ " " + itemDetails.getCost());
            tvCost.setVisibility(View.VISIBLE);
            tvCostDisclaimer.setVisibility(View.VISIBLE);
        } else {
            tvCost.setVisibility(View.GONE);
            tvCostDisclaimer.setVisibility(View.GONE);
        }
        GradientDrawable background = (GradientDrawable) tvRating.getBackground();
        RatingColorType colorType = RatingColorType.getCodeByCssClass(itemDetails.getRatingClass());
        if (colorType == null) {
            colorType = RatingColorType.R25;
        }
        background.setColor(this.getResources().getColor(colorType.getColor()));
    }

    private void setImage() {
        if (Utils.isNotEmpty(itemDetails.getImageUrl())) {
            with(ItemDetailsActivity.this)
                    .load(itemDetails.getImageUrl())
                    .centerCrop()
                    .placeholder(R.color.lightColor)
                    .crossFade().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    pbImageLoader.setVisibility(View.GONE);
                    return false;
                }
            })
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.color.lightColor);
        }
    }

    private void setMyFoodview() {
        if(!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", itemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.GET_MY_ITEM_FOODVIEW, entity, "application/json", new MyItemFoodviewResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class MyItemFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            response = new Gson().fromJson(new String(responseBody), MyItemFoodviewResponse.class);
            if (response.isResult()) {
                pbFoodviewLoader.setVisibility(View.GONE);
                llMyFoodview.setVisibility(View.VISIBLE);
                if (response.isRecommended()) {
                    rbMyRatings.setOnRatingBarChangeListener(null);
                    rbMyRatings.setRating(Float.parseFloat(response.getRecommendation().getRating()));
                    tvMyFoodviewTimeDiff.setText(response.getRecommendation().getTimeDiff());
                    tvMyFoodviewTimeDiff.setVisibility(View.VISIBLE);
                    if (!Utils.isEmpty(response.getRecommendation().getDescription())) {
                        tvMyFoodview.setText(response.getRecommendation().getDescription());
                        tvMyFoodview.setVisibility(View.VISIBLE);
                        btnAddFoodview.setText(R.string.editFoodview);
                    } else {
                        tvMyFoodview.setVisibility(View.GONE);
                        btnAddFoodview.setText(R.string.addFoodview);
                    }
                } else {
                    rbMyRatings.setRating(0.0f);
                    tvMyFoodview.setVisibility(View.GONE);
                    tvMyFoodviewTimeDiff.setVisibility(View.GONE);
                    btnAddFoodview.setText(R.string.addFoodview);
                }
                rbMyRatings.setOnRatingBarChangeListener(onRatingBarChangeListener);
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    RatingBar.OnRatingBarChangeListener onRatingBarChangeListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
            if (v == 0.0f) {
                return;
            }
            if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
                return;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", itemId);
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
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                Toast.makeText(mContext, "Your rating has been saved. Thank you!", Toast.LENGTH_SHORT).show();
                tvMyFoodviewTimeDiff.setText(mContext.getString(R.string.justNow));
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    private void setUserFoodviews(Integer page) {
        if (!Utils.isInternetConnected(mContext)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", itemId);
            jsonObject.put("page", page);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.GET_OTHER_USER_FOODVIEWS, entity, "application/json", new UserFoodviewsResponseHandler());
            canLoadFoodviews = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class UserFoodviewsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            UserFoodviewsResponse response = new Gson().fromJson(new String(responseBody), UserFoodviewsResponse.class);
            if (response.isResult()) {
                if (response.getFoodviews().size() == 0) {
                    if (pageFoodviews == 1) {
                        tvNoFoodviews.setVisibility(View.VISIBLE);
                        lvFoodviews.setVisibility(View.GONE);
                    }
                    moreResultsAvailable = false;
                } else {
                    tvNoFoodviews.setVisibility(View.GONE);
                    lvFoodviews.setVisibility(View.VISIBLE);
                    userFoodviewsListAdapter.appendAll(response.getFoodviews());
                    canLoadFoodviews = true;
                    moreResultsAvailable = true;
                }
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    private void setFonts() {
        tvName.setTypeface(Utils.getBold(this));
        tvRestName.setTypeface(Utils.getRegularFont(this));
        tvShortAddress.setTypeface(Utils.getRegularFont(this));
        tvRating.setTypeface(Utils.getRegularFont(this));
        tvTotalRatings.setTypeface(Utils.getRegularFont(this));
        tvCost.setTypeface(Utils.getRegularFont(this));
        tvCostDisclaimer.setTypeface(Utils.getRegularFont(this));
        tvMyFoodViewsTxt.setTypeface(Utils.getBold(this));
        tvMyFoodview.setTypeface(Utils.getBold(this));
        tvMyFoodviewTimeDiff.setTypeface(Utils.getRegularFont(this));
        btnAddFoodview.setTypeface(Utils.getRegularFont(this));
        tvFoodviewsTxt.setTypeface(Utils.getBold(this));
        tvNoFoodviews.setTypeface(Utils.getRegularFont(this));
    }

    private void asyncStart() {
        itemDetailsView.setVisibility(View.GONE);
        activityLoader.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        itemDetailsView.setVisibility(View.VISIBLE);
        activityLoader.setVisibility(View.GONE);
    }

    View.OnClickListener onAddFoodviewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ItemFoodViewDetails itemFoodViewDetails = new ItemFoodViewDetails(itemDetails.getId(),
                    itemDetails.getMerchantId(), itemDetails.getMerchantName(),
                    itemDetails.getShortAddress(), itemDetails.getName(), true);
            Intent i = new Intent(ItemDetailsActivity.this, FoodviewActivity.class);
            i.putExtra(FoodviewActivity.FOODVIEW_DETAILS, itemFoodViewDetails);
            startActivityForResult(i, ITEM_DETAILS_TO_FOODVIEW_REQ_CODE);
        }
    };

    View.OnClickListener onRestaurantDetailsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(ItemDetailsActivity.this, MerchantDetailsActivity.class);
            i.putExtra(MerchantDetailsActivity.MERCHANT_ID, itemDetails.getMerchantId());
            startActivity(i);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ITEM_DETAILS_TO_FOODVIEW_REQ_CODE && resultCode == Activity.RESULT_OK) {
            setMyFoodview();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
