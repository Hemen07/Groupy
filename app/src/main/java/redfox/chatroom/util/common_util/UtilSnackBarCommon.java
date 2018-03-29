package redfox.chatroom.util.common_util;

import android.content.Context;
import android.util.Log;


public class UtilSnackBarCommon {

    private final static String TAG = UtilSnackBarCommon.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;


    public static void snackBarCommon(Context context, String message) {
        if (LOG_DEBUG) Log.w(TAG, "snackbarCommon()");

        if (context != null) {

            switch (message) {
                case UtilConstant.PWD_RESET_SUCCESS_MSG:
                 /*   styleSnackBar(message, context, ContextCompat.getColor(context, R.color.snackbar_pwd_reset_success)
                            , Color.BLACK);*/
                    break;
                default:
                    break;
            }
        }
    }

  /*  private static void styleSnackBar(String message, Context context, int backgroundColor, int textColor) {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) ((Activity) context).findViewById(R.id.pwd_reset_Coord);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, 6000);

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

    }*/

}
