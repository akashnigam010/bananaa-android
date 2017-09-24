package in.bananaa.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FacebookManager {
    private Activity activity;
    private CallbackManager callbackManager;

    public FacebookManager(Activity activity) {
        this.activity = activity;
        initFacebook();
    }

    public void getHashKey() {

        // Add code to print out the key hash
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(
                    "" + activity.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        }
    }

    private void initFacebook() {
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public void login(final FacebookCallback<LoginResult> onLoginListener) {

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
//        LoginManager.getInstance().logInWithPublishPermissions(activity, Arrays.asList("publish_actions"));
        LoginManager.getInstance().registerCallback(callbackManager, onLoginListener);

    }

    public void logout() {
        LoginManager.getInstance().logOut();
    }

    public void shareDialog(String title, String description, String contentURL) {
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse(contentURL))
                    .build();
            ShareDialog shareDialog = new ShareDialog(activity);
            shareDialog.show(linkContent);

        }

    }


    public void shareLinks(String url, FacebookCallback<Sharer.Result> onShareListener) {
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .build();
        ShareApi.share(content, onShareListener);
    }

    public void sharePhoto(String title, Bitmap image, FacebookCallback<Sharer.Result> onShareListener) {

        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image).setCaption(title)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, onShareListener);
    }

    public void shareVideo(String title, File videoFile, FacebookCallback<Sharer.Result> onShareListener) {
        Uri videoFileUri = Uri.fromFile(videoFile);
        ShareVideo video = new ShareVideo.Builder()
                .setLocalUrl(videoFileUri)
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(video).setContentTitle(title)
                .build();
        ShareApi.share(content, onShareListener);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
