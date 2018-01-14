package e.nottdar.androidchat.ui.signup;

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
import e.nottdar.androidchat.data.models.SignUpModel;
import e.nottdar.androidchat.data.pref.SignInPref;
import e.nottdar.androidchat.ui.interfaces.FirebaseAuthCallbacks;
import e.nottdar.androidchat.ui.interfaces.InputValidation;
import e.nottdar.androidchat.ui.profile.Profile;
import e.nottdar.androidchat.util.common_util.UtilExtra;
import e.nottdar.androidchat.util.common_util.UtilSnackBarAuth;
import e.nottdar.androidchat.util.common_util.UtilSnackBarRegister;
import e.nottdar.androidchat.util.common_util.UtilValidation;
import e.nottdar.androidchat.util.firebase_util.UtilAuthentication;
import e.nottdar.androidchat.util.firebase_util.UtilConstants;


public class SignUpActivity extends AppCompatActivity implements FirebaseAuthCallbacks, InputValidation {

    private final static String TAG = SignUpActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;


    @BindView(R.id.SignUp_Imv)
    ImageView sImv;
    @BindView(R.id.SignUp_TIPL_password_id)
    TextInputLayout sTplsPwd;
    @BindView(R.id.SignUp_Etx_UserMail_id)
    TextInputEditText sEtxEmail;
    @BindView(R.id.SignUp_TIPL_email_id)
    TextInputLayout sTiplEmail;
    @BindView(R.id.SignUp_Etx_Pwd_id)
    TextInputEditText sEtxPwd;
    @BindView(R.id.SignUp_Btn_Register_id)
    Button sRegisterBtn;
    @BindView(R.id.SignUp_progress_view_id)
    AnimatedCircleLoadingView progBar;
    @BindView(R.id.SignUp_coodrinator)
    CoordinatorLayout coordinatorLayout;

    private SignInPref mPref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.e(TAG, " onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        setUpPref();
        setUpProgressBar();
        setUpFireBase();
    }


    private void setUpPref() {
        mPref = SignInPref.getmInstance(this);
    }

    private void setUpProgressBar() {
        progBar.startIndeterminate();
    }

    private void setUpFireBase() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setUpSignUp() {
        String email = sEtxEmail.getText().toString().trim();
        String password = sEtxPwd.getText().toString().trim();

        if (UtilValidation.isValidEmail(this, email, this) == true) {

            if (UtilValidation.isValidPassword(this, password, this) == true) {

                progBar.setVisibility(View.VISIBLE);
                UtilExtra.hideKeyboard(this);
                UtilAuthentication.signUp(SignUpActivity.this, mAuth, email, password,
                        this);
            }
        }

    }

    @OnClick(R.id.SignUp_Btn_Register_id)
    public void onViewClicked() {
        if (LOG_DEBUG) Log.i(TAG, "btn Tapped");
        setUpSignUp();
    }

    @Override
    public void inputErrorCallbacks(String message) {
        if (LOG_DEBUG) Log.i(TAG, " inputErrorCallbacks() : " + message);
        progBar.setVisibility(View.GONE);
        UtilSnackBarRegister.snackBarRegister(this, message);
    }

    @Override
    public void userDataCallback(FirebaseUser user, String message) {
        if (LOG_DEBUG) Log.i(TAG, "userDataCallback()");
        progBar.setVisibility(View.GONE);
        UtilExtra.hideKeyboard(this);

        //extra data :
        if (user != null) {
            Log.e(TAG, " extractData () and  callback Message " + message);
            if (LOG_DEBUG) {
                Log.i(TAG, " Email :" + user.getEmail());
                Log.i(TAG, " UID :" + user.getUid());
            }
        } else if (LOG_DEBUG) Log.e(TAG, " FireBase user = null ");

        if (message != null && user != null) {
            messageBasedOperation(message, user);
        }

    }

    private void messageBasedOperation(String message, FirebaseUser user) {
        switch (message) {

            case UtilConstants.SIGN_UP_EMAIL_ALREADY_REGISTERED:
                if (LOG_DEBUG) Log.i(TAG, " You are already Registered ..");
                UtilSnackBarAuth.snackbarAuth(this, UtilConstants.SIGN_UP_EMAIL_ALREADY_REGISTERED_MSG);
                uiOperation(0, user);
                break;
            case UtilConstants.SIGN_UP_SUCCESS_MSG:
                UtilExtra.hideKeyboard(this);
                uiOperation(1, user);
                break;
            default:
        }


    }

    private void uiOperation(int status, FirebaseUser firebaseUser) {
        if (LOG_DEBUG) Log.i(TAG, " uiOperation : status " + status);

        if (status == 1) {
            if (LOG_DEBUG) Log.i(TAG, " result = 1 : start Profile Activity");

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            SignUpModel signUpModel = new SignUpModel();

            signUpModel.setName(firebaseUser.getDisplayName()); // nada here
            signUpModel.setEmail(firebaseUser.getEmail());
            signUpModel.setUID(firebaseUser.getUid());

            bundle.putParcelable(UtilConstants.PARCEL_KEY, signUpModel);
            intent.putExtras(bundle);

            startActivity(new Intent(this, Profile.class).putExtras(bundle));
            sEtxEmail.setText("");
            sEtxPwd.setText("");

        } else if (status == 0) {
            if (LOG_DEBUG)
                Log.i(TAG, "result 0 : Try Again Scenario ");//--------------DO SOMETHING ...
            //you are registered, so go back to login or request password if you forgot
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.e(TAG, " onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.e(TAG, " onPause()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.e(TAG, " onStop()");

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.e(TAG, " onDestroy()");

    }


}
