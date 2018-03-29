package redfox.chatroom.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import redfox.chatroom.network.HandlerThread;
import redfox.chatroom.network.HandlerCallbacks;
import redfox.chatroom.network.NetMonitorCallbacks;


public class ConstantNetMonitorReceiver extends BroadcastReceiver implements HandlerCallbacks {

    private final static String TAG = ConstantNetMonitorReceiver.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    //A Continuous net change detector
    private NetMonitorCallbacks mObserverCallBack;

    private HandlerThread handlerThread;


    public ConstantNetMonitorReceiver(NetMonitorCallbacks mObserverCallBack) {
        this.mObserverCallBack = mObserverCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (LOG_DEBUG)
            Log.i(TAG, " onReceive()");
        handlerThread = new HandlerThread(this, context.getApplicationContext());
        handlerThread.start();

    }


    @Override
    public void isNetAvailable(boolean isAvailable) {

        if (LOG_DEBUG)
            Log.i(TAG, " isNetAvailable()");

        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (LOG_DEBUG)
                System.out.println(TAG + " : UI ");
        } else {
            if (LOG_DEBUG)
                System.out.println(TAG + " : NON UI");
        }
        mObserverCallBack.isContinuousNetCheck(isAvailable);
    }

    public void quitHandlerThread() {
        if (handlerThread.mhtHandler != null) {
            handlerThread.mhtHandler.removeCallbacks(null);
            handlerThread.mhtHandler.getLooper().quit();
            Thread thread = handlerThread;
            thread.interrupt();
            handlerThread = null;
        }
    }
}