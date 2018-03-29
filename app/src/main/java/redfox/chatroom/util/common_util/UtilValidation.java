package redfox.chatroom.util.common_util;

import android.content.Context;
import android.util.Patterns;

import java.util.regex.Pattern;

import redfox.chatroom.ui.interfaces.InputValidationCallbacks;


/**
 * Created by notTdar on 1/10/2018.
 */

public class UtilValidation {

    private static void callBack(Context context, String message, InputValidationCallbacks mCallbacks) {
        mCallbacks.inputErrorCallbacks(message);
    }

    private static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty();
    }

    public static boolean isValidEmail(Context context, String email, InputValidationCallbacks mCallbacks) {
        if (isNullOrEmpty(email)) {
            callBack(context, UtilConstant.EMAIL_EMPTY, mCallbacks);
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            callBack(context, UtilConstant.EMAIL_INVALID, mCallbacks);
        } else {
            return true;
        }
        return false;
    }

    public static boolean isValidPassword(Context context, String password, InputValidationCallbacks mCallbacks) {
        if (isNullOrEmpty(password)) {
            callBack(context, UtilConstant.PASSWORD_EMPTY, mCallbacks);
        } else if (password.length() < 6) {
            callBack(context, UtilConstant.PASSWORD_LESS_6, mCallbacks);
        } else if (password.length() > 20) {
            callBack(context, UtilConstant.PASSWORD_LESS_20, mCallbacks);
        } else {
            return true;
        }
        return false;
    }

   /* public static boolean isValidMobile(Context context, String mobile) {
        return isValidMobile(context, mobile, "^[0-9]{10}$");
    }

    public static boolean isValidMobile(Context context, String mobile, String regex) {
        if (isNullOrEmpty(mobile)) {
            callBack(context, "Please enter Mobile number first.");
        } else if (!Pattern.matches(regex, mobile)) {
            callBack(context, "Please enter a valid Mobile number.");
        } else {
            return true;
        }
        return false;
    }*/

    public static boolean isValidUsername(Context context, String username, InputValidationCallbacks mInputValidationCallbacks) {
        return isValidUsername(context, username, "^[a-zA-Z0-9._-]{3,20}$", mInputValidationCallbacks);
    }

    private static boolean isValidUsername(Context context, String username, String regex, InputValidationCallbacks mInputValidationCallbacks) {
        if (isNullOrEmpty(username)) {
            callBack(context, UtilConstant.USERNAME_EMPTY, mInputValidationCallbacks);
        } else if (!Pattern.matches(regex, username)) {
            callBack(context, UtilConstant.USERNAME_INVALID, mInputValidationCallbacks);
        } else {
            return true;
        }
        return false;
    }


}
