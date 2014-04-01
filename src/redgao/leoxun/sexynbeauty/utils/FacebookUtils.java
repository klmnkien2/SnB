package redgao.leoxun.sexynbeauty.utils;

//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//
//import org.json.JSONObject;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.content.pm.Signature;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
//import android.util.Base64;
//import android.util.Log;
//import android.view.View;
//
//import com.facebook.FacebookRequestError;
//import com.facebook.HttpMethod;
//import com.facebook.LoggingBehavior;
//import com.facebook.Request;
//import com.facebook.RequestAsyncTask;
//import com.facebook.Response;
//import com.facebook.Session;
//import com.facebook.SessionState;
//import com.facebook.Settings;
//import com.facebook.UiLifecycleHelper;
//import com.facebook.Session.StatusCallback;

public final class FacebookUtils extends ActionBarActivity {
    /*
     * Use to upload photo to your facebook
     */
//    private void sharePhoto(Bitmap image) {
//        Request request = Request.newUploadPhotoRequest(session, image, new Request.Callback() {
//            @Override
//            public void onCompleted(Response response) {
//                FacebookUtils.this.displayAlert("Warning", "Success FB!");
//            }
//        });
//        request.executeAsync();
//    }
    
    /*
     * Use to post a status to your facebook
     */
    
//    private Session session;
//    private static final String FB_APP_ID = "483436361760813";
//    private static final String PENDING_REQUEST_BUNDLE_KEY = "gag.facebook:PendingRequest";
//    private boolean pendingRequest;
//    
//    public void setupFacebookOnCreate(Bundle savedInstanceState) {        
//        this.session = createSession();
//        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
//    }
//    
//    public void facebook(View v) {
//        if (this.session.isOpened()) {
//            shareLinkViaStatus();
//        } else {
//            StatusCallback callback = new StatusCallback() {
//                public void call(Session session, SessionState state, Exception exception) {
//                    if (exception != null) {
//                        FacebookUtils.this.displayAlert("Warning", "Fail to login with FB: " + exception.getMessage());
//                        FacebookUtils.this.session = createSession();
//                    }
//                }
//            };
//            pendingRequest = true;
//            this.session.openForRead(new Session.OpenRequest(this).setCallback(callback));
//        }
//    }
//    
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (this.session.onActivityResult(this, requestCode, resultCode, data) &&
//                pendingRequest &&
//                this.session.getState().isOpened()) {
//            shareLinkViaStatus();
//        }
//    }
//    
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        pendingRequest = savedInstanceState.getBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        uiHelper.onSaveInstanceState(outState);
//        outState.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
//    }
//    
//    private Session createSession() {
//        Session activeSession = Session.getActiveSession();
//        if (activeSession == null || activeSession.getState().isClosed()) {
//            activeSession = new Session.Builder(this).setApplicationId(FB_APP_ID).build();
//            Session.setActiveSession(activeSession);
//        }
//        return activeSession;
//    }
//    
//    private void shareLinkViaStatus() {
//        Bundle postParams = new Bundle();
//        postParams.putString("name", "Facebook SDK for Android");
//        postParams.putString("caption", "Build great social apps and get more installs.");
//        postParams.putString("description", getMessage());
//        postParams.putString("link", "https://developers.facebook.com/android");
//        postParams.putString("picture", currentViewUrl);
////        postParams.putString("access_token", session.getAccessToken());
//
//        final ProgressDialog dialog = new ProgressDialog(FacebookUtils.this);
//        dialog.setMessage("Loading ...");
//        dialog.setCancelable(false);
//        dialog.show();
//        
//        Request.Callback callback = new Request.Callback() {
//            public void onCompleted(Response response) {
//                FacebookRequestError error = response.getError();
//                if (error != null) {
//                    displayAlert("FACEBOOK ERROR", ""+ error.getErrorMessage());
//                } else {
//                    JSONObject graphResponse = response
//                            .getGraphObject()
//                            .getInnerJSONObject();
//                    String postId = null;
//                    try {
//                        postId = graphResponse.getString("id");
//                        displayAlert("FACEBOOK", ""+ postId);
//                    } catch (Exception e) {
//                        displayAlert("FACEBOOK ERROR", "Json exception");
//                    }
//                }
//                
//                if (dialog!=null) {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();       
//                    }
//                }
//            }
//        };
//
//        Request request = new Request(session, "me/feed",postParams, HttpMethod.POST, callback);
//        RequestAsyncTask task = new RequestAsyncTask(request);
//        task.execute();        
//    }
}
