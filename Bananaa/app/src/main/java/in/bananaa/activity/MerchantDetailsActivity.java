package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import in.bananaa.adapter.ItemListAdapter;
import in.bananaa.adapter.MyFoodviewsListAdapter;
import in.bananaa.adapter.TagListAdapter;
import in.bananaa.object.FoodviewsResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MerchantDetailsResponse;
import in.bananaa.object.PopularItemsResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.CustomListView;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static com.bumptech.glide.Glide.with;
import static in.bananaa.utils.Constant.ADD_SCROLL_HEIGHT;
import static in.bananaa.utils.Constant.MERCHANT_DETAILS_TO_FOODVIEW_REQ_CODE;

public class MerchantDetailsActivity extends AppCompatActivity {
    public static final String MERCHANT_ID = "merchantId";
    Integer merchantId;
    MerchantDetailsResponse merchantDetails;

    ScrollView merchantDetailsView;
    ProgressBar activityLoader;

    ImageView ivImage;
    ProgressBar pbImageLoader;
    ImageButton ivShare;
    ImageButton ivBack;
    TextView tvName;
    TextView tvShortAddress;
    TextView tvHours;
    TextView tvHoursTxt;
    TextView tvPhone;
    TextView tvPhoneTxt;
    TextView tvAverageCost;
    TextView tvAverageCostTxt;
    TextView tvType;
    TextView tvTypeTxt;
    TextView tvLongAddress;
    TextView tvLongAddressTxt;
    TextView tvCuisinesAndSpreadTxt;
    TextView tvDelectableDishesTxt;
    CustomListView lvDelectableDishes;
    CustomListView lvCuisinesAndSpread;
    CustomListView lvMyFoodViews;

    ProgressBar pbSeeMore;
    AppCompatButton btnSeeMore;
    LinearLayout seeMoreSection;

    TextView tvMyFoodViewsTxt;
    TextView tvFoodviewSubHeading;
    AppCompatButton btnAddFoodview;

    ItemListAdapter itemListAdapter;
    TagListAdapter cuisinesListAdapter;
    MyFoodviewsListAdapter myFoodviewsListAdapter;

    AppCompatActivity mContext;

