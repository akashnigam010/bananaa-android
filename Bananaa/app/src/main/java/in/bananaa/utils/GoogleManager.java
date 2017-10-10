package in.bananaa.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import in.bananaa.R;

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
                .requestServerAuthCode(activity.getString(R.string.google_server_client_id), false)
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
