package e.nottdar.androidchat.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import e.nottdar.androidchat.R;
import e.nottdar.androidchat.data.pref.SignInPref;
import e.nottdar.androidchat.ui.main_screen.MainActivity;
import e.nottdar.androidchat.ui.interfaces.FirebaseAuthCallbacks;
import e.nottdar.androidchat.ui.interfaces.InputValidation;
import e.nottdar.androidchat.ui.signup.SignUpActivity;
import e.nottdar.androidchat.util.common_util.UtilExtra;
import e.nottdar.androidchat.util.common_util.UtilSnackBarAuth;
import e.nottdar.androidchat.util.common_util.UtilSnackBarLogin;
import e.nottdar.androidchat.util.common_util.UtilValidation;
import e.nottdar.androidchat.util.firebase_util.UtilAuthentication;
import e.nottdar.androidchat.util.firebase_util.UtilConstants;

public class LoginActivity extends AppCompatActivity implements FirebaseAuthCallbacks, InputValidation {

    private final static String TAG = LoginActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.v(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setUpSharedPref();
        setUpProgress();
        setUpFireBase();

    }

    private void setUpSharedPref() {
        if (LOG_DEBUG) Log.v(TAG, " setUpSharedPref()");
        mPref = SignInPref.getmInstance(this);
        checkSignInStatus();

    }

    private void checkSignInStatus() {
        if (LOG_DEBUG) Log.v(TAG, " checkSignInStatus()");

        if (mPref.getSignInStatus() == 1) {
            System.out.println(" status ================ 1");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            System.out.println(" status ================ 0 ");

        }
    }

    private void setUpProgress() {
        progBar.startIndeterminate();
    }

    private void setUpFireBase() {
        if (LOG_DEBUG) Log.v(TAG, " setUpFireBase()");
        mAuth = FirebaseAuth.getInstance();
        // getFireBaseUserInfo(mAuth.getCurrentUser());
    }

    @OnClick({R.id.SignIn_Btn_Login_id, R.id.SignIn_Btn_Register_id})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.SignIn_Btn_Login_id:
                if (LOG_DEBUG) Log.i(TAG, " Login btn Tapped..");
                setUpLogin();
                break;
            case R.id.SignIn_Btn_Register_id:

                if (LOG_DEBUG) Log.i(TAG, " Register btn Tapped..");
                startActivity(new Intent(this, SignUpActivity.class));
                sEtxEmail.setText("");
                sEtxPwd.setText("");
                break;
        }
    }

    private void setUpLogin() {
        String email = sEtxEmail.getText().toString().trim();
        String password = sEtxPwd.getText().toString().trim();

        if (UtilValidation.isValidEmail(this, email, this) == true) {

            if (UtilValidation.isValidPassword(this, password, this) == true) {

                progBar.setVisibility(View.VISIBLE);
                UtilExtra.hideKeyboard(this);
                UtilAuthentication.signIN(LoginActivity.this, mAuth, email, password, this);
            }
        }

    }

    @Override
    public void inputErrorCallbacks(String message) {
        if (LOG_DEBUG) Log.i(TAG, " inputErrorCallbacks() : " + message);
        progBar.setVisibility(View.GONE);
        UtilSnackBarLogin.snackbarLogin(this, message);
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
                saveToPref(user);
            }
        } else if (LOG_DEBUG) Log.e(TAG, " FireBase user = null ");

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

            case UtilConstants.LOGIN_EMAIL_NOT_REGISTERED:
                if (LOG_DEBUG) Log.i(TAG, "mail not registered");
                UtilSnackBarAuth.snackbarAuth(this, UtilConstants.LOGIN_EMAIL_NOT_REGISTERED_MSG);
                break;
            case UtilConstants.LOGIN_PASSWORD_WRONG:
                if (LOG_DEBUG) Log.i(TAG, "Oops, password does not match");
                UtilSnackBarAuth.snackbarAuth(this, UtilConstants.LOGIN_PASSWORD_WRONG_MSG);
                break;
            case UtilConstants.LOGIN_SUCCESS_MSG:
                UtilExtra.hideKeyboard(this);
                checkSignInStatus();
                break;
            default:
        }

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
