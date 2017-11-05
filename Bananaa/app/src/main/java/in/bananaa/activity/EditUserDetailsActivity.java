package in.bananaa.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.object.StatusResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class EditUserDetailsActivity extends AppCompatActivity {
    public static final String NAME = "userName";
    public static final String STATUS = "userStatus";
    public static final String RELOAD_PROFILE = "reloadUserProfile";
    private AppCompatActivity mContext;
    private TextView tvTitle;
    private TextView tvName;
    private EditText etName;
    private EditText etStatus;
    private TextView tvStatus;
    String name = null;
    String status = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);
        tvName = (TextView) findViewById(R.id.tvName);
        etName = (EditText) findViewById(R.id.etName);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        etStatus = (EditText) findViewById(R.id.etStatus);
        name = (String) getIntent().getSerializableExtra(NAME);
        status = (String) getIntent().getSerializableExtra(STATUS);
        etName.setText(name);
        etStatus.setText(status);
        etName.setSelection(etName.getText().length());
        customizeToolbar();
        setFont();
    }

    private Toolbar customizeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        if (id == R.id.action_save) {
            if (etName.getText().toString().equals(name) && etStatus.getText().toString().equals(status)) {
                finish();
                return true;
            }
            if (!Utils.checkIfInternetConnectedAndToast(this)) {
                return false;
            } else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", etName.getText().toString());
                    jsonObject.put("status", etStatus.getText().toString());
                    StringEntity entity = new StringEntity(jsonObject.toString());
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.addHeader("Authorization", "Bearer " + PreferenceManager.getAccessToken());
                    client.setTimeout(Constant.TIMEOUT);
                    client.post(mContext, URLs.SAVE_PROFILE, entity, "application/json", new SaveStatusResponseHandler());
                } catch (Exception e) {
                    Utils.exceptionOccurred(this, e);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private class SaveStatusResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            StatusResponse response = new Gson().fromJson(new String(responseBody), StatusResponse.class);
            if (response.isResult()) {
                finishActivity();
            } else {
                Utils.responseError(mContext, response);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Utils.responseFailure(mContext);
        }
    }

    private void finishActivity() {
        Intent intent = getIntent();
        intent.putExtra(RELOAD_PROFILE, true);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menuItems) {
        getMenuInflater().inflate(R.menu.edit_user_details_menu, menuItems);
        Utils.setMenuItemsFont(menuItems, Utils.getBold(this), this);
        return super.onCreateOptionsMenu(menuItems);
    }

    private void setFont() {
        tvTitle.setTypeface(Utils.getBold(mContext));
        tvName.setTypeface(Utils.getRegularFont(mContext));
        etName.setTypeface(Utils.getBold(mContext));
        tvStatus.setTypeface(Utils.getRegularFont(mContext));
        etStatus.setTypeface(Utils.getRegularFont(mContext));
    }
}
