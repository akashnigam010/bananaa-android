package in.bananaa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.adapter.GlobalSearchAdapter;
import in.bananaa.object.SearchItem;
import in.bananaa.object.SearchResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class SearchActivity extends AppCompatActivity {
    Context mContext;
    TextView tvTitle;
    EditText etSearch;
    ListView lvSearchResults;
    GlobalSearchAdapter globalSearchAdapter;
    ProgressBar progress;
    ImageView cancelIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        globalSearchAdapter = new GlobalSearchAdapter(this);

        customizeToolbar();
        customizeSearchBar();
        customizeListView();
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
    }

    private void customizeListView() {
        lvSearchResults = (ListView) findViewById(R.id.lvSearchResults);
        lvSearchResults.setAdapter(globalSearchAdapter);
        lvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchItem merchant = globalSearchAdapter.getItem(position);
                Intent i = new Intent(SearchActivity.this, MerchantDetailsActivity.class);
                i.putExtra(MerchantDetailsActivity.MERCHANT_ID, merchant.getId());
                startActivity(i);
                finish();
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.isInternetConnected(SearchActivity.this)) {
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
                client.post(SearchActivity.this, URLs.GLOBAL_SEARCH, entity, "application/json", new GlobalSearchResponseHandler());
            } catch (UnsupportedEncodingException e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            } catch (Exception e) {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
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

    private void setFont() {
        tvTitle.setTypeface(Utils.getRegularFont(this));
        etSearch.setTypeface(Utils.getRegularFont(this));
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
