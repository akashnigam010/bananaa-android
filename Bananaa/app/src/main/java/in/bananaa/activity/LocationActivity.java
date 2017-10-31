package in.bananaa.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import cz.msebera.android.httpclient.Header;
import in.bananaa.R;
import in.bananaa.adapter.CityAdapter;
import in.bananaa.object.location.LocalitiesResponse;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.CustomListView;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class LocationActivity extends AppCompatActivity {
    TextView title;
    ProgressBar pbLocation;
    ScrollView svLocation;
    TextView disclaimer1;
    TextView disclaimer2;
    CustomListView lvCities;
    CityAdapter cityAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        title = (TextView) findViewById(R.id.location_toolbar_title);
        pbLocation = (ProgressBar) findViewById(R.id.pbLocation);
        svLocation = (ScrollView) findViewById(R.id.svLocation);
        disclaimer1 = (TextView) findViewById(R.id.disclaimer1);
        disclaimer2 = (TextView) findViewById(R.id.disclaimer2);
        lvCities = (CustomListView) findViewById(R.id.lvCities);
        cityAdapter = new CityAdapter(this);
        lvCities.setAdapter(cityAdapter);
        customizeToolbar();
        getCities();
        title.setTypeface(Utils.getRegularFont(this));
        disclaimer1.setTypeface(Utils.getRegularFont(this));
        disclaimer2.setTypeface(Utils.getRegularFont(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void getCities() {
        try {
            asyncStart();
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constant.TIMEOUT);
            client.get(mContext, URLs.GET_LOCATIONS, null, "application/json", new GetLocationsResponseHandler());
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    public class GetLocationsResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            LocalitiesResponse response = new Gson().fromJson(new String(responseBody), LocalitiesResponse.class);
            if (response.isResult()) {
                cityAdapter.addAll(response.getCities());
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

    private void customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.location_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void asyncStart() {
        svLocation.setVisibility(View.GONE);
        pbLocation.setVisibility(View.VISIBLE);
    }

    private void asyncEnd() {
        svLocation.setVisibility(View.VISIBLE);
        pbLocation.setVisibility(View.GONE);
    }
}
