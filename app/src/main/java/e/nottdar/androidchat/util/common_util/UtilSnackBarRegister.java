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


public class UtilSnackBarRegister {

    private final static String TAG = UtilSnackBarRegister.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;


    public static void snackBarRegister(Context context, String message) {
        if (LOG_DEBUG) Log.w(TAG, "snackBarRegister()");

        if (context != null) {

            switch (message) {
                case UtilCommonConstant.EMAIL_EMPTY:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_email_check)
                            , Color.WHITE);
                    break;
                case UtilCommonConstant.EMAIL_INVALID:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_email_check)
                            , Color.WHITE);
                    break;
                case UtilCommonConstant.PASSWORD_EMPTY:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_pass_check)
                            , Color.BLACK);
                    break;
                case UtilCommonConstant.PASSWORD_LESS_6:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_pass_check)
                            , Color.BLACK);
                    break;
                case UtilCommonConstant.PASSWORD_LESS_20:
                    styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_pass_check)
                            , Color.BLACK);
                    break;
                default:
                    break;
            }
        }
    }

    private static void styleSnackBar(String message, Context context, int backgroundColor, int textColor) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((Activity) context).findViewById(R.id.SignUp_coodrinator);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT);

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
