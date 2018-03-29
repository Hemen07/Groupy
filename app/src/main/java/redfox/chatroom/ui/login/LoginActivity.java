package redfox.chatroom.ui.login;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import redfox.chatroom.R;
import redfox.chatroom.data.pref.SignInPref;
import redfox.chatroom.ui.interfaces.FirebaseAuthCallbacks;
import redfox.chatroom.ui.interfaces.InputValidationCallbacks;
import redfox.chatroom.ui.interfaces.NetDialogBtnCallbacks;
import redfox.chatroom.ui.main_screen.MainActivity;
import redfox.chatroom.ui.signup.SignUpActivity;
import redfox.chatroom.util.common_util.UtilExtra;
import redfox.chatroom.util.common_util.UtilNetDialog;
import redfox.chatroom.util.common_util.UtilNetworkDetect;
import redfox.chatroom.util.common_util.UtilSnackBarAuth;
import redfox.chatroom.util.common_util.UtilSnackBarLogin;
import redfox.chatroom.util.common_util.UtilValidation;
import redfox.chatroom.util.firebase_util.UtilAuthentication;
import redfox.chatroom.util.firebase_util.UtilFBConstants;

public class LoginActivity extends AppCompatActivity implements FirebaseAuthCallbacks, InputValidationCallbacks, NetDialogBtnCallbacks {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;


    @BindView(R.id.SignIn_Imv)
    ImageView sImv;
    @BindView(R.id.SignIn_Etx_UserMail_id)
    TextInputEditText sEtxEmail;
    @BindView(R.id.SignIn_TIPL_email_id)
    TextInputLayout sTiplEmail;
    @BindView(R.id.signIn_Etx_Pwd_id)
    TextInputEditText sEtxPwd;
    @BindView(R.id.signIn_TIPL_password_id)
    TextInputLayout sTplsPwd;
    @BindView(R.id.SignIn_Btn_Login_id)
    Button sLogInBtn;
    @BindView(R.id.SignIn_Btn_Register_id)
    Button sRegisterBtn;
    @BindView(R.id.SignIn_progress_view_id)
    AnimatedCircleLoadingView progBar;
    @BindView(R.id.coordinatorId)
    CoordinatorLayout coordLayout;

    private FirebaseAuth mAuth;
    private SignInPref mPref;

    private UtilNetDialog utilNetDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setUpSharedPref();
        setUpProgress();
        setUpFireBase();
        setUpNetDialog();
    }

    private void setUpSharedPref() {
        if (LOG_DEBUG) Log.v(TAG, " setUpSharedPref()");
        mPref = SignInPref.getmInstance(this);
        checkSignInStatus();

    }

    private void checkSignInStatus() {
        if (LOG_DEBUG) Log.v(TAG, " checkSignInStatus()");

        if (mPref.getSignInStatus() == 1) {
            if (LOG_DEBUG) System.out.println(" status ================ 1");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            if (LOG_DEBUG) System.out.println(" status ================ 0 ");

        }
    }

    private void setUpProgress() {
        progBar.startIndeterminate();
    }

    private void setUpFireBase() {
        if (LOG_DEBUG) Log.v(TAG, " setUpFireBase()");
        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpNetDialog() {
        utilNetDialog = new UtilNetDialog(this, LoginActivity.this);
        utilNetDialog.initExitDialog();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick({R.id.SignIn_Btn_Login_id, R.id.SignIn_Btn_Register_id})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.SignIn_Btn_Login_id:
                if (LOG_DEBUG) Log.i(TAG, " Login btn Tapped..");

                if (UtilNetworkDetect.checkOnline(this))
                    setUpLogin();
                else {
                    showExitDialog();
                }
                break;
            case R.id.SignIn_Btn_Register_id:

                if (LOG_DEBUG) Log.i(TAG, " Register btn Tapped..");
                startActivity(new Intent(this, SignUpActivity.class)
                        , ActivityOptions.makeSceneTransitionAnimation(this).toBundle());


                sEtxEmail.setText("");
                sEtxPwd.setText("");
                break;
        }
    }

    private void setUpLogin() {
        String email = sEtxEmail.getText().toString().trim();
        String password = sEtxPwd.getText().toString().trim();

        if (UtilValidation.isValidEmail(this, email, this)) {

            if (UtilValidation.isValidPassword(this, password, this)) {

                progBar.setVisibility(View.VISIBLE);
                UtilAuthentication.signIN(LoginActivity.this, mAuth, email, password, this);
            }
        }

    }

    @Override
    public void inputErrorCallbacks(String message) {
        if (LOG_DEBUG) Log.i(TAG, " inputErrorCallbacks() : " + message);
        progBar.setVisibility(View.GONE);
        UtilSnackBarLogin.snackbarLogin(this, message);
        UtilExtra.showKeyboard(this);
    }

    @Override
    public void userDataCallback(FirebaseUser user, String message) {
        if (LOG_DEBUG) Log.i(TAG, "userDataCallback() ");

        progBar.setVisibility(View.GONE);
        UtilExtra.hideKeyboard(this);

        if (user != null) {
            if (LOG_DEBUG) {
                Log.e(TAG, " extractData () and  callback Message " + message);
                Log.i(TAG, "  Name :" + user.getDisplayName());
                Log.i(TAG, " Email :" + user.getEmail());
                Log.i(TAG, " UID :" + user.getUid());
            }

            saveToPref(user);
        } else {
            if (LOG_DEBUG) Log.e(TAG, " FireBase user = null ");
        }

        if (message != null) {
            messageBasedOperation(message);
        }
    }

    private void saveToPref(FirebaseUser firebaseUser) {
        if (LOG_DEBUG) Log.i(TAG, " saveToPref()");

        if (mPref != null) {
            mPref.setUserName(firebaseUser.getDisplayName());
            mPref.setUserEmail(firebaseUser.getEmail());
            mPref.setUserUID(firebaseUser.getUid());
            mPref.saveSignInStatus(1);
        } else {
            mPref = SignInPref.getmInstance(this);
            mPref.setUserName(firebaseUser.getDisplayName());
            mPref.setUserEmail(firebaseUser.getEmail());
            mPref.setUserUID(firebaseUser.getUid());
            mPref.saveSignInStatus(1);
        }
    }

    private void messageBasedOperation(String message) {
        switch (message) {

            case UtilFBConstants.LOGIN_EMAIL_NOT_REGISTERED:
                if (LOG_DEBUG) Log.i(TAG, "mail not registered");
                UtilSnackBarAuth.snackbarAuth(this, UtilFBConstants.LOGIN_EMAIL_NOT_REGISTERED_MSG);
                break;
            case UtilFBConstants.LOGIN_PASSWORD_WRONG:
                if (LOG_DEBUG) Log.i(TAG, "Oops, password does not match");
                UtilSnackBarAuth.snackbarAuth(this, UtilFBConstants.LOGIN_PASSWORD_WRONG_MSG);
                break;
            case UtilFBConstants.LOGIN_SUCCESS_MSG:
                UtilExtra.hideKeyboard(this);
                checkSignInStatus();
                break;
            default:
        }

    }


    private void showExitDialog() {
        utilNetDialog.showExitDialog();
    }

    @Override
    public void onPositiveBtnTapped() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.v(TAG, " onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.v(TAG, " onPause()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.v(TAG, " onStop()");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.v(TAG, " onDestroy()");
    }

}
