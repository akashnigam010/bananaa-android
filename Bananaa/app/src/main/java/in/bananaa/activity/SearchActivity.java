package in.bananaa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
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
import in.bananaa.object.GlobalSearchResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class SearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView tvTitle;
    EditText etSearch;
    ListView lvSearchResults;
    GlobalSearchAdapter globalSearchAdapter;
    AlertMessages messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        messages = new AlertMessages(this);
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

    private void customizeSearchBar() {
        etSearch = (EditText) findViewById(R.id.etGlobalSearch);
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etSearch.setText("");
                        // update result list
                        return true;
                    }
                }
                return false;
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
                        doSearch(s.toString());
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
                Intent i = new Intent(SearchActivity.this, TestActivity.class);
                startActivity(i);
            }
        });
    }

    private void doSearch(String searchString) {
        if (!Utils.isInternetConnected(SearchActivity.this)) {
            messages.showCustomMessage("No Internet Connection");
            return;
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("searchString", searchString);
                StringEntity entity = new StringEntity(jsonObject.toString());
                AsyncHttpClient client = new AsyncHttpClient();
                //client.addHeader("Authorization", "Bearer " + PreferenceManager.getToken());
                client.setTimeout(Constant.TIMEOUT);
                client.post(SearchActivity.this, URLs.GLOBAL_SEARCH, entity, "application/json", new GlobalSearchResponseHandler());
            } catch (UnsupportedEncodingException e) {
                messages.showCustomMessage("FUCK!");
                e.printStackTrace();
            } catch (Exception e) {
                messages.showCustomMessage("FUCK!");
                e.printStackTrace();
            }
        }
    }

    public class GlobalSearchResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            GlobalSearchResponse response = new Gson().fromJson(new String(responseBody), GlobalSearchResponse.class);
            if (response.isResult()) {
                if (response.getSearchItems() != null) {
                    // remove below extra logic to handle no results found
                    if (response.getSearchItems().size() == 1) {
                        if (response.getSearchItems().get(0).getType() != null){
                            globalSearchAdapter.addAll(response.getSearchItems());
                        } else {
                            messages.showCustomMessage("No results found");
                        }
                    } else {
                        globalSearchAdapter.addAll(response.getSearchItems());
                    }

                } else {
                    messages.showCustomMessage("No results found");
                }
            } else {
                messages.showCustomMessage("Something seems fishy! Please try after some time.");
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            //messages.showCustomMessage("Something seems fishy! Please try after some time.");
        }
    }

    private void setFont() {
        tvTitle.setTypeface(Utils.getRegularFont(this));
        etSearch.setTypeface(Utils.getRegularFont(this));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
