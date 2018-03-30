package redfox.chatroom.ui.main_screen;

import android.content.Context;
import android.os.Process;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import redfox.chatroom.data.models.MessageModel;
import redfox.chatroom.ui.interfaces.LoadMessagesCallbacks;
import redfox.chatroom.util.common_util.UtilConstant;

public class LoadMessageThread {

    private static final boolean LOG_DEBUG = false;
    private final static String TAG = LoadMessageThread.class.getSimpleName();

    private Context mContext;//use only when you sure your app is in foreground else use thread
    private LoadMessagesCallbacks mCallbacks;
    private DataSnapshot dataSnapshot;
    private List<MessageModel> messageModelList;
    private String whichOperation;//one time or refresh
    private Thread myWorker = null;

    public LoadMessageThread(Context mContext, LoadMessagesCallbacks mCallbacks) {
        this.mContext = mContext;
        this.mCallbacks = mCallbacks;

    }

    public void loadMessagesToListInBG(DataSnapshot mDataSnapshot, List<MessageModel> msgList, String whichOperation) {
        if (LOG_DEBUG)
            System.out.println("---------------------loadMessageThread------------------------");
        this.dataSnapshot = mDataSnapshot;
        this.messageModelList = msgList;
        this.whichOperation = whichOperation;
        myWorker = new Thread(new Runnable() {
            @Override
            public void run() {

                if (mContext != null) {

                    messageModelList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        MessageModel data = item.getValue(MessageModel.class);
                        messageModelList.add(data);
                    }

                    if (messageModelList != null && messageModelList.size() > 0)
                        filledList();
                    else
                        notFilledList();
                }
            }
        });
        myWorker.start();
        myWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    //one time or refresh
    private void filledList() {
        if (LOG_DEBUG) System.out.println(" FILLED() :- " + messageModelList.size());
        if (whichOperation.equalsIgnoreCase(UtilConstant.ONE_TIME_OPERATION))
            mCallbacks.loadMessagesInBG("FILLED", UtilConstant.ONE_TIME_OPERATION);
        else
            mCallbacks.loadMessagesInBG("FILLED", UtilConstant.REFRESH_OPERATION);

    }

    private void notFilledList() {
        if (LOG_DEBUG) System.out.println(" NOT FILLED() :- " + messageModelList.size());

        if (whichOperation.equalsIgnoreCase(UtilConstant.ONE_TIME_OPERATION))
            mCallbacks.loadMessagesInBG("NOT FILLED", UtilConstant.ONE_TIME_OPERATION);
        else
            mCallbacks.loadMessagesInBG("NOT FILLED", UtilConstant.REFRESH_OPERATION);
    }

    public void destroyLoadMessageThread() {
        if (myWorker != null) {
            Thread anotherThread = myWorker;
            anotherThread.interrupt();
            myWorker = null;
        }
    }


}