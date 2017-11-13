package in.bananaa.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import in.bananaa.R;
import in.bananaa.object.GenericResponse;
import in.bananaa.object.Tag;
import in.bananaa.object.exception.ResponseFailureException;
import in.bananaa.object.exception.StatusFalseException;
import in.bananaa.object.location.LocationStore;
import in.bananaa.object.location.LocationType;

import static in.bananaa.utils.Constant.FACEBOOK_PAGE_ID;
import static in.bananaa.utils.Constant.FACEBOOK_URL;
import static in.bananaa.utils.Constant.INSTAGRAM_URL;
import static in.bananaa.utils.Constant.PRIVACY_URL;
import static in.bananaa.utils.Constant.TNC_URL;
import static in.bananaa.utils.Constant.TWITTER_URL;
import static in.bananaa.utils.Constant.TWITTER_USERNAME;

public class Utils {
    public static int[] chipsBackgrounds = new int[]{
            R.drawable.bg_chip1,
            R.drawable.bg_chip2,
            R.drawable.bg_chip3,
            R.drawable.bg_chip4,
            R.drawable.bg_chip5,
            R.drawable.bg_chip6,
            R.drawable.bg_chip7,
            R.drawable.bg_chip8,
            R.drawable.bg_chip9,
            R.drawable.bg_chip10
    };

    public static void setMenuItemsFont(Menu menu, Typeface font, Context mContext) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem, font);
                }
            }
            applyFontToMenuItem(mi, font);
        }
    }

    private static void applyFontToMenuItem(MenuItem mi, Typeface font) {
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public static Typeface getRegularFont(Context mContext) {
        return Typeface.createFromAsset(mContext.getAssets(), "bna-regular.ttf");
    }

    public static Typeface getBold(Context mContext) {
        return Typeface.createFromAsset(mContext.getAssets(), "bna-bold.ttf");
    }

    public static Typeface getSimpsonFont(Context mContext) {
        return Typeface.createFromAsset(mContext.getAssets(), "bna-simpson.ttf");
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean checkIfInternetConnectedAndToast(Activity activity) {
        if (!isInternetConnected(activity)) {
            Toast.makeText(activity, activity.getString(R.string.noInternet), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public static void genericErrorToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        Crashlytics.setUserIdentifier(PreferenceManager.getLoginDetails().getId().toString());
        Crashlytics.log(1, activity.getClass().getSimpleName(), message);
    }

    public static void exceptionOccurred(Activity activity, Exception e) {
        Toast.makeText(activity, activity.getString(R.string.genericError), Toast.LENGTH_SHORT).show();
        Crashlytics.setUserIdentifier(PreferenceManager.getLoginDetails().getId().toString());
        Crashlytics.log(1, activity.getClass().getSimpleName(), e.getMessage());
        Crashlytics.logException(e);
    }

    public static void responseError(Activity activity, GenericResponse response) {
        Toast.makeText(activity, activity.getString(R.string.genericError), Toast.LENGTH_SHORT).show();
        Crashlytics.setUserIdentifier(PreferenceManager.getLoginDetails().getId().toString());
        Crashlytics.log(2, activity.getClass().getSimpleName(), "Response status false : " + response.getClass().getSimpleName());
        Crashlytics.logException(new StatusFalseException("Response status false"));
    }

    public static void responseFailure(Activity activity) {
        Toast.makeText(activity, activity.getString(R.string.genericError), Toast.LENGTH_SHORT).show();
        Crashlytics.setUserIdentifier(PreferenceManager.getLoginDetails().getId().toString());
        Crashlytics.log(3, activity.getClass().getSimpleName(), "Response failed : " + activity.getClass().getSimpleName());
        Crashlytics.logException(new ResponseFailureException("Response failed"));
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.toString().trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static String parseListToCommaSeparatedString(List<String> list) {
        String finalStr = "";
        if (list.size() > 0) {
            int i;
            for (i=0; i<list.size()-1; i++) {
                finalStr += list.get(i) + ", ";
            }

            finalStr += list.get(i);
        }
        return finalStr;
    }

    public static String getUserLevel(Float level) {
        if (level <= 2.5) {
            return "Beginner";
        } else if (level > 2.5 && level <= 3.0) {
            return "Advance Foodviewer";
        } else if (level > 3.0 && level <= 4.0) {
            return "Super Foodviewer";
        } else {
            return "Expert Foodviewer";
        }
    }

    public static String getTagsString(List<Tag> tags) {
        String str = "";
        switch(tags.size()) {
            case 0: return "";
            case 1: return tags.get(0).getName();
            case 2: return tags.get(0).getName() + " and " + tags.get(1).getName();
            default: int i=0;
                for (i=0; i<tags.size()-2; i++) {
                    str += tags.get(i).getName() + ", ";
                }
                str += tags.get(i).getName() + " and " + tags.get(i+1).getName();
                return str;
        }
    }

    public static String loadDefaultLocation() {
        LocationStore locationStore = new LocationStore(1, "Hyderabad", LocationType.CITY);
        PreferenceManager.setStoredLocation(locationStore);
        return locationStore.getName();
    }

    public static void shareToOtherApps(Context mContext, String url, boolean isRestaurant) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (isRestaurant) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this restaurant at Bananaa - " + url);
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Checkout this awesome dish at Bananaa - " + url);
        }
        sendIntent.setType("text/plain");
        mContext.startActivity(sendIntent);
    }

    public static Intent getFacebookPageURL() {
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + FACEBOOK_PAGE_ID));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
        }
    }

    public static Intent getTwitterIntent() {
        try {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=".concat(TWITTER_USERNAME)));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse(TWITTER_URL));
        }
    }

    public static Intent getInstagramIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(INSTAGRAM_URL));
    }

    public static Intent getTncIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(TNC_URL));
    }

    public static Intent getPrivacyIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_URL));
    }

    public static void openContactUsApplication(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:contact@bananaa.in?subject=" + "Send Feedback to Bananaa" + "&body=");
        intent.setData(data);
        context.startActivity(intent);
    }

    public static void openPlayStoreForRating(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}
