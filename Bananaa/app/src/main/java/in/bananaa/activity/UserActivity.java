package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.RatingsAndFoodviewsListAdapter;
import in.bananaa.object.FoodviewsResponse;
import in.bananaa.object.Profile;
import in.bananaa.object.ProfileResponse;
import in.bananaa.object.login.LoginUserDto;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Constant.ADD_SCROLL_HEIGHT;
import static in.bananaa.utils.Constant.USER_TO_EDIT_PROFILE;
import static in.bananaa.utils.Constant.USER_TO_PREF_REQ_CODE;

public class UserActivity extends AppCompatActivity {
    public static final String USER_ID = "userId";
    private ProgressBar pbUserProfile;
    private ScrollView svUserProfile;
    private ImageView ivBack;
    private TextView tvFoodbookTxt;
    private ImageView ivImage;
    private TextView tvName;
    private ImageView ivEdit;
    private TextView tvLevel;
    private TextView tvRatingCount;
    private TextView tvFoodviewCount;
    private TextView tvStatus;
    CardView cvCuisinesAndDishesPrefs;
    private TextView tvFavouriteCuisinesTxt;
    private TextView tvFavouriteCuisines;
    private TextView tvFavouriteDishesTxt;
    private TextView tvFavouriteDishes;
    private AppCompatButton editPreferences;
    private TextView tvRatingsAndFoodviewsTxt;
    private ListView lvFoodviews;

    private AppCompatActivity mContext;
    private Integer userId;
    private Profile userProfile;

    private RatingsAndFoodviewsListAdapter ratingsAndFoodviewsListAdapter;

