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
import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.Constant;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;
import in.bananaa.utils.login.ClientType;
import in.bananaa.utils.login.LoginResponse;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (PreferenceManager.isUserLoggedIn()) {
            startMainActivity();
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

    private void doLogin(String accessToken, ClientType clientType) {
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("accessToken", accessToken);
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
                startMainActivity();
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

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MyPreferencesActivity.class);
        startActivity(intent);
        finish();
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
