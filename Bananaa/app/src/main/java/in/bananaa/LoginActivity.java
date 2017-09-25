package in.bananaa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.PreferenceManager;
import in.bananaa.utils.Utils;
import in.bananaa.utils.login.LoginDetails;
import in.bananaa.utils.login.LoginMethod;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    TextView tvSkip;
    Button fbLoginBtn;
    Button googleLoginBtn;
    AlertMessages messages;

    FacebookManager facebookManager;
    GoogleManager googleManager;

    Boolean isFbLogin = false;
    Boolean isGoogleLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (PreferenceManager.doSkipLoginScreen()) {
            startMainActivity();
        }

        facebookManager = new FacebookManager(this);
        googleManager = new GoogleManager(this);

        messages = new AlertMessages(this);
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        fbLoginBtn = (Button) findViewById(R.id.fbLogin);
        googleLoginBtn = (Button) findViewById(R.id.googleLogin);
        setFont();
        tvSkip.setOnClickListener(onClickListenerSkip);
        fbLoginBtn.setOnClickListener(onFbSignIn);
        googleLoginBtn.setOnClickListener(onGoogleSignIn);
    }

    View.OnClickListener onClickListenerSkip = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveLoginDetails(true, false, null);
            startMainActivity();
        }
    };

    View.OnClickListener onFbSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isFbLogin = true;
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                messages.showCustomMessage("No Internet Connection");
                return;
            }
            facebookManager.login(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    saveLoginDetails(false, true, LoginMethod.FACEBOOK);
                    startMainActivity();
                }

                @Override
                public void onCancel() {
                    messages.showCustomMessage("FB Login cancelled");
                }

                @Override
                public void onError(FacebookException error) {
                    messages.showCustomMessage("Some error occurred - fb login");
                }
            });
        }
    };

    View.OnClickListener onGoogleSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            isGoogleLogin = true;
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                messages.showCustomMessage("No connection");
                return;
            } else {
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
                saveLoginDetails(false, true, LoginMethod.GOOGLE);
                startMainActivity();
            } else {
                messages.showCustomMessage("Login failed");
            }
        }
    }

    private void saveLoginDetails(Boolean isSkippedLogin, Boolean isLoggedIn, LoginMethod loginMethod) {
        LoginDetails loginDetails = new LoginDetails(isSkippedLogin, isLoggedIn, loginMethod);
        PreferenceManager.putLoginDetails(loginDetails);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setFont() {
        tvSkip.setTypeface(Utils.getRegularFont(this));
        fbLoginBtn.setTypeface(Utils.getRegularFont(this));
        googleLoginBtn.setTypeface(Utils.getRegularFont(this));
    }
}
