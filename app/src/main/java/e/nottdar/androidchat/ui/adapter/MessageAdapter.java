package e.nottdar.androidchat.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import e.nottdar.androidchat.R;
import e.nottdar.androidchat.data.models.MessageModel;
import e.nottdar.androidchat.ui.interfaces.AdapterCallBack;
import e.nottdar.androidchat.util.common_util.UtilCal;
import e.nottdar.androidchat.util.common_util.UtilColor;
import e.nottdar.androidchat.util.common_util.UtilImage;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private final static String TAG = MessageAdapter.class.getSimpleName();
    private final static boolean LOG_DEBUG = true;


    public List<MessageModel> messageModelList = new ArrayList<>();
    private Context mContext;
    private AdapterCallBack mAdapterCallBack;
    private String who;
    private View v;


    public MessageAdapter(List<MessageModel> messageModelList, Context mContext, AdapterCallBack mAdapterCallBack, String who) {
        this.messageModelList = messageModelList;
        this.mContext = mContext;
        this.mAdapterCallBack = mAdapterCallBack;
        this.who = who;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_imv)
        ImageView messageImv;
        @BindView(R.id.message_user)
        TextView messageUser;
        @BindView(R.id.message_time)
        TextView messageTime;
        @BindView(R.id.message_text)
        TextView messageText;
        @BindView(R.id.message_container)
        RelativeLayout messageContainer;

        MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);
        final MyViewHolder myViewHolder = new MyViewHolder(v);

        myViewHolder.messageContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAdapterCallBack.onLongClick(myViewHolder.getAdapterPosition(), v);
                return true;
            }
        });

        return myViewHolder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, final int position) {
        if (mContext != null) {
            MessageModel messageModel = messageModelList.get(position);

            boolean whoTheFuck = (messageModel.getUserName().equals(who));
            whoTheFuckOperation(whoTheFuck, myViewHolder, messageModel);

            myViewHolder.messageText.setText(messageModel.getText());
            myViewHolder.messageTime.setText(UtilCal.formatDatePattern4(messageModel.getTime()));

            displayImage(myViewHolder, messageModel);

        }

    }

    private void whoTheFuckOperation(boolean isItMe, MyViewHolder myViewHolder, MessageModel messageModel) {
        if (messageModel != null) {

            if (isItMe == true) {

                myViewHolder.messageUser.setText(messageModel.getUserName());
                myViewHolder.messageContainer.setBackgroundColor(ContextCompat.getColor(mContext, R.color.blue_dark_grey));

            } else {
                myViewHolder.messageUser.setText(messageModel.getUserName());
                myViewHolder.messageContainer.setBackgroundColor(UtilColor.getRandomColor(mContext));

            }
        }
    }

    private void displayImage(MyViewHolder myViewHolder, MessageModel messageModel) {

        String imgName = messageModel.getUserName().substring(0, 1).toLowerCase().concat(".png");
        Bitmap bitmap = UtilImage.getBitmapFromAssets(mContext, imgName);
        if (bitmap != null) {
            myViewHolder.messageImv.setImageBitmap(bitmap);

        }
    }


    @Override
    public int getItemCount() {
        return (messageModelList != null ? messageModelList.size() : 0);
    }


}
