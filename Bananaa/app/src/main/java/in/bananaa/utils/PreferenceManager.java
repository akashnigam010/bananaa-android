package in.bananaa.utils;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import in.bananaa.utils.login.LoginResponse;

public class PreferenceManager extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String BANANAA = "Bananaa";
    private static final String IS_SKIPPED_LOGIN = "isSkippedLogin";
    private static final String IS_LOGGED_IN = "isLoggedIn";

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor prefEditor;

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(BANANAA, MODE_PRIVATE);
        prefEditor = preferences.edit();
        prefEditor.commit();
    }

    public static void putLoginDetails(LoginResponse loginResponse) {
        if (loginResponse == null) {
            return;
        }

        //putIsLoggedIn(loginDetails.isLoggedIn());
        //putIsSkippedLogin(loginDetails.isSkippedLogin());
    }

    public static Boolean isSkipLoginScreen() {
        return (preferences.getBoolean(IS_LOGGED_IN, false) ||
                preferences.getBoolean(IS_SKIPPED_LOGIN, false)) ? true : false;
    }

    public static void resetLoginDetails() {
        putIsLoggedIn(false);
        putIsSkippedLogin(false);
    }

    private static void putIsLoggedIn(Boolean isLoggedIn) {
        prefEditor.putBoolean(IS_LOGGED_IN, isLoggedIn);
        prefEditor.commit();
    }

    private static void putIsSkippedLogin(Boolean isSkippedLogin) {
        prefEditor.putBoolean(IS_SKIPPED_LOGIN, isSkippedLogin);
        prefEditor.commit();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
