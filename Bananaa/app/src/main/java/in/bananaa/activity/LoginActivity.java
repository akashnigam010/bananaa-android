package in.bananaa.activity;

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

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.bananaa.R;
import in.bananaa.object.login.ClientType;
import in.bananaa.object.login.LoginResponse;
import in.bananaa.utils.Constant;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.URLs;
import in.bananaa.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    AppCompatActivity mContext;
    TextView tvLogin;
    ProgressBar progress;
    Button fbLoginBtn;
    Button googleLoginBtn;
    TextView tvNoInternet;
    TextView tvDisclaimer;

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
            startActivity();
        }

        facebookManager = new FacebookManager(this);
        googleManager = new GoogleManager(this);

        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        progress = (ProgressBar) findViewById(R.id.loginLoader);
        fbLoginBtn = (Button) findViewById(R.id.fbLogin);
        googleLoginBtn = (Button) findViewById(R.id.googleLogin);
        tvDisclaimer = (TextView) findViewById(R.id.tvDisclaimer);
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
                    Utils.exceptionOccurred(mContext, error);
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
        if (!Utils.checkIfInternetConnectedAndToast(this)) {
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject();
            asyncStart();
            jsonObject.put("accessToken", authCode);
            jsonObject.put("client", clientType);
            StringEntity entity = new StringEntity(jsonObject.toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(Constant.TIMEOUT);
            client.post(LoginActivity.this, URLs.LOGIN, entity, "application/json", new LoginActivity.LoginResponseHandler());
        } catch (Exception e) {
            Utils.exceptionOccurred(this, e);
        }
    }

    public class LoginResponseHandler extends AsyncHttpResponseHandler {

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
            asyncEnd();
            LoginResponse response = new Gson().fromJson(new String(responseBody), LoginResponse.class);
            if (response.isResult()) {
                saveLoginDetails(response);
                startActivity();
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

    private void saveLoginDetails(LoginResponse loginResponse) {
        PreferenceManager.putLoginDetails(loginResponse);
    }

    private void startActivity() {
        Intent intent = null;
        if (PreferenceManager.getIsPreferencesSaved()) {
            intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(LoginActivity.this, MyPreferencesActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setFont() {
        tvLogin.setTypeface(Utils.getBold(this));
        tvNoInternet.setTypeface(Utils.getRegularFont(this));
        fbLoginBtn.setTypeface(Utils.getRegularFont(this));
        googleLoginBtn.setTypeface(Utils.getRegularFont(this));
        tvDisclaimer.setTypeface(Utils.getRegularFont(this));
    }

    public void asyncStart() {
        progress.setVisibility(View.VISIBLE);
    }

    public void asyncEnd() {
        progress.setVisibility(View.INVISIBLE);
    }
}
