package e.nottdar.androidchat.util.common_util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import e.nottdar.androidchat.R;
import e.nottdar.androidchat.util.firebase_util.UtilConstants;


public class UtilSnackBarAuth {

    private final static String TAG = UtilSnackBarAuth.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;


    public static void snackbarAuth(Context context, String message) {
        if (LOG_DEBUG) Log.w(TAG, "snackbarAuth()");

        if (context != null) {
            switch (message) {
                case UtilConstants.LOGIN_EMAIL_NOT_REGISTERED_MSG:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_not_registered), Color.WHITE, false);
                    break;
                case UtilConstants.LOGIN_PASSWORD_WRONG_MSG:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_cred_not_match), Color.WHITE, true);
                    break;
                case UtilConstants.LOGIN_SUCCESS_MSG:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_login_success), Color.WHITE, false);
                    break;
                case UtilConstants.SIGN_UP_EMAIL_ALREADY_REGISTERED_MSG:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_signup_already_register), Color.BLACK, false);
                    break;
                default:
            }
        }
    }

    public static void styleSnackBar(String message, Context context, int backgroundColor, int textColor, boolean actionVisible) {
        Snackbar snackbar = null;
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorId);


        if (actionVisible == true) {
            snackbar = Snackbar.make(coordinatorLayout, message, 6000);

            snackbar.setActionTextColor(Color.WHITE);
            snackbar.setAction("Tap to Reset", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("booo action");
                }
            });
        } else {
            snackbar = Snackbar.make(coordinatorLayout, message, 4000);
        }
        View view = snackbar.getView();
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.caviar_dreams);

        view.setBackgroundColor(backgroundColor);
        textView.setTextColor(textColor);
        textView.setTextSize(14);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTypeface(typeface);
        snackbar.show();

    }

}
