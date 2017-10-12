package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipView;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.object.GlobalSearchItem;
import in.bananaa.object.GlobalSearchResponse;
import in.bananaa.object.SearchResultType;
import in.bananaa.object.TagChip;
import in.bananaa.object.myPreferences.MyPreferences;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class MyPreferencesPagerAdapter extends PagerAdapter {
    private MyPreferences myPreferences;
    private Boolean isNewPreference = false;

    private LayoutInflater layoutInflater;
    private Activity mContext;
    private int[] layouts;
    private TextView tvPref1Title;
    private RadioGroup rgVegNonVeg;
    private RadioButton rbVeg;
    private RadioButton rbNonVeg;
    private RadioButton rbAnything;

    private TextView tvPref2Title;
    private EditText etPrefCuisine;
    private ListView lvSearchCuisine;
    private TagSearchAdapter cuisineSearchAdapter;
    private ChipView cvCuisines;
    private ChipViewAdapter cvCuisineChipViewAdapter;
    private List<Chip> cvCuisineChipList = new ArrayList<>();

    private TextView tvPref3Title;
    private EditText etPrefSuggestion;
    private ListView lvSearchSuggestion;
    private TagSearchAdapter suggestionSearchAdapter;
    private ChipView cvSuggestions;
    private ChipViewAdapter cvSuggestionChipViewAdapter;
    private List<Chip> cvSuggestionChipList = new ArrayList<>();

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
        cvCuisines = (ChipView) view.findViewById(R.id.cvCuisines);
        lvSearchCuisine = (ListView) view.findViewById(R.id.lvSearchCuisine);
        cuisineSearchAdapter = new TagSearchAdapter(mContext);
        lvSearchCuisine.setAdapter(cuisineSearchAdapter);

        cvCuisines.setChipLayoutRes(R.layout.chip_close);
        cvCuisineChipViewAdapter = new TagChipViewAdapter(mContext);
        cvCuisines.setAdapter(cvCuisineChipViewAdapter);
        cvCuisines.setChipList(cvCuisineChipList);

        lvSearchCuisine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalSearchItem item = cuisineSearchAdapter.getItem(position);
                TagChip tag = new TagChip(item.getId(), item.getName(), item.getType());
                cvCuisines.add(tag);
                lvSearchCuisine.setVisibility(View.GONE);
            }
        });

        etPrefCuisine.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() >= 2) {
                            doSearch(s.toString(), SearchResultType.CUISINE);
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    private void customizePage3(View view) {
        tvPref3Title = (TextView) view.findViewById(R.id.tvPref3Title);
        etPrefSuggestion = (EditText) view.findViewById(R.id.etPrefSuggestion);
        cvSuggestions = (ChipView) view.findViewById(R.id.cvSuggestions);
        lvSearchSuggestion = (ListView) view.findViewById(R.id.lvSearchSuggestion);
        suggestionSearchAdapter = new TagSearchAdapter(mContext);
        lvSearchSuggestion.setAdapter(suggestionSearchAdapter);

        cvSuggestions.setChipLayoutRes(R.layout.chip_close);
        cvSuggestionChipViewAdapter = new TagChipViewAdapter(mContext);
        cvSuggestions.setAdapter(cvSuggestionChipViewAdapter);
        cvSuggestions.setChipList(cvSuggestionChipList);

        lvSearchSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalSearchItem item = suggestionSearchAdapter.getItem(position);
                TagChip tag = new TagChip(item.getId(), item.getName(), item.getType());
                cvSuggestions.add(tag);
                lvSearchSuggestion.setVisibility(View.GONE);
            }
        });

        etPrefSuggestion.addTextChangedListener(new TextWatcher() {
            final android.os.Handler handler = new android.os.Handler();
            Runnable runnable;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable);
            }

            @Override
            public void afterTextChanged(final Editable s) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().length() >= 2) {
                            doSearch(s.toString(), SearchResultType.DISH);
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }

    private void doSearch(String searchString, SearchResultType type) {
        if (!Utils.isInternetConnected(mContext)) {
            AlertMessages.noInternet(mContext);
            return;
        } else {
            try {
                asyncStart();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(Constant.TIMEOUT);
                if (type == SearchResultType.CUISINE.CUISINE) {
                    client.post(mContext, URLs.SEARCH_CUISINES, entity, "application/json", new TagSearchResponseHandler(type));
                } else {
                    client.post(mContext, URLs.SEARCH_SUGGESTIONS, entity, "application/json", new TagSearchResponseHandler(type));
                }
            } catch (UnsupportedEncodingException e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            } catch (Exception e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }
    }

    public class TagSearchResponseHandler extends AsyncHttpResponseHandler {
        private SearchResultType type;

        public TagSearchResponseHandler(SearchResultType type) {
            this.type = type;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            GlobalSearchResponse response = new Gson().fromJson(new String(responseBody), GlobalSearchResponse.class);
            if (response.isResult()) {
                if (response.getSearchItems() != null && response.getSearchItems().size() > 0) {
                    if (type == SearchResultType.CUISINE) {
                        cuisineSearchAdapter.addAll(response.getSearchItems());
                        lvSearchCuisine.setVisibility(View.VISIBLE);
                    } else {
                        suggestionSearchAdapter.addAll(response.getSearchItems());
                        lvSearchSuggestion.setVisibility(View.VISIBLE);
                    }
                } else {
                    //messages.showCustomMessage("No results found");
                }
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
        etPrefSuggestion.setTypeface(Utils.getRegularFont(mContext));
    }

    private void asyncStart() {
        //cancelIcon.setVisibility(View.INVISIBLE);
        //progress.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        //cancelIcon.setVisibility(View.VISIBLE);
        //progress.setVisibility(View.GONE);
    }
}
