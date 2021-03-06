package in.bananaa.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.bananaa.R;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;

public class WelcomeActivity extends AppCompatActivity {
    private Context mContext;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnNext.setTypeface(Utils.getRegularFont(mContext));
        btnSkip.setTypeface(Utils.getRegularFont(mContext));

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    finishActivity();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorActive = getResources().getColor(R.color.dot_dark);
        int colorInactive = getResources().getColor(R.color.dot_light);

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

    private void finishActivity() {
        if (PreferenceManager.isFirstTimeLaunch()) {
            PreferenceManager.setFirstTimeLaunch(false);
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
            finish();
        } else {
            PreferenceManager.setFirstTimeLaunch(false);
            finish();
        }
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                btnNext.setVisibility(View.GONE);
            } else {
                btnNext.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            selectPageAndCustomize(view, position);
            return view;
        }

        private void selectPageAndCustomize(View view, int position) {
            switch (position) {
                case 0:
                    customizePage1(view);
                    break;
                case 1:
                    customizePage2(view);
                    break;
                case 2:
                    customizePage3(view);
                    break;
                case 3:
                    customizePage4(view);
                    break;
                default: break;
            }
        }

        private void customizePage1(View view) {
            TextView tvWelcome1Heading = (TextView) view.findViewById(R.id.tvWelcome1Heading);
            TextView tvWelcome1Desc1 = (TextView) view.findViewById(R.id.tvWelcome1Desc1);
            TextView tvWelcome1Desc2 = (TextView) view.findViewById(R.id.tvWelcome1Desc2);
            tvWelcome1Heading.setTypeface(Utils.getBold(mContext));
            tvWelcome1Desc1.setTypeface(Utils.getBold(mContext));
            tvWelcome1Desc2.setTypeface(Utils.getBold(mContext));
        }

        private void customizePage2(View view) {
            TextView tvWelcome2Heading = (TextView) view.findViewById(R.id.tvWelcome2Heading);
            TextView tvWelcome2Desc = (TextView) view.findViewById(R.id.tvWelcome2Desc);
            tvWelcome2Heading.setTypeface(Utils.getBold(mContext));
            tvWelcome2Desc.setTypeface(Utils.getBold(mContext));
        }

        private void customizePage3(View view) {
            TextView tvWelcome3Heading = (TextView) view.findViewById(R.id.tvWelcome3Heading);
            TextView tvWelcome3Desc1 = (TextView) view.findViewById(R.id.tvWelcome3Desc1);
            tvWelcome3Heading.setTypeface(Utils.getBold(mContext));
            tvWelcome3Desc1.setTypeface(Utils.getBold(mContext));
        }

        private void customizePage4(View view) {
            TextView tvWelcome4Desc1 = (TextView) view.findViewById(R.id.tvWelcome4Desc1);
            TextView tvWelcome4Desc2 = (TextView) view.findViewById(R.id.tvWelcome4Desc2);
            TextView tvWelcome4Desc3 = (TextView) view.findViewById(R.id.tvWelcome4Desc3);
            AppCompatButton btnStart = (AppCompatButton) view.findViewById(R.id.btnStart);
            tvWelcome4Desc1.setTypeface(Utils.getBold(mContext));
            tvWelcome4Desc2.setTypeface(Utils.getBold(mContext));
            tvWelcome4Desc3.setTypeface(Utils.getBold(mContext));
            btnStart.setTypeface(Utils.getRegularFont(mContext));

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishActivity();
                }
            });
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
