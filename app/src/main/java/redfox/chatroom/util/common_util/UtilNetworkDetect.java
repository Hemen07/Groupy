package redfox.chatroom.util.common_util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class UtilNetworkDetect {


    private final static String TAG = UtilNetworkDetect.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    //takes Application Context,use it anywhere
    public static boolean checkOnline(Context context) {
        try {
            boolean isNetAvailable = false;

            ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {

                    isNetAvailable = netInfo.isConnected();

                    if (LOG_DEBUG) {
                        System.out.println(TAG + " net available :-  " + netInfo.isAvailable());
                        System.out.println(TAG + " net connected :-  " + netInfo.isConnected());
                        System.out.println(TAG + " net type      :-  " + netInfo.getTypeName());
                    }
                }
            }

            return isNetAvailable;

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

}
