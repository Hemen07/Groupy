package redfox.chatroom.ui.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import redfox.chatroom.R;
import redfox.chatroom.data.models.MessageModel;
import redfox.chatroom.util.common_util.UtilCal;

public class SentMessageHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.ims_text_message_body_tv)
    TextView msgText;
    @BindView(R.id.ims_text_message_time_tv)
    TextView msgTime;


    public SentMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind(MessageModel messageModel) {
        msgText.setText(messageModel.getText());
        msgTime.setText(UtilCal.formatDatePattern4(messageModel.getTime()));

    }
}
