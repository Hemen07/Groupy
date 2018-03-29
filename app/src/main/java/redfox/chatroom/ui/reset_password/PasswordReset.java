package redfox.chatroom.ui.reset_password;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

import redfox.chatroom.R;
import redfox.chatroom.ui.interfaces.PwdResetCallbacks;
import redfox.chatroom.util.common_util.UtilConstant;
import redfox.chatroom.util.firebase_util.UtilAuthentication;


public class PasswordReset implements PwdResetCallbacks, View.OnClickListener {

    private final static String TAG = PasswordReset.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;
    private AnimatedCircleLoadingView progBar;
    private AlertDialog alertDialog;
    private Context context;
    private Button btnSubmit;
    private Button btnCancel;
    private EditText etx;

    public PasswordReset(Context context) {
        this.context = context;
    }

    public void resetPassDialog() {
        if (context != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View customView = layoutInflater.inflate(R.layout.pwd_reset, null);
            builder.setView(customView);

            etx = (EditText) customView.findViewById(R.id.pwd_reset_etx);
            btnCancel = (Button) customView.findViewById(R.id.pwd_reset_cancel_btn);
            btnSubmit = (Button) customView.findViewById(R.id.pwd_reset_submit_btn);
            progBar = (AnimatedCircleLoadingView) customView.findViewById(R.id.pwd_reset_progBar);
            builder.setCancelable(false);

            alertDialog = builder.create();
            btnSubmit.setOnClickListener(this);
            btnCancel.setOnClickListener(this);

            alertDialog.show();
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pwd_reset_submit_btn:

                setUpProgress();
                progBar.setVisibility(View.VISIBLE);


                if (LOG_DEBUG) Log.w(TAG, " PWD RESET SUBMIT TAPPED ");

                String emailInput = etx.getText().toString().trim();
                if (emailInput.length() < 1 && TextUtils.isEmpty(emailInput)) {
                    etx.setHint("empty !! type properly ");
                    etx.setHintTextColor(context.getResources().getColor(R.color.snackbar_email_check));

                } else {
                    disableButtons();
                    UtilAuthentication.resetPassword(context, emailInput, PasswordReset.this);
                }
                break;
            case R.id.pwd_reset_cancel_btn:
                if (LOG_DEBUG) Log.w(TAG, " PWD RESET CANCEL TAPPED ");

                dismissDialog();
                break;
            default:
        }

    }

    @Override
    public void pwdResetStatus(String message) {

        if (context != null) {
            if (progBar != null) progBar.setVisibility(View.GONE);

            switch (message) {
                case UtilConstant.PWD_RESET_SUCCESS:
                    if (LOG_DEBUG) Log.w(TAG, " PWD reset Successfully ");
                    enableButtons();
                    dismissDialog();

                    //Can't show snackBar her as no activity or parent ..
                    //and I am not able to find a way to go LoginActivity where it will work

                    //   UtilSnackBarCommon.snackBarCommon(context, UtilConstant.PWD_RESET_SUCCESS_MSG);
                    Toast.makeText(context, UtilConstant.PWD_RESET_SUCCESS_MSG, Toast.LENGTH_LONG).show();
                    break;
                case UtilConstant.PWD_RESET_FAIL:
                    if (LOG_DEBUG) Log.w(TAG, " PWD reset Failed ");
                    enableButtons();
                    etx.setError(UtilConstant.PWD_RESET_FAILED_MSG);
                    break;
                default:
            }
        }

    }

    private void setUpProgress() {
        progBar.startIndeterminate();
    }


    public void dismissDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private void disableButtons() {
        btnSubmit.setEnabled(false);
        btnCancel.setEnabled(false);
    }


    private void enableButtons() {
        btnSubmit.setEnabled(true);
        btnCancel.setEnabled(true);
    }
}
