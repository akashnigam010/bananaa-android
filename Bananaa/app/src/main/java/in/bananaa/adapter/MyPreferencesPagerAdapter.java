package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import in.bananaa.R;
import in.bananaa.object.myPreferences.MyPreferences;
import in.bananaa.utils.Utils;

public class MyPreferencesPagerAdapter extends PagerAdapter {
    MyPreferences myPreferences;
    Boolean isNewPreference = false;

    LayoutInflater layoutInflater;
    Activity mContext;
    int[] layouts;
    TextView tvPref1Title;
    RadioGroup rgVegNonVeg;
    RadioButton rbVeg;
    RadioButton rbNonVeg;
    RadioButton rbAnything;

    TextView tvPref2Title;
    EditText etPrefCuisine;

    TextView tvPref3Title;
    EditText etPrefItem;

    public MyPreferencesPagerAdapter(Activity mContext, int[] layouts, MyPreferences myPreferences) {
        this.mContext = mContext;
        this.layouts = layouts;
        this.myPreferences = myPreferences;
        if (myPreferences == null) {
            isNewPreference = true;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);
        selectPageAndCustomize(view, position);
        return view;
    }

    private void selectPageAndCustomize(View view, int position) {
        switch (position) {
            case 0:
                customizePage1(view);
                setFontPage1();
                break;
            case 1:
                customizePage2(view);
                setFontPage2();
                break;
            case 2:
                customizePage3(view);
                setFontPage3();
                break;
            default: break;
        }
    }

    private void customizePage1(View view) {
        tvPref1Title = (TextView) view.findViewById(R.id.tvPref1Title);
        rgVegNonVeg = (RadioGroup) view.findViewById(R.id.rgVegNonVeg);
        rbVeg = (RadioButton) view.findViewById(R.id.rbVeg);
        rbNonVeg = (RadioButton) view.findViewById(R.id.rbVeg);
        rbAnything = (RadioButton) view.findViewById(R.id.rbVeg);
        if (!isNewPreference) {
            switch (myPreferences.getType()) {
                case 0 :
                    rgVegNonVeg.check(R.id.rbVeg);
                    break;
                case 1 :
                    rgVegNonVeg.check(R.id.rbNonVeg);
                    break;
                case 2 :
                    rgVegNonVeg.check(R.id.rbAnything);
                    break;
                default : break;
            }
        }
        rgVegNonVeg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                   switch(i){
                       case R.id.rbVeg:
                           myPreferences.setType(0);
                           break;
                       case R.id.rbNonVeg:
                           myPreferences.setType(1);
                           break;
                       case R.id.rbAnything:
                           myPreferences.setType(2);
                           break;
                   }
                   final android.os.Handler handler = new android.os.Handler();
                   final Button next = (Button) mContext.findViewById(R.id.btn_next);
                   Runnable runnable;
                   runnable = new Runnable() {
                       @Override
                       public void run() {
                           next.performClick();
                       }
                   };
                   handler.postDelayed(runnable, 300);
               }
           }
        );
    }

    private void customizePage2(View view) {
        tvPref2Title = (TextView) view.findViewById(R.id.tvPref2Title);
        etPrefCuisine = (EditText) view.findViewById(R.id.etPrefCuisine);
    }

    private void customizePage3(View view) {
        tvPref3Title = (TextView) view.findViewById(R.id.tvPref3Title);
        etPrefItem = (EditText) view.findViewById(R.id.etPrefItem);
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

    private void setFontPage1() {
        tvPref1Title.setTypeface(Utils.getRegularFont(mContext));
        rbVeg.setTypeface(Utils.getRegularFont(mContext));
        rbNonVeg.setTypeface(Utils.getRegularFont(mContext));
        rbAnything.setTypeface(Utils.getRegularFont(mContext));
    }

    private void setFontPage2() {
        tvPref2Title.setTypeface(Utils.getRegularFont(mContext));
        etPrefCuisine.setTypeface(Utils.getRegularFont(mContext));
    }

    private void setFontPage3() {
        tvPref3Title.setTypeface(Utils.getRegularFont(mContext));
        etPrefItem.setTypeface(Utils.getRegularFont(mContext));
    }
}
