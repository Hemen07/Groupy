package redfox.chatroom.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import redfox.chatroom.R;
import redfox.chatroom.data.models.MessageModel;
import redfox.chatroom.ui.holders.ReceivedMessageHolder;
import redfox.chatroom.ui.holders.SentMessageHolder;
import redfox.chatroom.ui.interfaces.AdapterCallbacks;


public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = MessageAdapter.class.getSimpleName();
    private final static boolean LOG_DEBUG = false;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    private List<MessageModel> messageModelList = new ArrayList<>();
    private Context mContext;
    private AdapterCallbacks mAdapterCallbacks;
    private String who;
    private View v;


    public MessageAdapter(List<MessageModel> messageModelList, Context mContext, AdapterCallbacks mAdapterCallbacks, String who) {
        this.messageModelList = messageModelList;
        this.mContext = mContext;
        this.mAdapterCallbacks = mAdapterCallbacks;
        this.who = who;
    }


    @Override
    public int getItemViewType(int position) {
        MessageModel messageModel = messageModelList.get(position);

        boolean isItMe = (messageModel.getUserName().equals(who));

        if (isItMe) {
            if (LOG_DEBUG) System.out.println("       VIEW_TYPE_MESSAGE_SENT  ");
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            if (LOG_DEBUG) System.out.println("       VIEW_TYPE_MESSAGE_RECEIVED  ");
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            if (LOG_DEBUG) System.out.println("       onCreate()  -- VIEW_TYPE_MESSAGE_SENT  ");
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(v);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            if (LOG_DEBUG) System.out.println("       onCreate()  -- VIEW_TYPE_MESSAGE_RECEIVED  ");
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(v);
        }

        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder myViewHolder, final int position) {
        if (mContext != null) {
            MessageModel messageModel = messageModelList.get(position);
            if (messageModel != null) {

                switch (myViewHolder.getItemViewType()) {
                    case VIEW_TYPE_MESSAGE_SENT:
                        if (LOG_DEBUG)
                            System.out.println("       onBind()  -- VIEW_TYPE_MESSAGE_SENT  ");
                        ((SentMessageHolder) myViewHolder).bind(messageModel);
                        break;
                    case VIEW_TYPE_MESSAGE_RECEIVED:
                        if (LOG_DEBUG)
                            System.out.println("       onBind()  -- VIEW_TYPE_MESSAGE_RECEIVED  ");
                        ((ReceivedMessageHolder) myViewHolder).bind(mContext, messageModel);
                        break;
                    default:
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return (messageModelList != null ? messageModelList.size() : 0);
    }
}
