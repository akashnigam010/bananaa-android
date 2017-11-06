package in.bananaa.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.plumillonforge.android.chipview.Chip;
import com.plumillonforge.android.chipview.ChipViewAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.object.SearchResultType;
import in.bananaa.object.TagsPreference;
import in.bananaa.object.TagsPreferencesResponse;
import in.bananaa.object.VegnonvegPreferenceResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.OnTagChipClickListener;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.TagChipView;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Constant.ADD_SCROLL_HEIGHT_PREFEERENCES;
import static in.bananaa.utils.Utils.chipsBackgrounds;

public class MyPreferencesPagerAdapter extends PagerAdapter {
    private Activity mContext;
    private int[] layouts;
    private TextView tvPref1Title;
    private RadioGroup rgVegNonVeg;
    private RadioButton rbVeg;
    private RadioButton rbNonVeg;
    private RadioButton rbAnything;

    private TextView tvPref2Title;
    private EditText etPrefCuisine;
    private ProgressBar pbCuisineLoader;
    private ImageView ivCuisineSearchCancel;
    private int pageCuisines = 1;
    private boolean noMoreCuisines = false;
    private boolean canSearchCuisines = true;
    private ScrollView svSearchCuisines;
    private TagChipView cvCuisines;
    private List<Chip> cvCuisineChipList = new ArrayList<>();

    private TextView tvPref3Title;
    private EditText etPrefSuggestion;
    private ProgressBar pbSuggestionLoader;
    private ImageView ivSuggestionSearchCancel;
    private int pageSuggestions = 1;
    private boolean noMoreSuggestions = false;
    private boolean canSearchSuggestions = true;
    private ScrollView svSearchSuggestions;
    private TagChipView cvSuggestions;
    private List<Chip> cvSuggestionChipList = new ArrayList<>();

