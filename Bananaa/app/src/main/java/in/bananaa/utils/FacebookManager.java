package in.bananaa.utils;

import android.app.Activity;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

public class FacebookManager {
    private Activity activity;
    private CallbackManager callbackManager;

    public FacebookManager(Activity activity) {
        this.activity = activity;
        initFacebook();
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public void login(final FacebookCallback<LoginResult> onLoginListener) {
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, onLoginListener);
    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
