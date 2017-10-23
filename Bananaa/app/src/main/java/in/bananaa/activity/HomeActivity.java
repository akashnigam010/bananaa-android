package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import in.bananaa.R;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;
import in.bananaa.object.login.LoginUserDto;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Constant.HOME_TO_LOCATION;
import static in.bananaa.utils.Constant.HOME_TO_PREF_REQ_CODE;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FacebookManager facebookManager;
    GoogleManager googleManager;
    TextView title;
    TextView homeBnaText;
    TextView homeSearch;
    LoginUserDto user;

    NavigationView navigationView;
    ImageView ivUserImage;
    TextView tvUserName;
    LinearLayout llFoodbookLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        drawer.setDrawerListener(toggle);
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
            // reload preferences
        } else if (requestCode == HOME_TO_LOCATION && resultCode == Activity.RESULT_OK) {
            LocationStore location = PreferenceManager.getStoredLocation();
            title.setText(location.getName());
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

    private void setFont() {
        title.setTypeface(Utils.getRegularFont(this));
        homeBnaText.setTypeface(Utils.getSimpsonFont(this));
        homeSearch.setTypeface(Utils.getRegularFont(this));
        tvUserName.setTypeface(Utils.getBold(this));
    }
}
