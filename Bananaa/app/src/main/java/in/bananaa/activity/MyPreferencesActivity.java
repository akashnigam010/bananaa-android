package in.bananaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.bananaa.R;
import in.bananaa.adapter.MyPreferencesPagerAdapter;
import in.bananaa.object.myPreferences.MyPreferences;
import in.bananaa.utils.PreferenceManager;

public class MyPreferencesActivity extends AppCompatActivity {
    public static final String MY_PREFERENCES = "myPreferences";
    private ViewPager viewPager;
    private MyPreferencesPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnBack, btnNext;
    MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PreferenceManager.getIsPreferencesSaved()) {
            launchHomeScreen();
            finish();
        }
        myPreferences = (MyPreferences) getIntent().getSerializableExtra(MY_PREFERENCES);
        setContentView(R.layout.activity_my_preferences);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnBack = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnBack.setVisibility(View.GONE);

        layouts = new int[]{
                R.layout.my_pref_slide1,
                R.layout.my_pref_slide2,
                R.layout.my_pref_slide3};

        addBottomDots(0);
        myViewPagerAdapter = new MyPreferencesPagerAdapter(this, layouts, myPreferences);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(-1);
                viewPager.setCurrentItem(current);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorActive = ContextCompat.getColor(this, R.color.dot_dark);
        int colorInactive = ContextCompat.getColor(this, R.color.dot_light);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorActive);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        PreferenceManager.setFirstTimeLaunch(false);
        startActivity(new Intent(MyPreferencesActivity.this, MainActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == 0) {
                btnNext.setText(getString(R.string.next));
                btnBack.setVisibility(View.GONE);
            } else if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.submit));
                btnBack.setVisibility(View.VISIBLE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnBack.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
}
