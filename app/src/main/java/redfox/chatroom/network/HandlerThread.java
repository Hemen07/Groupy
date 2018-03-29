package redfox.chatroom.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import redfox.chatroom.util.common_util.UtilNetworkDetect;


public class HandlerThread extends android.os.HandlerThread {
    private final static String TAG = HandlerThread.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;

    public Handler mhtHandler;

    private HandlerCallbacks handlerCallbacks;
    private Context context;


    public HandlerThread(HandlerCallbacks handlerCallbacks, Context context) {
        super("HandlerThread");
        if (LOG_DEBUG)
            Log.e(TAG, "HandlerThread");
        this.handlerCallbacks = handlerCallbacks;
        this.context = context;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (LOG_DEBUG)
                System.out.println("HT " + "UI");
        } else {
            if (LOG_DEBUG)
                System.out.println("HT " + "NON UI");
        }

        boolean netStatus = checkNet();
        handlerCallbacks.isNetAvailable(netStatus);

        mhtHandler = new Handler(getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (LOG_DEBUG)
                    Log.w(TAG, "inside handleMessage()");
                return true;
            }
        }) {
        };
    }

    private boolean checkNet() {
        boolean status = UtilNetworkDetect.checkOnline(context.getApplicationContext());
        if (status) {
            if (LOG_DEBUG)
                Log.w(TAG, " Online  ........... ");
        } else {
            if (LOG_DEBUG)
                Log.w(TAG, " -----------  Offline");
        }
        return status;
    }


}
