package e.nottdar.androidchat.util.firebase_util;

/**
 * Created by notTdar on 1/9/2018.
 */

public class UtilConstants {
    //startActivityforResult

    public static final int ACTIVITY_REQ_CODE = 2;
    public static final int ACTIVITY_SUCCESS_CODE = 200;
    public static final int ACTIVITY_FAILED_CODE = 400;


    public static final String PARCEL_KEY = "PARCEL_KEY";
    public static final String FAILED_MSG_KEY = "FAILED_MSG_KEY";
    public static final String FAILED_MSG = "FAILED_MSG";


    //callback message for signIn and signUp with email and pass
    public static final String LOGIN_SUCCESS_MSG = "Successfully LOGGED IN.....YA";
    public static final String LOGIN_FAILED_MSG = "LOGIN_FAILED_MSG";
    public static final String LOGIN_PASSWORD_WRONG = "The password is invalid or the user does not have a password.";
    public static final String LOGIN_PASSWORD_WRONG_MSG = "Invalid Password ! Forgot ?";
    public static final String LOGIN_EMAIL_NOT_REGISTERED = "There is no user record corresponding to this identifier. The user may have been deleted.";
    public static final String LOGIN_EMAIL_NOT_REGISTERED_MSG = "You are not Registered ! Register below";


    public static final String SIGN_UP_SUCCESS_MSG = "Hooray!  Now You can LOG IN ";
    public static final String SIGN_UP_FAILED_MSG = "SIGN_UP_FAILED_MSG";
    public static final String SIGN_UP_EMAIL_ALREADY_REGISTERED = "The email address is already in use by another account.";
    public static final String SIGN_UP_EMAIL_ALREADY_REGISTERED_MSG = "You are already Registered, SIGN IN";


    public static final String PROFILE_SET_SUCCESS = "PROFILE_SET_SUCCESS";
    public static final String PROFILE_SET_FAILED = "PROFILE_SET_FAILED";

}
