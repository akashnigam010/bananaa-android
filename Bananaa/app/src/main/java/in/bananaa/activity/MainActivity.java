package in.bananaa.activity;

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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import in.bananaa.R;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;
import in.bananaa.utils.login.LoginUserDto;

public class MainActivity extends AppCompatActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);

        ivUserImage = (ImageView) headerLayout.findViewById(R.id.ivUserImage);
        tvUserName = (TextView) headerLayout.findViewById(R.id.tvUserName);
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
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        title = (TextView) findViewById(R.id.home_toolbar_title);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

       // noinspection SimplifiableIfStatement
        if (id == R.id.action_location) {
            redirectToLocation();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

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

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void redirectToLocation() {
        Intent intent = new Intent(MainActivity.this, LocationActivity.class);
        startActivity(intent);
    }

    private void redirectToSearch() {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void setFont() {
        title.setTypeface(Utils.getRegularFont(this));
        homeBnaText.setTypeface(Utils.getSimpsonFont(this));
        homeSearch.setTypeface(Utils.getRegularFont(this));
        tvUserName.setTypeface(Utils.getBold(this));
    }
}
