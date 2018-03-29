package redfox.chatroom.util.common_util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import redfox.chatroom.R;
import redfox.chatroom.ui.interfaces.NetDialogBtnCallbacks;

public class UtilNetDialog {

    private Context mContext;
    private AlertDialog exitDialog;
    private NetDialogBtnCallbacks mCallbacks;


    public UtilNetDialog(Context mContext, NetDialogBtnCallbacks mCallbacks) {
        this.mContext = mContext;
        this.mCallbacks = mCallbacks;
    }

    public void initExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogTheme);
        builder.setTitle(Html.fromHtml("<font color='#fa942f'>No Internet !! </font>"));
        builder.setMessage(Html.fromHtml("<font color='#43a96f'>UnInterrupted Connection required...</font>"));
        builder.setCancelable(false);

        builder.setPositiveButton("EXIT"
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int arg1) {
                        mCallbacks.onPositiveBtnTapped();
                    }
                });
        exitDialog = builder.create();
    }

    public void showExitDialog() {
        if (exitDialog != null) {
            exitDialog.show();
        }
    }


}
