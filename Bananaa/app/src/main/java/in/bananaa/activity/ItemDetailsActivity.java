package in.bananaa.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.UserFoodviewsListAdapter;
import in.bananaa.object.ItemDetailsResponse;
import in.bananaa.object.ItemFoodViewDetails;
import in.bananaa.object.MyItemFoodviewResponse;
import in.bananaa.object.RatingColorType;
import in.bananaa.object.UserFoodviewsResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static com.bumptech.glide.Glide.with;

public class ItemDetailsActivity extends AppCompatActivity {
    public static final String ITEM_ID = "itemId";
    Integer itemId;
    AppCompatActivity mContext;
    ScrollView itemDetailsView;
    ProgressBar activityLoader;
    ItemDetailsResponse itemDetails;

    ImageView ivBack;
    ImageView ivShare;
    ImageView ivImage;
    TextView tvName;
    TextView tvRestName;
    TextView tvShortAddress;
    TextView tvRating;
    TextView tvTotalRatings;

    TextView tvMyFoodViewsTxt;
    RatingBar rbMyRatings;
    TextView tvMyFoodview;
    TextView tvMyFoodviewTimeDiff;
    Button btnAddFoodview;
    TextView tvFoodviewsTxt;

    TextView tvNoFoodviews;
    ListView lvFoodviews;

    UserFoodviewsListAdapter userFoodviewsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        itemId = (Integer) getIntent().getSerializableExtra(ITEM_ID);
        if (itemId == null) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            return;
        }
        itemDetailsView = (ScrollView) findViewById(R.id.itemDetailsView);
        activityLoader = (ProgressBar) findViewById(R.id.activityLoader);
        getItemDetails();
    }

    private void getItemDetails() {
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("id", itemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.ITEM_DETAILS, entity, "application/json", new DetailsResponseHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
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
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd();
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void initializeView() {
        setComponents();
        setFonts();
        setItemDetails();
        setMyFoodview();
        setUserFoodviews();
    }

    private void setComponents() {
        ivBack = (ImageButton) findViewById(R.id.ivBack);
        ivShare = (ImageButton) findViewById(R.id.ivShare);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvName = (TextView) findViewById(R.id.tvName);
        tvRestName = (TextView) findViewById(R.id.tvRestName);
        tvShortAddress = (TextView) findViewById(R.id.tvShortAddress);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvTotalRatings = (TextView) findViewById(R.id.tvTotalRatings);
        tvMyFoodViewsTxt = (TextView) findViewById(R.id.tvMyFoodViewsTxt);
        rbMyRatings = (RatingBar) findViewById(R.id.rbMyRatings);
        tvMyFoodview = (TextView) findViewById(R.id.tvMyFoodview);
        tvMyFoodviewTimeDiff = (TextView) findViewById(R.id.tvMyFoodviewTimeDiff);
        btnAddFoodview = (Button) findViewById(R.id.btnAddFoodview);
        tvFoodviewsTxt = (TextView) findViewById(R.id.tvFoodviewsTxt);
        tvNoFoodviews = (TextView) findViewById(R.id.tvNoFoodviews);
        lvFoodviews = (ListView) findViewById(R.id.lvFoodviews);

        ivImage.setOnClickListener(onImageClickListener);
        ivBack.setOnClickListener(onClickBackListener);
        btnAddFoodview.setOnClickListener(onAddFoodviewClickListener);

        userFoodviewsListAdapter = new UserFoodviewsListAdapter(this);
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
                    .placeholder(R.color.grey)
                    .crossFade()
                    .into(ivImage);
        } else {
            ivImage.setImageResource(R.color.lightColor);
        }
    }

    private void setMyFoodview() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", itemId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.GET_MY_ITEM_FOODVIEW, entity, "application/json", new MyItemFoodviewResponseHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    public class MyItemFoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            MyItemFoodviewResponse response = new Gson().fromJson(new String(responseBody), MyItemFoodviewResponse.class);
            if (response.isResult()) {
                if (response.isRecommended()) {
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
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void setUserFoodviews() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("itemId", itemId);
            jsonObject.put("page", 1);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(ItemDetailsActivity.this, URLs.GET_OTHER_USER_FOODVIEWS, entity, "application/json", new UserFoodviewsResponseHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    public class UserFoodviewsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            UserFoodviewsResponse response = new Gson().fromJson(new String(responseBody), UserFoodviewsResponse.class);
            if (response.isResult()) {
                if (response.getFoodviews().size() == 0) {
                    tvNoFoodviews.setVisibility(View.VISIBLE);
                    lvFoodviews.setVisibility(View.GONE);
                } else {
                    tvNoFoodviews.setVisibility(View.GONE);
                    lvFoodviews.setVisibility(View.VISIBLE);
                    userFoodviewsListAdapter.addAll(response.getFoodviews());
                    lvFoodviews.setAdapter(userFoodviewsListAdapter);
                }
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void setFonts() {
        tvName.setTypeface(Utils.getBold(this));
        tvRestName.setTypeface(Utils.getRegularFont(this));
        tvShortAddress.setTypeface(Utils.getRegularFont(this));
        tvRating.setTypeface(Utils.getRegularFont(this));
        tvTotalRatings.setTypeface(Utils.getRegularFont(this));
        tvMyFoodViewsTxt.setTypeface(Utils.getBold(this));
        tvMyFoodview.setTypeface(Utils.getRegularFont(this));
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
            ItemFoodViewDetails itemFoodViewDetails = new ItemFoodViewDetails(null, itemDetails.getId(), itemDetails.getMerchantName(),
                    itemDetails.getShortAddress(), itemDetails.getName(), null, 0.0f, false);
            Intent i = new Intent(ItemDetailsActivity.this, FoodviewActivity.class);
            i.putExtra(FoodviewActivity.FOODVIEW_DETAILS, itemFoodViewDetails);
            startActivity(i);
        }
    };
}
