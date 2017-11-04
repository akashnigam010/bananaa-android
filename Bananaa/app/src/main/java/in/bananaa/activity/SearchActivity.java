package in.bananaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.GlobalSearchAdapter;
import in.bananaa.object.SearchItem;
import in.bananaa.object.SearchResponse;
import in.bananaa.object.SearchResultType;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class SearchActivity extends AppCompatActivity {
    AppCompatActivity mContext;
    TextView tvTitle;
    EditText etSearch;
    ListView lvSearchResults;
    GlobalSearchAdapter globalSearchAdapter;
    ProgressBar progress;
    ImageView cancelIcon;
    LinearLayout llSearchIntro;
    TextView tvSearchImageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        globalSearchAdapter = new GlobalSearchAdapter(this);

        customizeToolbar();
        customizeSearchBar();
        customizeListView();
        llSearchIntro = (LinearLayout) findViewById(R.id.llSearchIntro);
        tvSearchImageTxt = (TextView) findViewById(R.id.tvSearchImageTxt);
        setFont();
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) findViewById(R.id.search_toolbar_title);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void customizeSearchBar() {
        etSearch = (EditText) findViewById(R.id.etGlobalSearch);
        cancelIcon = (ImageView) findViewById(R.id.globalCancelButton);
        progress = (ProgressBar) findViewById(R.id.globalSearchLoader);
        cancelIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etSearch.setText("");
                tvSearchImageTxt.setText(mContext.getString(R.string.searchForAwesome));
                return true;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
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
                            doSearch(s.toString());
                        }
                    }
                };
                handler.postDelayed(runnable, 500);
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchString = etSearch.getText().toString();
                    Intent i = new Intent(SearchActivity.this, SearchResultsActivity.class);
                    i.putExtra(SearchResultsActivity.IS_TAG_SEARCH, false);
                    i.putExtra(SearchResultsActivity.TAG_NAME, "'" + searchString + "'");
                    i.putExtra(SearchResultsActivity.SEARCH_STRING, searchString);
                    startActivity(i);
                    finish();
                    return true;
                }
                return false;
            }
        });
    }

    private void customizeListView() {
        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);
        lvSearchResults.setAdapter(globalSearchAdapter);
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchItem merchantOrTag = globalSearchAdapter.getItem(position);
                Intent i;
                if (merchantOrTag.getType() == SearchResultType.RESTAURANT) {
                    i = new Intent(SearchActivity.this, MerchantDetailsActivity.class);
                    i.putExtra(MerchantDetailsActivity.MERCHANT_ID, merchantOrTag.getId());
                } else {
                    i = new Intent(SearchActivity.this, SearchResultsActivity.class);
                    i.putExtra(SearchResultsActivity.IS_TAG_SEARCH, true);
                    i.putExtra(SearchResultsActivity.TAG_ID, merchantOrTag.getId());
                    i.putExtra(SearchResultsActivity.TAG_NAME, merchantOrTag.getName());
                    i.putExtra(SearchResultsActivity.TYPE, merchantOrTag.getType());
                }
                startActivity(i);
                finish();
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.checkIfInternetConnectedAndToast(this)) {
            return;
        } else {
            try {
                asyncStart();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(Constant.TIMEOUT);
                client.post(SearchActivity.this, URLs.GLOBAL_SEARCH, entity, "application/json", new GlobalSearchResponseHandler());
            } catch (Exception e) {
                Utils.exceptionOccurred(this, e);
            }
        }
    }

    public class GlobalSearchResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            SearchResponse response = new Gson().fromJson(new String(responseBody), SearchResponse.class);
            if (response.isResult()) {
                if (response.getSearchItems() != null && response.getSearchItems().size() > 0) {
                    globalSearchAdapter.addAll(response.getSearchItems());
                    llSearchIntro.setVisibility(View.GONE);
                } else {
                    globalSearchAdapter.clearAll();
                    tvSearchImageTxt.setText(mContext.getString(R.string.noResults));
                    llSearchIntro.setVisibility(View.VISIBLE);
                }
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

    private void setFont() {
        tvTitle.setTypeface(Utils.getRegularFont(this));
        etSearch.setTypeface(Utils.getRegularFont(this));
        tvSearchImageTxt.setTypeface(Utils.getSimpsonFont(this));
    }

    private void asyncStart() {
        cancelIcon.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        cancelIcon.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

}
