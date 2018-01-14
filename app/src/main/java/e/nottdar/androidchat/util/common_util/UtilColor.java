package e.nottdar.androidchat.util.common_util;

import android.content.Context;

import e.nottdar.androidchat.R;


public class UtilColor {
    private final static String TAG = UtilColor.class.getSimpleName();

    //    <!--Don't forget to change this boring colors-->


    public static int getRandomColor(Context context) {
      //  Log.e(TAG, "getRandomColor()");
        int[] colors = context.getResources().getIntArray(R.array.message_accent_colors);
        return colors[((int) (Math.random() * colors.length))];
    }



}