    int pageFoodviews = 1;
    private boolean moreResultsAvailable = true;
    private boolean canLoadFoodviews = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        userId = (Integer) getIntent().getSerializableExtra(USER_ID);
        if (userId == null) {
            Utils.genericErrorToast(this, this.getString(R.string.genericError));
            finish();
            return;
        }
        pbUserProfile = (ProgressBar) findViewById(R.id.pbUserProfile);
        svUserProfile = (ScrollView) findViewById(R.id.svUserProfile);
        getUserProfile();
    }

    private void getUserProfile() {
        Utils.checkInternetConnectionRollBack(this);
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("id", userId);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(UserActivity.this, URLs.GET_PROFILE, entity, "application/json", new ProfileResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(this, e);
        }
    }

    public class ProfileResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            ProfileResponse response = new Gson().fromJson(new String(responseBody), ProfileResponse.class);
            if (response.isResult()) {
                userProfile = response.getProfile();
                initiateView();
                new updateUserDetailsInDevice().execute();
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

    private void initiateView() {
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvFoodbookTxt = (TextView) findViewById(R.id.tvFoodbookTxt);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        tvName = (TextView) findViewById(R.id.tvName);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        tvRatingCount = (TextView) findViewById(R.id.tvRatingCount);
        tvFoodviewCount = (TextView) findViewById(R.id.tvFoodviewCount);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvFavouriteCuisinesTxt = (TextView) findViewById(R.id.tvFavouriteCuisinesTxt);
        tvFavouriteCuisines = (TextView) findViewById(R.id.tvFavouriteCuisines);
        tvFavouriteDishesTxt = (TextView) findViewById(R.id.tvFavouriteDishesTxt);
        tvFavouriteDishes = (TextView) findViewById(R.id.tvFavouriteDishes);
        editPreferences = (AppCompatButton) findViewById(R.id.editPreferences);
        tvRatingsAndFoodviewsTxt = (TextView) findViewById(R.id.tvRatingsAndFoodviewsTxt);
        lvFoodviews = (ListView) findViewById(R.id.lvFoodviews);
        ratingsAndFoodviewsListAdapter = new RatingsAndFoodviewsListAdapter(this);
        lvFoodviews.setAdapter(ratingsAndFoodviewsListAdapter);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (userId.equals(PreferenceManager.getLoginDetails().getId())) {
            editPreferences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(UserActivity.this, MyPreferencesActivity.class);
                    startActivityForResult(intent, USER_TO_PREF_REQ_CODE);
                }
            });
        } else {
            editPreferences.setVisibility(View.GONE);
        }

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserActivity.this, ImageViewActivity.class);
                i.putExtra(ImageViewActivity.IMAGE_URL, userProfile.getImageUrl());
                startActivity(i);
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserActivity.this, EditUserDetailsActivity.class);
                i.putExtra(EditUserDetailsActivity.NAME, userProfile.getFullName());
                i.putExtra(EditUserDetailsActivity.STATUS, userProfile.getStatus());
                startActivityForResult(i, USER_TO_EDIT_PROFILE);
            }
        });

        setUserDetails();
        setFont();
        getRatingsAndFoodviews(1);
        initAutoFoodviewsLoad();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if ((requestCode == USER_TO_PREF_REQ_CODE || requestCode == USER_TO_EDIT_PROFILE) && resultCode == Activity.RESULT_OK) {
            recreate();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initAutoFoodviewsLoad() {
        svUserProfile.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (svUserProfile != null) {
                    if (svUserProfile.getChildAt(0).getBottom() <= (svUserProfile.getHeight() + ADD_SCROLL_HEIGHT + svUserProfile.getScrollY())) {
                        if (moreResultsAvailable && canLoadFoodviews) {
                            getRatingsAndFoodviews(++pageFoodviews);
                        }
                    }
                }
            }
        });
    }

    private void setUserDetails() {
        Glide.with(this).load(userProfile.getImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivImage.setImageDrawable(circularBitmapDrawable);
            }
        });

        tvName.setText(userProfile.getFullName());
        tvRatingCount.setText(userProfile.getRatingCount() + " Ratings");
        tvFoodviewCount.setText(userProfile.getFoodviewCount() + " Foodviews");
        tvLevel.setText(Utils.getUserLevel(userProfile.getLevel()));
        if (!Utils.isEmpty(userProfile.getStatus())) {
            tvStatus.setText(userProfile.getStatus());
        } else {
            tvStatus.setVisibility(View.GONE);
        }

        int cuisinesSize = userProfile.getCuisines().size();
        int dishesSize = userProfile.getDishes().size();
        if (cuisinesSize == 0 && dishesSize == 0 && !userId.equals(PreferenceManager.getLoginDetails().getId())) {
            cvCuisinesAndDishesPrefs = (CardView) findViewById(R.id.cvCuisinesAndDishesPrefs);
            cvCuisinesAndDishesPrefs.setVisibility(View.GONE);
        } else {
            if (cuisinesSize == 0) {
                tvFavouriteCuisinesTxt.setVisibility(View.GONE);
                tvFavouriteCuisines.setVisibility(View.GONE);
            } else {
                tvFavouriteCuisines.setText(Utils.getTagsString(userProfile.getCuisines()));
            }

            if (dishesSize == 0) {
                tvFavouriteDishesTxt.setVisibility(View.GONE);
                tvFavouriteDishes.setVisibility(View.GONE);
            } else {
                tvFavouriteDishes.setText(Utils.getTagsString(userProfile.getDishes()));
            }
        }
    }

    private void getRatingsAndFoodviews(Integer page) {
        if (!Utils.isInternetConnected(this)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", userProfile.getId());
            jsonObject.put("page", page);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(UserActivity.this, URLs.GET_ALL_RECOMMENDATIONS, entity, "application/json", new FoodviewResponseHandler());
            canLoadFoodviews = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(this, e);
        }
    }

    public class FoodviewResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            FoodviewsResponse response = new Gson().fromJson(new String(responseBody), FoodviewsResponse.class);
            if (response.isResult()) {
                if (response.getRecommendations().size() > 0) {
                    ratingsAndFoodviewsListAdapter.appendAll(response.getRecommendations());
                    canLoadFoodviews = true;
                    moreResultsAvailable = true;
                } else {
                    moreResultsAvailable = false;
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

    private void setFont() {
        tvFoodbookTxt.setTypeface(Utils.getBold(this));
        tvName.setTypeface(Utils.getBold(this));
        tvLevel.setTypeface(Utils.getRegularFont(this));
        tvRatingCount.setTypeface(Utils.getBold(this));
        tvFoodviewCount.setTypeface(Utils.getBold(this));
        tvStatus.setTypeface(Utils.getBold(this));
        tvFavouriteCuisinesTxt.setTypeface(Utils.getBold(this));
        tvFavouriteCuisines.setTypeface(Utils.getRegularFont(this));
        tvFavouriteDishesTxt.setTypeface(Utils.getBold(this));
        tvFavouriteDishes.setTypeface(Utils.getRegularFont(this));
        editPreferences.setTypeface(Utils.getRegularFont(this));
        tvRatingsAndFoodviewsTxt.setTypeface(Utils.getBold(this));
    }

    private void asyncStart() {
        svUserProfile.setVisibility(View.GONE);
        pbUserProfile.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        svUserProfile.setVisibility(View.VISIBLE);
        pbUserProfile.setVisibility(View.GONE);
    }

    private class updateUserDetailsInDevice extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            LoginUserDto user = PreferenceManager.getLoginDetails();
            if (!user.getFirstName().equals(userProfile.getFirstName())) {
                PreferenceManager.setFirstName(userProfile.getFirstName());
            }
            if (!user.getLastName().equals(userProfile.getLastName())) {
                PreferenceManager.setLastName(userProfile.getLastName());
            }
            return null;
        }
    }
}
