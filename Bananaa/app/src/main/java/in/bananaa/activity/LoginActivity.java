package in.bananaa.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.object.VegnonvegPreferenceResponse;
import in.bananaa.object.login.ClientType;
import in.bananaa.object.login.LoginResponse;
import in.bananaa.object.myPreferences.MyPreferences;
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    Context mContext;
    ProgressBar progress;
    Button fbLoginBtn;
    Button googleLoginBtn;
    TextView tvNoInternet;

    FacebookManager facebookManager;
    GoogleManager googleManager;

    Boolean isFbLogin = false;
    Boolean isGoogleLogin = false;

    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (PreferenceManager.isUserLoggedIn()) {
            startPreferenceActivity();
        }

        facebookManager = new FacebookManager(this);
        googleManager = new GoogleManager(this);

        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        progress = (ProgressBar) findViewById(R.id.loginLoader);
        fbLoginBtn = (Button) findViewById(R.id.fbLogin);
        googleLoginBtn = (Button) findViewById(R.id.googleLogin);
        setFont();
        fbLoginBtn.setOnClickListener(onFbSignIn);
        googleLoginBtn.setOnClickListener(onGoogleSignIn);
    }

    View.OnClickListener onFbSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isFbLogin = true;
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                tvNoInternet.setVisibility(View.VISIBLE);
                return;
            }
            tvNoInternet.setVisibility(View.GONE);
            facebookManager.login(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    doLogin(loginResult.getAccessToken().getToken(), ClientType.FACEBOOK);
                }

                @Override
                public void onCancel() {}

                @Override
                public void onError(FacebookException error) {
                    AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
                }
            });
        }
    };

    View.OnClickListener onGoogleSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isGoogleLogin = true;
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                tvNoInternet.setVisibility(View.VISIBLE);
                return;
            } else {
                tvNoInternet.setVisibility(View.GONE);
                googleManager.login();
                return;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isFbLogin) {
            facebookManager.onActivityResult(requestCode, resultCode, data);
        }
        if (isGoogleLogin) {
            GoogleSignInResult result = googleManager.onActivityResult(requestCode, resultCode, data);
            if (result != null && result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String authCode = acct.getServerAuthCode();
                doLogin(authCode, ClientType.GOOGLE);
            }
        }
    }

    private void doLogin(String authCode, ClientType clientType) {
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("accessToken", authCode);
            jsonObject.put("client", clientType);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constant.TIMEOUT);
            client.post(LoginActivity.this, URLs.LOGIN, entity, "application/json", new LoginActivity.LoginResponseHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    public class LoginResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            LoginResponse response = new Gson().fromJson(new String(responseBody), LoginResponse.class);
            if (response.isResult()) {
                saveLoginDetails(response);
                accessToken = response.getAccessToken();
                startPreferenceActivity();
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

    private void saveLoginDetails(LoginResponse loginResponse) {
        PreferenceManager.putLoginDetails(loginResponse);
    }

    private void startPreferenceActivity() {
        Intent intent = null;
        if (!PreferenceManager.getIsPreferencesSaved()) {
            if (accessToken == null) {
                accessToken = PreferenceManager.getAccessToken();
            }
            getVegnonvegPreferences();
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void getVegnonvegPreferences() {
        try {
            JSONObject jsonObject = new JSONObject();
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("Authorization", "Bearer " + accessToken);
            client.setTimeout(Constant.TIMEOUT);
            client.post(LoginActivity.this, URLs.GET_VEGNONVEG_PREFERENCES, entity, "application/json", new LoginActivity.GetPreferenceHandler());
        } catch (UnsupportedEncodingException e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        } catch (Exception e) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    public class GetPreferenceHandler extends AsyncHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            VegnonvegPreferenceResponse response = new Gson().fromJson(new String(responseBody), VegnonvegPreferenceResponse.class);
            if (response.isResult()) {
                MyPreferences myPreferences = new MyPreferences();
                myPreferences.setType(response.getId());
                Intent intent = new Intent(LoginActivity.this, MyPreferencesActivity.class);
                intent.putExtra(MyPreferencesActivity.MY_PREFERENCES, myPreferences);
                startActivity(intent);
                finish();
            } else {
                AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            AlertMessages.showError(mContext, mContext.getString(R.string.genericError));
        }
    }

    private void setFont() {
        tvNoInternet.setTypeface(Utils.getRegularFont(this));
        fbLoginBtn.setTypeface(Utils.getRegularFont(this));
        googleLoginBtn.setTypeface(Utils.getRegularFont(this));
    }

    public void asyncStart() {
        progress.setVisibility(View.VISIBLE);
    }

    public void asyncEnd() {
        progress.setVisibility(View.INVISIBLE);
    }
}