    int pageFoodviews = 1;
    private boolean moreResultsAvailable = true;
    private boolean canLoadFoodviews = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_merchant_details);
        merchantId = (Integer) getIntent().getSerializableExtra(MERCHANT_ID);
        if (merchantId == null) {
            Utils.genericErrorToast(this, this.getString(R.string.genericError));
            finish();
            return;
        }
        merchantDetailsView = (ScrollView) findViewById(R.id.merchantDetailsView);
        activityLoader = (ProgressBar) findViewById(R.id.activityLoader);
        getMerchantDetails();
    }

    private void getMerchantDetails() {
        if (!Utils.checkIfInternetConnectedAndToast(this)) {
            finish();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("id", merchantId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(MerchantDetailsActivity.this, URLs.MERCHANT_DETAILS, entity, "application/json", new DetailsResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class DetailsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            merchantDetails = new Gson().fromJson(new String(responseBody), MerchantDetailsResponse.class);
            if (merchantDetails.isResult()) {
                initializeView();
            } else {
                Utils.responseError(mContext, merchantDetails);
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
        setFont();
        setMerchantDetails();
        asyncEnd();
    }

    private void setComponents() {
        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivShare = (ImageButton) findViewById(R.id.ivShare);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        pbImageLoader = (ProgressBar) findViewById(R.id.pbImageLoader);
        tvName = (TextView) findViewById(R.id.tvName);
        tvShortAddress = (TextView) findViewById(R.id.tvShortAddress);
        tvHours = (TextView) findViewById(R.id.tvHours);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        tvAverageCost = (TextView) findViewById(R.id.tvAverageCost);
        tvType = (TextView) findViewById(R.id.tvType);
        tvLongAddress = (TextView) findViewById(R.id.tvLongAddress);
        tvHoursTxt = (TextView) findViewById(R.id.tvHoursTxt);
        tvPhoneTxt = (TextView) findViewById(R.id.tvPhoneTxt);
        tvAverageCostTxt = (TextView) findViewById(R.id.tvAverageCostTxt);
        tvTypeTxt = (TextView) findViewById(R.id.tvTypeTxt);
        tvLongAddressTxt = (TextView) findViewById(R.id.tvLongAddressTxt);
        tvCuisinesAndSpreadTxt = (TextView) findViewById(R.id.tvCuisinesAndSpreadTxt);
        tvDelectableDishesTxt = (TextView) findViewById(R.id.tvDelectableDishesTxt);
        lvCuisinesAndSpread = (CustomListView) findViewById(R.id.lvCuisinesAndSpread);
        lvDelectableDishes = (CustomListView) findViewById(R.id.lvDelectableDishes);
        seeMoreSection = (LinearLayout) findViewById(R.id.seeMoreSection);
        pbSeeMore = (ProgressBar) findViewById(R.id.pbSeeMore);
        btnSeeMore = (AppCompatButton) findViewById(R.id.btnSeeMore);
        tvMyFoodViewsTxt = (TextView) findViewById(R.id.tvMyFoodViewsTxt);
        tvFoodviewSubHeading = (TextView) findViewById(R.id.tvFoodviewSubHeading);
        lvMyFoodViews = (CustomListView) findViewById(R.id.lvMyFoodviews);
        btnAddFoodview = (AppCompatButton) findViewById(R.id.btnAddFoodview);

        itemListAdapter = new ItemListAdapter(this, merchantDetails);
        itemListAdapter.addAll(merchantDetails.getItems());
        cuisinesListAdapter = new TagListAdapter(this);
        cuisinesListAdapter.addAll(merchantDetails.getRatedCuisines());
        myFoodviewsListAdapter = new MyFoodviewsListAdapter(this, merchantId, merchantDetails.getName(), merchantDetails.getShortAddress());

        lvCuisinesAndSpread.setAdapter(cuisinesListAdapter);
        lvDelectableDishes.setAdapter(itemListAdapter);
        lvMyFoodViews.setAdapter(myFoodviewsListAdapter);

        ivImage.setOnClickListener(onImageClickListener);
        tvPhone.setOnClickListener(onPhoneNumberClickListener);
        btnSeeMore.setOnClickListener(onClickSeeMoreListner);
        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.shareToOtherApps(mContext, merchantDetails.getMerchantUrlAbsolute(), true);
            }
        });
        ivBack.setOnClickListener(onClickBackListener);
        btnAddFoodview.setOnClickListener(onClickRateAndFoodViewListener);
        setToastMessages();
        getMyFoodviews(pageFoodviews, true);
        initAutoFoodviewsLoad();
    }

    private void initAutoFoodviewsLoad() {
        merchantDetailsView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (merchantDetailsView != null) {
                    if (merchantDetailsView.getChildAt(0).getBottom() <= (merchantDetailsView.getHeight() + ADD_SCROLL_HEIGHT + merchantDetailsView.getScrollY())) {
                        if (moreResultsAvailable && canLoadFoodviews) {
                            getMyFoodviews(++pageFoodviews, false);
                        }
                    }
                }
            }
        });
    }

    View.OnClickListener onPhoneNumberClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tvPhone.getText()));
            startActivity(intent);
        }
    };

    View.OnClickListener onImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MerchantDetailsActivity.this, ImageViewActivity.class);
            i.putExtra(ImageViewActivity.IMAGE_URL, merchantDetails.getImageUrl());
            startActivity(i);
        }
    };

    View.OnClickListener onClickSeeMoreListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
                return;
            }
            try {
                pbSeeMore.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", merchantId);
                jsonObject.put("page", 1);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(Constant.TIMEOUT);
                client.post(MerchantDetailsActivity.this, URLs.GET_POPULAR_ITEMS, entity, "application/json", new PopularItemsResponseHandler());
                canLoadFoodviews = false;
            } catch (Exception e) {
                Utils.exceptionOccurred(mContext, e);
            }
        }
    };

    private class PopularItemsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            PopularItemsResponse response = new Gson().fromJson(new String(responseBody), PopularItemsResponse.class);
            if (response.isResult()) {
                itemListAdapter.addAll(response.getItems());
                seeMoreSection.setVisibility(View.GONE);
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    View.OnClickListener onClickBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener onClickRateAndFoodViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ItemFoodViewDetails itemFoodViewDetails =
                    new ItemFoodViewDetails(null, merchantId, merchantDetails.getName(),
                            merchantDetails.getShortAddress(), null, false);
            Intent i = new Intent(MerchantDetailsActivity.this, FoodviewActivity.class);
            i.putExtra(FoodviewActivity.FOODVIEW_DETAILS, itemFoodViewDetails);
            startActivityForResult(i, MERCHANT_DETAILS_TO_FOODVIEW_REQ_CODE);
        }
    };

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == MERCHANT_DETAILS_TO_FOODVIEW_REQ_CODE && resultCode == Activity.RESULT_OK) {
            boolean value = data.getBooleanExtra(FoodviewActivity.RELOAD_FOODVIEWS, Boolean.FALSE);
            if (value) {
                pageFoodviews = 1;
                getMyFoodviews(pageFoodviews, true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setMerchantDetails() {
        setImage();
        tvName.setText(merchantDetails.getName());
        tvShortAddress.setText(merchantDetails.getShortAddress());
        tvHours.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getOpeningHours()));
        tvPhone.setText(merchantDetails.getPhone());
        tvAverageCost.setText(getResources().getString(R.string.rupees)+ " " + merchantDetails.getAverageCost());
        tvType.setText(Utils.parseListToCommaSeparatedString(merchantDetails.getType()));
        tvLongAddress.setText(merchantDetails.getLongAddress());
        tvFoodviewSubHeading.setText(getResources().getString(R.string.foodViewSubHeading) + merchantDetails.getName());
    }

    private void setImage() {
        if (Utils.isNotEmpty(merchantDetails.getImageUrl())) {
            with(MerchantDetailsActivity.this)
                    .load(merchantDetails.getImageUrl())
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
            }).into(ivImage);
        } else {
            ivImage.setImageResource(R.color.lightColor);
        }
    }

    private void setFont() {
        tvName.setTypeface(Utils.getBold(this));
        tvShortAddress.setTypeface(Utils.getRegularFont(this));
        tvHours.setTypeface(Utils.getRegularFont(this));
        tvPhone.setTypeface(Utils.getRegularFont(this));
        tvAverageCost.setTypeface(Utils.getRegularFont(this));
        tvType.setTypeface(Utils.getRegularFont(this));
        tvLongAddress.setTypeface(Utils.getRegularFont(this));
        tvHoursTxt.setTypeface(Utils.getRegularFont(this));
        tvPhoneTxt.setTypeface(Utils.getRegularFont(this));
        tvAverageCostTxt.setTypeface(Utils.getRegularFont(this));
        tvTypeTxt.setTypeface(Utils.getRegularFont(this));
        tvLongAddressTxt.setTypeface(Utils.getRegularFont(this));
        tvDelectableDishesTxt.setTypeface(Utils.getBold(this));
        tvCuisinesAndSpreadTxt.setTypeface(Utils.getBold(this));
        tvMyFoodViewsTxt.setTypeface(Utils.getBold(this));
        btnSeeMore.setTypeface(Utils.getRegularFont(this));
        tvFoodviewSubHeading.setTypeface(Utils.getRegularFont(this));
        btnAddFoodview.setTypeface(Utils.getRegularFont(this));
    }

    private void asyncStart() {
        merchantDetailsView.setVisibility(View.GONE);
        activityLoader.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        merchantDetailsView.setVisibility(View.VISIBLE);
        activityLoader.setVisibility(View.GONE);
    }

    private void setToastMessages() {
        tvDelectableDishesTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= tvDelectableDishesTxt.getRight() - tvDelectableDishesTxt.getTotalPaddingRight()) {
                        showToast(false);
                    }
                }
                return true;
            }
        });

        tvCuisinesAndSpreadTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= tvCuisinesAndSpreadTxt.getRight() - tvCuisinesAndSpreadTxt.getTotalPaddingRight()) {
                        showToast(true);
                    }
                }
                return true;
            }
        });
    }

    private void showToast(boolean isCuisineToast) {
        if (isCuisineToast) {
            Toast.makeText(this,
                    "Rated based on weighted average of most recent dish ratings and the number of dishes served per cuisine.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Rated b" +
                            "ased on weighted average of most recent ratings, user credibility and number of times people have tried a dish.", Toast.LENGTH_LONG).show();
        }
    }

    private void getMyFoodviews(Integer page, boolean replaceResults) {
        if (!Utils.isInternetConnected(mContext)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("merchantId", merchantId);
            jsonObject.put("page", page);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(MerchantDetailsActivity.this, URLs.GET_MY_FOODVIEWS, entity, "application/json", new FoodviewResponseHandler(replaceResults));
            canLoadFoodviews = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class FoodviewResponseHandler extends AsyncHttpResponseHandler {
        private boolean replaceResults;

        public FoodviewResponseHandler(boolean replaceResults) {
            this.replaceResults = replaceResults;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            FoodviewsResponse response = new Gson().fromJson(new String(responseBody), FoodviewsResponse.class);
            if (response.isResult()) {
                if (response.getRecommendations().size() > 0) {
                    if (replaceResults) {
                        myFoodviewsListAdapter.addAll(response.getRecommendations());
                    } else {
                        myFoodviewsListAdapter.appendAll(response.getRecommendations());
                    }
                    canLoadFoodviews = true;
                    moreResultsAvailable = true;
                } else {
                    moreResultsAvailable = false;
                    if (replaceResults) {
                        myFoodviewsListAdapter.addAll(response.getRecommendations());
                    }
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
}
