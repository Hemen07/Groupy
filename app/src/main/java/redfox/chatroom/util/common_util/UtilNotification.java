package redfox.chatroom.util.common_util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import redfox.chatroom.R;
import redfox.chatroom.ui.main_screen.MainActivity;


public class UtilNotification {

    private final static String TAG = UtilNotification.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;

    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 10;
    private Notification notification;

    //    Channel
    private static final String NOTIFICATION_CHANNEL_ID = "REMAINDER_CHANNEL_ID";
    private static final String NOTIFICATION_CHANNEL_NAME = "REMAINDER_CHANNEL_NAME";
    private static final String NOTIFICATION_CHANNEL_DESCRIPTION = "REMAINDER_CHANNEL_DESCRIPTION";

    //    Group
    private static final String NOTIFICATION_GROUP_ID = "REMAINDER_GROUP";
    private static final String NOTIFICATION_GROUP_NAME = "REMAINDER_GROUP_NAME";


    private static final int ACTIVITY_REQ_CODE = 22;


    public void createNotification(Context context, String userName, String userTweet, long whenScheduled) {

        if (LOG_DEBUG) Log.e(TAG, " createNotification()");
        Context mContext = context.getApplicationContext();

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent panelPI = setUpPendingIntent(mContext);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            if (LOG_DEBUG) Log.v(TAG, " : version : <=M ");

            //noinspection deprecation
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(userName)
                    .setContentText(userTweet)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setLights(Color.CYAN, 500, 1200)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(panelPI);

            notification = builder.build();

        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N | Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            if (LOG_DEBUG) Log.v(TAG, " : version : N| N1 - 24: 25 ");

            //noinspection deprecation
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setContentTitle(userName)
                    .setContentText(userTweet)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setLights(Color.CYAN, 500, 1200)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(panelPI);

            notification = builder.build();


        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (LOG_DEBUG) Log.v(TAG, " : version : >=O ");


            NotificationChannel mChannel = new NotificationChannel
                    (NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);

            mChannel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.CYAN);
            // mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);

            NotificationChannelGroup mGroup = new NotificationChannelGroup(NOTIFICATION_GROUP_ID, NOTIFICATION_GROUP_NAME);
            notificationManager.createNotificationChannelGroup(mGroup);

            NotificationCompat.Builder builder = new NotificationCompat.Builder
                    (mContext, NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(userName)
                    .setContentText(userTweet)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setContentIntent(panelPI);

            notification = builder.build();
        }

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }


    private PendingIntent setUpPendingIntent(Context mContext) {
        Intent intent = new Intent(mContext, MainActivity.class);
        return PendingIntent.getActivity(mContext, ACTIVITY_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


}
