package redfox.chatroom.ui.holders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import redfox.chatroom.R;
import redfox.chatroom.data.models.MessageModel;
import redfox.chatroom.util.common_util.UtilCal;
import redfox.chatroom.util.common_util.UtilImage;

public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.imr_profile_imv)
    ImageView msgUserImv;
    @BindView(R.id.imr_text_message_name_tv)
    TextView msgUserName;
    @BindView(R.id.imr_text_message_body_tv)
    TextView msgText;
    @BindView(R.id.imr_text_message_time_tv)
    TextView msgTime;

    public ReceivedMessageHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

    }

    public void bind(Context context, MessageModel messageModel) {
        displayImage(context, messageModel);
        msgUserName.setText(messageModel.getUserName());
        msgText.setText(messageModel.getText());
        msgTime.setText(UtilCal.formatDatePattern4(messageModel.getTime()));

    }

    private void displayImage(Context context, MessageModel messageModel) {
        String imgName = messageModel.getUserName().substring(0, 1).toLowerCase().concat(".png");
        Bitmap bitmap = UtilImage.getBitmapFromAssets(context, imgName);
        if (bitmap != null) {
            msgUserImv.setImageBitmap(bitmap);
        }
    }
}
