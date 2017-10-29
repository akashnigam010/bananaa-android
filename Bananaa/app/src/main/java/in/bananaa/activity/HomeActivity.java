package in.bananaa.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.FoodSuggestionsAdapter;
import in.bananaa.object.FoodSuggestions.FoodSuggestionsResponse;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;
import in.bananaa.object.login.LoginUserDto;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.CustomListView;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Constant.HOME_TO_LOCATION;
import static in.bananaa.utils.Constant.HOME_TO_PREF_REQ_CODE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext;
    FacebookManager facebookManager;
    GoogleManager googleManager;
    ScrollView svHome;
    TextView title;
    TextView homeBnaText;
    TextView homeSearch;
    LoginUserDto user;

    NavigationView navigationView;
    ImageView ivUserImage;
    TextView tvUserName;
    LinearLayout llFoodbookLink;

    SwipeRefreshLayout homeSwipeRefresh;
    TextView tvHi;
    TextView tvYourSuggestions;
    CustomListView lvFoodSuggestions;
    FoodSuggestionsAdapter foodSuggestionsAdapter;
    ProgressBar pbMoreResults;
    LinearLayout llNoMoreResults;
    TextView tvThatsAll;
    TextView tvEditPrefs;
    TextView tvEditLocation;

    Integer page = 1;
    private boolean moreResultsAvailable = true;
    private boolean canLoadFoodSuggestions = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (PreferenceManager.isFirstTimeLaunch()) {
            Intent i = new Intent(HomeActivity.this, WelcomeActivity.class);
            startActivity(i);
            finish();
        } else if (!PreferenceManager.isUserLoggedIn()) {
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        } else if (!PreferenceManager.getIsPreferencesSaved()) {
            Intent i = new Intent(HomeActivity.this, MyPreferencesActivity.class);
            startActivity(i);
            finish();
        }

        facebookManager = new FacebookManager(this);
        googleManager = new GoogleManager(this);
        user = PreferenceManager.getLoginDetails();
        customizeMainContent();
        Toolbar toolbar = customizeToolbar();
        customizeNavigationDrawer(toolbar);
        setFont();
    }

    private void customizeMainContent() {
        homeSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.homeSwipeRefresh);
        svHome = (ScrollView) findViewById(R.id.svHome);
        homeBnaText = (TextView) findViewById(R.id.homeBnaText);
        homeBnaText.setText("Bananaa");
        homeSearch = (TextView) findViewById(R.id.homeSearch);
        homeSearch.setText("");
        homeSearch.setHint("Search for Restaurant, Cuisine or Dish");
        homeSearch.setOnClickListener(onHomeSearchClickListener);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        Utils.setMenuItemsFont(menu, Utils.getBold(this), this);
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        tvHi = (TextView) findViewById(R.id.tvHi);
        tvYourSuggestions = (TextView) findViewById(R.id.tvYourSuggestions);
        lvFoodSuggestions = (CustomListView) findViewById(R.id.lvFoodSuggestions);
        foodSuggestionsAdapter = new FoodSuggestionsAdapter(this);
        lvFoodSuggestions.setAdapter(foodSuggestionsAdapter);
        pbMoreResults = (ProgressBar) findViewById(R.id.pbMoreResults);
        llNoMoreResults = (LinearLayout) findViewById(R.id.llNoMoreResults);
        tvThatsAll = (TextView) findViewById(R.id.tvThatsAll);
        tvEditPrefs = (TextView) findViewById(R.id.tvEditPrefs);
        tvEditLocation = (TextView) findViewById(R.id.tvEditLocation);

        ivUserImage = (ImageView) headerLayout.findViewById(R.id.ivUserImage);
        tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        llFoodbookLink = (LinearLayout) headerLayout.findViewById(R.id.llFoodbookLink);
        Glide.with(this).load(user.getImageUrl()).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivUserImage) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                ivUserImage.setImageDrawable(circularBitmapDrawable);
            }
        });
        tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        llFoodbookLink.setOnClickListener(onFoodbookLinkListener);
        initiateFoodSuggestionsLoad();
        initAutoFoodSuggestionsLoad();

        homeSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateFoodSuggestionsLoad();
            }
        });

        tvHi.setText("Hi " + PreferenceManager.getLoginDetails().getFirstName() + "!");
        tvThatsAll.setText(mContext.getResources().getString(R.string.endText1, PreferenceManager.getLoginDetails().getFirstName()));
        tvEditPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MyPreferencesActivity.class);
                startActivityForResult(intent, HOME_TO_PREF_REQ_CODE);
            }
        });
        tvEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivityForResult(intent, HOME_TO_LOCATION);
            }
        });
    }

    private void initAutoFoodSuggestionsLoad() {
        svHome.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (svHome != null) {
                    if (svHome.getChildAt(0).getBottom() <= (svHome.getHeight() + svHome.getScrollY())) {
                        if (moreResultsAvailable && canLoadFoodSuggestions) {
                            loadFoodSuggestions(++page);
                        }
                    }
                }
            }
        });
    }

    View.OnClickListener onFoodbookLinkListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i = new Intent(HomeActivity.this, UserActivity.class);
            i.putExtra(UserActivity.USER_ID, PreferenceManager.getLoginDetails().getId());
            startActivity(i);
        }
    };

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        title = (TextView) findViewById(R.id.home_toolbar_title);
        LocationStore location = PreferenceManager.getStoredLocation();
        if (location != null) {
            title.setText(location.getName());
        } else {
            LocationStore locationStore = new LocationStore(1, "Hyderabad", LocationType.CITY);
            PreferenceManager.setStoredLocation(locationStore);
            title.setText(locationStore.getName());
        }
        return toolbar;
    }

    private void customizeNavigationDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    View.OnClickListener onHomeSearchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            redirectToSearch();
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_location) {
            redirectToLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.myPreferences) {
            Intent intent = new Intent(HomeActivity.this, MyPreferencesActivity.class);
            startActivityForResult(intent, HOME_TO_PREF_REQ_CODE);
        } else if (id == R.id.howItWorks) {
            Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.logout) {
            facebookManager.logout();
            googleManager.logout();
            PreferenceManager.resetLoginDetails();
            redirectToLogin();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == HOME_TO_PREF_REQ_CODE && resultCode == Activity.RESULT_OK) {
            initiateFoodSuggestionsLoad();
        } else if (requestCode == HOME_TO_LOCATION && resultCode == Activity.RESULT_OK) {
            LocationStore location = PreferenceManager.getStoredLocation();
            title.setText(location.getName());
            initiateFoodSuggestionsLoad();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToLocation() {
        Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
        startActivityForResult(intent, HOME_TO_LOCATION);
    }

    private void redirectToSearch() {
        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void initiateFoodSuggestionsLoad() {
        page = 1;
        pbMoreResults.setVisibility(View.VISIBLE);
        llNoMoreResults.setVisibility(View.GONE);
        loadFoodSuggestions(page);
        svHome.fullScroll(ScrollView.FOCUS_UP);
    }

    private void loadFoodSuggestions(Integer page) {
        try {
            LocationStore location = PreferenceManager.getStoredLocation();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("locationId", location.getId());
            jsonObject.put("isCity", location.getLocationType() == LocationType.CITY ? true : false);
            jsonObject.put("page", page);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(HomeActivity.this, URLs.GET_FOOD_SUGGESTIONS, entity, "application/json", new FoodSuggestionsHandler(page));
            canLoadFoodSuggestions = false;
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private class FoodSuggestionsHandler extends AsyncHttpResponseHandler {
        private Integer page;

        public FoodSuggestionsHandler(Integer page) {
            this.page = page;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            FoodSuggestionsResponse response = new Gson().fromJson(new String(responseBody), FoodSuggestionsResponse.class);
            homeSwipeRefresh.setRefreshing(false);
            if (response.isResult()) {
                if (page == 1) {
                    foodSuggestionsAdapter.clearAll();
                }
                if (response.getDishes().size() > 0) {
                    foodSuggestionsAdapter.appendAll(response.getDishes());
                    canLoadFoodSuggestions = true;
                    moreResultsAvailable = true;
                } else {
                    moreResultsAvailable = false;
                    pbMoreResults.setVisibility(View.GONE);
                    llNoMoreResults.setVisibility(View.VISIBLE);
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

    private void setFont() {
        title.setTypeface(Utils.getRegularFont(this));
        homeBnaText.setTypeface(Utils.getSimpsonFont(this));
        homeSearch.setTypeface(Utils.getRegularFont(this));
        tvUserName.setTypeface(Utils.getBold(this));
        tvHi.setTypeface(Utils.getBold(this));
        tvYourSuggestions.setTypeface(Utils.getRegularFont(this));
        tvThatsAll.setTypeface(Utils.getRegularFont(this));
        tvEditPrefs.setTypeface(Utils.getRegularFont(this));
        tvEditLocation.setTypeface(Utils.getRegularFont(this));
    }
}
