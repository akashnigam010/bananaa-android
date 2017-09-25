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

import in.bananaa.utils.AlertMessages;
import in.bananaa.utils.FacebookManager;
import in.bananaa.utils.GoogleManager;
import in.bananaa.utils.Utils;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;

    TextView tvSkip;
    Button fbLoginBtn;
    Button googleLoginBtn;
    AlertMessages messages;

    FacebookManager facebookManager;
    GoogleManager googleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            //startMainActivity();
            googleManager.signOut();
            facebookManager.logout();
        }
    };

    View.OnClickListener onFbSignIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                //messages.showErrornInConnection();
                messages.showCustomMessage("No Internet Connection");
                return;
            }
            facebookManager.login(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    messages.showCustomMessage("Logged In.. yay!");
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
            if (!Utils.isInternetConnected(LoginActivity.this)) {
                //messages.showErrornInConnection();
                messages.showCustomMessage("No con");
                return;
            } else {
                //signIn();
                googleManager.signIn();
                return;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookManager.onActivityResult(requestCode, resultCode, data);
        googleManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
        startActivity(intent);
        finish();

    }

    private void setFont() {
        tvSkip.setTypeface(Utils.getRegularFont(this));
        fbLoginBtn.setTypeface(Utils.getRegularFont(this));
        googleLoginBtn.setTypeface(Utils.getRegularFont(this));
    }
}