    public MyPreferencesPagerAdapter(Activity mContext, int[] layouts) {
        this.mContext = mContext;
        this.layouts = layouts;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        rbNonVeg = (RadioButton) view.findViewById(R.id.rbNonVeg);
        rbAnything = (RadioButton) view.findViewById(R.id.rbAnything);
        getVegnonvegPreferences();
        rgVegNonVeg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                   switch(i){
                       case R.id.rbVeg:
                           updateTagPreference(1, null, false, true);
                           break;
                       case R.id.rbNonVeg:
                           updateTagPreference(2, null, false, true);
                           break;
                       case R.id.rbAnything:
                           updateTagPreference(3, null, false, true);
                           break;
                   }
               }
           }
        );
    }

    private void customizePage2(View view) {
        tvPref2Title = (TextView) view.findViewById(R.id.tvPref2Title);
        etPrefCuisine = (EditText) view.findViewById(R.id.etPrefCuisine);
        pbCuisineLoader = (ProgressBar) view.findViewById(R.id.pbCuisineLoader);
        ivCuisineSearchCancel = (ImageView) view.findViewById(R.id.ivCuisineSearchCancel);
        svSearchCuisines = (ScrollView) view.findViewById(R.id.svSearchCuisines);
        cvCuisines = (TagChipView) view.findViewById(R.id.cvCuisines);

        cvCuisines.setChipLayoutRes(R.layout.chip);
        ChipViewAdapter cvCuisineChipViewAdapter = new TagChipViewAdapter(mContext);
        cvCuisines.setAdapter(cvCuisineChipViewAdapter);
        cvCuisines.setChipList(cvCuisineChipList);
        cvCuisines.setOnChipClickListener(new OnTagChipClickListener() {
            @Override
            public void onChipClick(Chip chip, View view, int position) {
                TagsPreference tc = (TagsPreference) chip;
                TextView tv = ((TextView) view.findViewById(android.R.id.text1));
                if (tc.getSelected()) {
                    tc.setSelected(false);
                    tv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_chip, null));
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.darkGrey));
                    updateTagPreference(tc.getId(), SearchResultType.CUISINE, true, false);
                } else {
                    tc.setSelected(true);
                    tv.setBackground(mContext.getResources().getDrawable(chipsBackgrounds[tc.getId()%10], null));
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    updateTagPreference(tc.getId(), SearchResultType.CUISINE, false, false);
                }
            }
        });

        pageCuisines = 1;
        initAutoCuisinesLoad();

        ivCuisineSearchCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPrefCuisine.setText("");
                return true;
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
                        String searchString = s.toString();
                        if (searchString.length() >= 2) {
                            searchCuisines(1, searchString, true);
                        }
                        else if (searchString.length() == 0) {
                            pageCuisines = 1;
                            initAutoCuisinesLoad();
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
        pbSuggestionLoader = (ProgressBar) view.findViewById(R.id.pbSuggestionLoader);
        ivSuggestionSearchCancel = (ImageView) view.findViewById(R.id.ivSuggestionSearchCancel);
        svSearchSuggestions = (ScrollView) view.findViewById(R.id.svSearchSuggestions);
        cvSuggestions = (TagChipView) view.findViewById(R.id.cvSuggestions);

        cvSuggestions.setChipLayoutRes(R.layout.chip);
        ChipViewAdapter cvSuggestionChipViewAdapter = new TagChipViewAdapter(mContext);
        cvSuggestions.setAdapter(cvSuggestionChipViewAdapter);
        cvSuggestions.setChipList(cvSuggestionChipList);
        cvSuggestions.setOnChipClickListener(new OnTagChipClickListener() {
            @Override
            public void onChipClick(Chip chip, View view, int position) {
                TagsPreference tc = (TagsPreference) chip;
                TextView tv = ((TextView) view.findViewById(android.R.id.text1));
                if (tc.getSelected()) {
                    tc.setSelected(false);
                    tv.setBackground(mContext.getResources().getDrawable(R.drawable.bg_chip, null));
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.darkGrey));
                    updateTagPreference(tc.getId(), SearchResultType.SUGGESTION, true, false);
                } else {
                    tc.setSelected(true);
                    tv.setBackground(mContext.getResources().getDrawable(chipsBackgrounds[tc.getId()%10], null));
                    tv.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    updateTagPreference(tc.getId(), SearchResultType.SUGGESTION, false, false);
                }
            }
        });
        pageSuggestions = 1;
        canSearchSuggestions = true;
        initAutoSuggestionsLoad();

        ivSuggestionSearchCancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etPrefSuggestion.setText("");
                return true;
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
                        String searchString = s.toString();
                        if (searchString.length() >= 2) {
                            searchSuggestions(1, searchString, true);
                        }
                        else if (searchString.length() == 0) {
                            pageSuggestions = 1;
                            initAutoSuggestionsLoad();
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });
    }
    private void getVegnonvegPreferences() {
        if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
            mContext.finish();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(mContext, URLs.GET_VEGNONVEG_PREFERENCE, entity, "application/json", new GetPreferenceHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class GetPreferenceHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            VegnonvegPreferenceResponse response = new Gson().fromJson(new String(responseBody), VegnonvegPreferenceResponse.class);
            if (response.isResult()) {
                if (response.getId() != null) {
                    switch(response.getId()) {
                        case 1 :
                            rgVegNonVeg.check(R.id.rbVeg);
                            break;
                        case 2 :
                            rgVegNonVeg.check(R.id.rbNonVeg);
                            break;
                        case 3 :
                            rgVegNonVeg.check(R.id.rbAnything);
                            break;
                        default : break;
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


    private void initAutoCuisinesLoad() {
        searchCuisines(pageCuisines, null, true);
        svSearchCuisines.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (svSearchCuisines != null) {
                    if (svSearchCuisines.getChildAt(0).getBottom() <= (svSearchCuisines.getHeight() + ADD_SCROLL_HEIGHT_PREFEERENCES + svSearchCuisines.getScrollY())) {
                        if (canSearchCuisines && !noMoreCuisines) {
                            searchCuisines(++ pageCuisines, null, false);
                        }
                    }
                }
            }
        });
    }

    private void initAutoSuggestionsLoad() {
        searchSuggestions(pageSuggestions, null, true);
        svSearchSuggestions.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (svSearchSuggestions != null) {
                    if (svSearchSuggestions.getChildAt(0).getBottom() <= (svSearchSuggestions.getHeight() + ADD_SCROLL_HEIGHT_PREFEERENCES + svSearchSuggestions.getScrollY())) {
                        if (canSearchSuggestions && !noMoreSuggestions) {
                            searchSuggestions(++ pageSuggestions, null, false);
                        }
                    }
                }
            }
        });
    }

    private void searchCuisines(Integer page, String searchString, boolean replaceExistingData) {
        if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        }
        try {
            asyncStart(SearchResultType.CUISINE);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", page);
            jsonObject.put("searchString", searchString);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(mContext, URLs.SEARCH_CUISINES, entity, "application/json", new GetCuisinesHandler(replaceExistingData));
            canSearchCuisines = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    private void searchSuggestions(Integer page, String searchString, boolean replaceExistingData) {
        if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        }
        try {
            asyncStart(SearchResultType.SUGGESTION);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("page", page);
            jsonObject.put("searchString", searchString);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            client.post(mContext, URLs.SEARCH_SUGGESTIONS, entity, "application/json", new GetSuggestionsHandler(replaceExistingData));
            canSearchSuggestions = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }

    public class GetCuisinesHandler extends AsyncHttpResponseHandler {
        private boolean replaceExistingData;
        public GetCuisinesHandler(boolean replaceExistingData) {
            this.replaceExistingData = replaceExistingData;
        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            TagsPreferencesResponse response = new Gson().fromJson(new String(responseBody), TagsPreferencesResponse.class);
            if (response.isResult()) {
                List<TagsPreference> chips = response.getSearchItems();
                if (chips.size() > 0) {
                    List<Chip> chipList = null;
                    if (replaceExistingData) {
                        chipList = new ArrayList<>();
                    } else {
                        chipList = cvCuisines.getChipList();
                    }
                    chipList.addAll(chips);
                    cvCuisines.setChipList(chipList);
                } else {
                    noMoreCuisines = true;
                }
            } else {
                Utils.responseError(mContext, response);
            }
            asyncEnd(SearchResultType.CUISINE);
            canSearchCuisines = true;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd(SearchResultType.CUISINE);
            Utils.responseFailure(mContext);
        }
    }

    public class GetSuggestionsHandler extends AsyncHttpResponseHandler {
        private boolean replaceExistingData;
        public GetSuggestionsHandler(boolean replaceExistingData) {
            this.replaceExistingData = replaceExistingData;
        }
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            TagsPreferencesResponse response = new Gson().fromJson(new String(responseBody), TagsPreferencesResponse.class);
            if (response.isResult()) {
                List<TagsPreference> chips = response.getSearchItems();
                if (chips.size() > 0) {
                    List<Chip> chipList = null;
                    if (replaceExistingData) {
                        chipList = new ArrayList<>();
                    } else {
                        chipList = cvSuggestions.getChipList();
                    }
                    chipList.addAll(chips);
                    cvSuggestions.setChipList(chipList);
                    noMoreSuggestions = false;
                } else {
                    noMoreSuggestions = true;
                }
            } else {
                Utils.responseError(mContext, response);
            }
            asyncEnd(SearchResultType.SUGGESTION);
            canSearchSuggestions = true;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            asyncEnd(SearchResultType.SUGGESTION);
            Utils.responseFailure(mContext);
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

    private void updateTagPreference(Integer id, SearchResultType type, boolean isRemove, boolean isSaveVegnonvegPref) {
        if (!Utils.checkIfInternetConnectedAndToast(mContext)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
            client.setTimeout(Constant.TIMEOUT);
            String url = null;
            if (isSaveVegnonvegPref) {
                url = URLs.SAVE_VEGNONVEG_PREFERENCE;
            } else {
                if (type == SearchResultType.CUISINE) {
                    if (isRemove) {
                        url = URLs.REMOVE_CUISINE_PREFERENCE;
                    } else {
                        url = URLs.ADD_CUISINE_PREFERENCE;
                    }
                } else if (type == SearchResultType.SUGGESTION) {
                    if (isRemove) {
                        url = URLs.REMOVE_SUGGESTION_PREFERENCE;
                    } else {
                        url = URLs.ADD_SUGGESTION_PREFERENCE;
                    }
                } else {
                    return;
                }
            }
            client.post(mContext, url, entity, "application/json", new UpdateTagPreferenceHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(mContext, e);
        }
    }
    public class UpdateTagPreferenceHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            // do nothing - data is saved in the backend
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    private void setFontPage1() {
        tvPref1Title.setTypeface(Utils.getBold(mContext));
        rbVeg.setTypeface(Utils.getRegularFont(mContext));
        rbNonVeg.setTypeface(Utils.getRegularFont(mContext));
        rbAnything.setTypeface(Utils.getRegularFont(mContext));
    }

    private void setFontPage2() {
        tvPref2Title.setTypeface(Utils.getBold(mContext));
        etPrefCuisine.setTypeface(Utils.getRegularFont(mContext));
    }

    private void setFontPage3() {
        tvPref3Title.setTypeface(Utils.getBold(mContext));
        etPrefSuggestion.setTypeface(Utils.getRegularFont(mContext));
    }

    private void asyncStart(SearchResultType type) {
        if (type == SearchResultType.CUISINE) {
            ivCuisineSearchCancel.setVisibility(View.INVISIBLE);
            pbCuisineLoader.setVisibility(View.VISIBLE);
        } else if (type == SearchResultType.SUGGESTION) {
            ivSuggestionSearchCancel.setVisibility(View.INVISIBLE);
            pbSuggestionLoader.setVisibility(View.VISIBLE);
        }
    }

    private void asyncEnd(SearchResultType type) {
        if (type == SearchResultType.CUISINE) {
            ivCuisineSearchCancel.setVisibility(View.VISIBLE);
            pbCuisineLoader.setVisibility(View.GONE);
        } else if (type == SearchResultType.SUGGESTION) {
            ivSuggestionSearchCancel.setVisibility(View.VISIBLE);
            pbSuggestionLoader.setVisibility(View.GONE);
        }
    }
}
