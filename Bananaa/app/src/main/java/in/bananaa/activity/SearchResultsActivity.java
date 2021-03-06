package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.GenericSearchRecyclerAdapter;
import in.bananaa.object.SearchResultType;
import in.bananaa.object.genericSearch.GenericSearchResponse;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

import static in.bananaa.utils.Constant.ADD_SCROLL_HEIGHT;
import static in.bananaa.utils.Constant.SEARCH_TO_LOCATION;

public class SearchResultsActivity extends AppCompatActivity {
    public static final String TYPE = "type";
    public static final String TAG_ID = "tagId";
    public static final String TAG_NAME = "tagName";
    public static final String IS_TAG_SEARCH = "isTagSearch";
    public static final String SEARCH_STRING = "searchString";
    public static final String ALL_PLACES = "All places nearby";

    private AppCompatActivity mContext;
    private TextView tvLocation;
    private TextView tvTitle;
    private SwitchCompat switchResults;
    private NestedScrollView svSearchResults;
    private RecyclerView rvSearchResults;

    private ProgressBar pbLoader;
    LinearLayout llNoMoreResults;
    TextView tvThatsAll;
    TextView tvRefineSearch;

    private String tagName;
    private SearchResultType type;
    private Integer tagId;
    private Boolean isTagSearch;
    private Boolean isMerchantSearch = false;
    private String searchString;

    private GenericSearchRecyclerAdapter searchResultsAdapter;
    private Integer page = 1;
    private boolean moreResultsAvailable = true;
    private boolean canLoadMoreResults = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        switchResults = (SwitchCompat) findViewById(R.id.switchResults);

        type = (SearchResultType) getIntent().getSerializableExtra(TYPE);
        tagId = (Integer) getIntent().getSerializableExtra(TAG_ID);
        tagName = (String) getIntent().getSerializableExtra(TAG_NAME);
        isTagSearch = (Boolean) getIntent().getSerializableExtra(IS_TAG_SEARCH);
        searchString = (String) getIntent().getSerializableExtra(SEARCH_STRING);

        if (isTagSearch == null) {
            Utils.genericErrorToast(this, this.getString(R.string.genericError));
            finish();
            return;
        }

        svSearchResults = (NestedScrollView) findViewById(R.id.svSearchResults);
        if (isTagSearch) {
            tvTitle.setText("Places where you'd find " + tagName);
            switchResults.setVisibility(View.GONE);
        } else {
            if (Utils.isEmpty(searchString)) {
                tvTitle.setText(ALL_PLACES);
                switchResults.setVisibility(View.GONE);
            } else {
                tvTitle.setText("Places where you'd find '" + searchString + "'");
                switchResults.setVisibility(View.VISIBLE);
            }
        }

        searchResultsAdapter = new GenericSearchRecyclerAdapter(this);
        rvSearchResults = (RecyclerView) findViewById(R.id.rvSearchResults);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvSearchResults.setLayoutManager(mLayoutManager);
        rvSearchResults.setNestedScrollingEnabled(false);
        rvSearchResults.setAdapter(searchResultsAdapter);
        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        llNoMoreResults = (LinearLayout) findViewById(R.id.llNoMoreResults);
        tvThatsAll = (TextView) findViewById(R.id.tvThatsAll);
        tvRefineSearch = (TextView) findViewById(R.id.tvRefineSearch);
        tvThatsAll.setText(mContext.getResources().getString(R.string.searchEndText1, PreferenceManager.getLoginDetails().getFirstName()));
        customizeToolbar();
        initiateSearch();
        initAutoFoodSuggestionsLoad();
        setFonts();

        switchResults.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    tvTitle.setText("All places named '" + searchString + "'");
                    isMerchantSearch = true;
                } else {
                    tvTitle.setText("Places where you'd find '" + searchString + "'");
                    isMerchantSearch = false;
                }
                initiateSearch();
            }
        });
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_results_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        LocationStore location = PreferenceManager.getStoredLocation();
        if (location != null) {
            tvLocation.setText(location.getName());
        } else {
            tvLocation.setText(Utils.loadDefaultLocation());
        }
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectToLocation();
            }
        });
        return toolbar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            redirectToSearch();
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void redirectToLocation() {
        Intent intent = new Intent(SearchResultsActivity.this, LocationActivity.class);
        startActivityForResult(intent, SEARCH_TO_LOCATION);
    }

    private void redirectToSearch() {
        Intent intent = new Intent(SearchResultsActivity.this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_TO_LOCATION && resultCode == Activity.RESULT_OK) {
            LocationStore location = PreferenceManager.getStoredLocation();
            tvLocation.setText(location.getName());
            initiateSearch();
        }
    }

    private void initiateSearch() {
        canLoadMoreResults = true;
        moreResultsAvailable = true;
        page = 1;
        pbLoader.setVisibility(View.VISIBLE);
        llNoMoreResults.setVisibility(View.GONE);
        loadResults(page);
        searchResultsAdapter.clearAll();
        svSearchResults.fullScroll(ScrollView.FOCUS_UP);
    }

    private void initAutoFoodSuggestionsLoad() {
        svSearchResults.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                if (svSearchResults != null) {
                    if (svSearchResults.getChildAt(0).getBottom() <= (svSearchResults.getHeight() + ADD_SCROLL_HEIGHT + svSearchResults.getScrollY())) {
                        if (moreResultsAvailable && canLoadMoreResults) {
                            loadResults(++page);
                        }
                    }
                }
            }
        });
    }

    private void loadResults(Integer page) {
        if (!Utils.checkIfInternetConnectedAndToast(this)) {
            finish();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            LocationStore locationStore = PreferenceManager.getStoredLocation();
            jsonObject.put("isCity", locationStore.getLocationType() == LocationType.CITY ? true : false);
            if (type != null) {
                jsonObject.put("type", type.toString());
            }
            jsonObject.put("page", page);
            jsonObject.put("tagId", tagId);
            jsonObject.put("searchString", searchString);
            jsonObject.put("locationId", locationStore.getId());
            jsonObject.put("isTagSearch", isTagSearch);
            jsonObject.put("isMerchantSearch", isMerchantSearch);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constant.TIMEOUT);
            client.post(SearchResultsActivity.this, URLs.GENERIC_SEARCH, entity, "application/json", new GenericSearchResponseHandler(page));
            canLoadMoreResults = false;
        } catch (Exception e) {
            Utils.exceptionOccurred(this, e);
        }
    }

    private class GenericSearchResponseHandler extends AsyncHttpResponseHandler {
        private Integer page;

        public GenericSearchResponseHandler(Integer page) {
            this.page = page;
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            GenericSearchResponse response = new Gson().fromJson(new String(responseBody), GenericSearchResponse.class);
            if (response.isResult()) {
                if (response.getMerchants().size() > 0) {
                    searchResultsAdapter.appendAll(response.getMerchants());
                    canLoadMoreResults = true;
                    moreResultsAvailable = true;
                } else {
                    moreResultsAvailable = false;
                    pbLoader.setVisibility(View.GONE);
                    llNoMoreResults.setVisibility(View.VISIBLE);
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

    private void setFonts() {
        tvLocation.setTypeface(Utils.getRegularFont(this));
        tvTitle.setTypeface(Utils.getBold(this));
        tvThatsAll.setTypeface(Utils.getRegularFont(this));
        tvRefineSearch.setTypeface(Utils.getRegularFont(this));
    }
}
