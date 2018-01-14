package e.nottdar.androidchat.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import e.nottdar.androidchat.R;
import e.nottdar.androidchat.data.models.SignUpModel;
import e.nottdar.androidchat.data.pref.SignInPref;
import e.nottdar.androidchat.ui.main_screen.MainActivity;
import e.nottdar.androidchat.ui.interfaces.FirebaseAuthCallbacks;
import e.nottdar.androidchat.ui.interfaces.InputValidation;
import e.nottdar.androidchat.util.common_util.UtilExtra;
import e.nottdar.androidchat.util.common_util.UtilSnackBarProfile;
import e.nottdar.androidchat.util.common_util.UtilValidation;
import e.nottdar.androidchat.util.firebase_util.UtilAuthentication;
import e.nottdar.androidchat.util.firebase_util.UtilConstants;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class Profile extends AppCompatActivity implements FirebaseAuthCallbacks, InputValidation {
    private final static String TAG = Profile.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;

    @BindView(R.id.Profile_TIPL_Name_id)
    TextInputLayout pTplsName;
    @BindView(R.id.Profile_Etx_Name_id)
    TextInputEditText pEtxName;
    @BindView(R.id.Profile_Btn_Set_id)
    Button btnSet;
    @BindView(R.id.Profile_progress_view_id)
    AnimatedCircleLoadingView progBar;
    @BindView(R.id.Profile_coordinator)
    CoordinatorLayout coordinator;

    private SignInPref mPref;
    private FirebaseAuth mAuth;
    private SignUpModel signUpModel;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (LOG_DEBUG) Log.e(TAG, "onCreate()");
        ButterKnife.bind(this);

        intentOperation(getIntent());

        setUpPref();
        setUpProgressBar();
        setUpFireBase();
    }

    private void intentOperation(Intent intent) {
        if (intent != null) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                signUpModel = bundle.getParcelable(UtilConstants.PARCEL_KEY);
                if (LOG_DEBUG) Log.i(TAG, " rcv signUpModel has " + signUpModel);
            }
        }
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


    private void setUpName() {
        String name = pEtxName.getText().toString().trim();

        if (UtilValidation.isValidUsername(this, name, this) == true) {
            progBar.setVisibility(View.VISIBLE);
            UtilExtra.hideKeyboard(this);
            FirebaseUser user = mAuth.getCurrentUser();
            UtilAuthentication.addUserName(user, name, this);
        }

    }

    @OnClick(R.id.Profile_Btn_Set_id)
    public void onViewClicked() {
        if (LOG_DEBUG) Log.i(TAG, "btn Tapped");
        setUpName();
    }


    @Override
    public void inputErrorCallbacks(String message) {
        if (LOG_DEBUG) Log.i(TAG, " inputErrorCallbacks() : " + message);
        progBar.setVisibility(View.GONE);
        UtilSnackBarProfile.snackBarProfile(this, message);
    }

    @Override
    public void userDataCallback(FirebaseUser user, String message) {
        if (LOG_DEBUG) Log.i(TAG, "userDataCallback()");
        progBar.setVisibility(View.GONE);
        UtilExtra.hideKeyboard(this);

        if (user != null) {
            Log.e(TAG, " extractData () and  callback Message " + message);
            if (LOG_DEBUG) {
                Log.i(TAG, " Name :" + user.getDisplayName());
                Log.i(TAG, " Email :" + user.getEmail());
                saveToPref(user);
            }
        } else if (LOG_DEBUG) Log.e(TAG, " FireBase user = null ");

        if (message != null && user != null) {
            messageBasedOperation(user, message);
        }
    }

    private void saveToPref(FirebaseUser firebaseUser) {
        if (LOG_DEBUG) Log.i(TAG, " saveToPref()");

        if (signUpModel != null) {

            if (firebaseUser != null) {
                if (mPref != null) {
                    mPref.setUserName(firebaseUser.getDisplayName());
                    mPref.setUserEmail(signUpModel.getEmail());
                    mPref.setUserUID(signUpModel.getUID());
                    mPref.saveSignInStatus(1);
                } else {
                    mPref = SignInPref.getmInstance(this);
                    mPref.setUserName(firebaseUser.getDisplayName());
                    mPref.setUserEmail(signUpModel.getEmail());
                    mPref.setUserUID(signUpModel.getUID());
                    mPref.saveSignInStatus(1);
                }
            }
        }
    }

    private void messageBasedOperation(FirebaseUser user, String message) {
        switch (message) {
            case UtilConstants.PROFILE_SET_SUCCESS:
                UtilExtra.hideKeyboard(this);
                moveToActivity();
                break;
            default:
        }
    }

    private void moveToActivity() {
        if (LOG_DEBUG) Log.i(TAG, " moveToActivity ");

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null && signUpModel.getEmail() != null) {
            if (user.getEmail().contains(signUpModel.getEmail())) {
                UtilAuthentication.deleteUser(user);
            } else {
//intentional
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}

