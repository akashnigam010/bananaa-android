package in.bananaa.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;

import in.bananaa.R;
import in.bananaa.utils.login.ClientType;

import static com.facebook.FacebookSdk.getApplicationContext;

public class GoogleManager implements
        GoogleApiClient.OnConnectionFailedListener {
    private static final String G_LOGIN = "GoogleLogin";
    private static final int RC_SIGN_IN = 9001;

    private FragmentActivity activity;
    private GoogleApiClient mGoogleApiClient;

    public GoogleManager(FragmentActivity activity) {
        this.activity = activity;
        initGoogleLogin();
    }

    private void initGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.google_server_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Debug.e(G_LOGIN, "Connection failed");
    }

    public GoogleSignInResult onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();
            String scope = "oauth2:"+ Scopes.EMAIL+" "+ Scopes.PROFILE;
            String mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            String mType = data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            // With the account name acquired, go get the auth token
            Account account = new Account(mEmail, mType);
            String accessToken = null;
            try {
                accessToken = GoogleAuthUtil.getToken(getApplicationContext(), account, scope, new Bundle());
                doLogin(accessToken, ClientType.GOOGLE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }

    public void login() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void logout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Debug.e(G_LOGIN, "Logged Out");
                    }
                });
    }
}
