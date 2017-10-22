package in.bananaa.utils;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import in.bananaa.object.login.LoginResponse;
import in.bananaa.object.login.LoginUserDto;

public class PreferenceManager extends Application implements Application.ActivityLifecycleCallbacks {
    private static final String BANANAA = "Bananaa";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String ID = "id";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String IMAGE_URL = "imageUrl";
    private static final String IS_PREFERENCES_SAVED = "isPreferencesSaved";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String IS_FIRST_TIME_LAUNCH = "isFirstLaunch";

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

        prefEditor.putBoolean(IS_LOGGED_IN, true);
        prefEditor.putInt(ID, loginResponse.getUser().getId());
        prefEditor.putString(FIRST_NAME, loginResponse.getUser().getFirstName());
        prefEditor.putString(LAST_NAME, loginResponse.getUser().getLastName());
        prefEditor.putString(IMAGE_URL, loginResponse.getUser().getImageUrl());
        prefEditor.putString(ACCESS_TOKEN, loginResponse.getAccessToken());
        prefEditor.commit();
    }

    public static Boolean isUserLoggedIn() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }

    public static void setFirstTimeLaunch(Boolean value) {
        prefEditor.putBoolean(IS_FIRST_TIME_LAUNCH, value);
        prefEditor.commit();
    }

    public static Boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public static String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, null);
    }

    public static Boolean getIsPreferencesSaved() {
        return preferences.getBoolean(IS_PREFERENCES_SAVED, false);
    }

    public static void setIsPreferencesSaved(Boolean value) {
        prefEditor.putBoolean(IS_PREFERENCES_SAVED, value);
        prefEditor.commit();
    }

    public static LoginUserDto getLoginDetails() {
        LoginUserDto user = new LoginUserDto();
        user.setId(preferences.getInt(ID, 0));
        user.setFirstName(preferences.getString(FIRST_NAME, null));
        user.setLastName(preferences.getString(LAST_NAME, null));
        user.setImageUrl(preferences.getString(IMAGE_URL, null));
        user.setPreferencesSaved(preferences.getBoolean(IS_PREFERENCES_SAVED, false));
        return user;
    }

    public static void resetLoginDetails() {
        prefEditor.putBoolean(IS_LOGGED_IN, false);
        prefEditor.putInt(ID, 0);
        prefEditor.putString(FIRST_NAME, null);
        prefEditor.putString(LAST_NAME, null);
        prefEditor.putString(IMAGE_URL, null);
        prefEditor.putString(ACCESS_TOKEN, null);
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
