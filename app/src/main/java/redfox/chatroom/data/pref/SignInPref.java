package redfox.chatroom.data.pref;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import redfox.chatroom.util.common_util.UtilConstant;


public class SignInPref {
    private final static String TAG = SignInPref.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    private Context context;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static SignInPref mInstance;


    public static synchronized SignInPref getmInstance(Context context) {
        if (LOG_DEBUG) Log.d(TAG, " -- getInstance() -- ");

        if (mInstance == null) {
            // System.out.println(" instance = null");
            mInstance = new SignInPref(context.getApplicationContext());
        } else {
            //System.out.println(" instance NOT null : RETURN instance");
        }
        return mInstance;
    }


    @SuppressLint("CommitPrefEdits")
    private SignInPref(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(UtilConstant.SIGN_IN_PREF, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (LOG_DEBUG)
            Log.i(TAG, "ctor");
    }


    // status SignIn :
    public void saveSignInStatus(int status) {
        editor.putInt(UtilConstant.IS_SIGNED_IN, status);
        editor.apply();
    }

    public Integer getSignInStatus() {
        return sharedPreferences.getInt(UtilConstant.IS_SIGNED_IN, 0);
    }

    //Name and Email
    public void setUserName(String name) {
        editor.putString(UtilConstant.IS_SIGNED_NAME, name);
        editor.apply();
    }

    public void setUserEmail(String name) {
        editor.putString(UtilConstant.IS_SIGNED_EMAIL, name);
        editor.apply();
    }

    public void setUserUID(String uid) {
        editor.putString(UtilConstant.IS_SIGNED_UID, uid);
        editor.apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(UtilConstant.IS_SIGNED_NAME, UtilConstant.DEFAULT_NAME);
    }

    public String getUserEmail() {
        return sharedPreferences.getString(UtilConstant.IS_SIGNED_EMAIL, "DEFAULT_EMAIL");
    }

    public String getUSerUID() {
        return sharedPreferences.getString(UtilConstant.IS_SIGNED_UID, "DEFAULT_UID");
    }


}
