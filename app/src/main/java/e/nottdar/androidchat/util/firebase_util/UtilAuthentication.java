package e.nottdar.androidchat.util.firebase_util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import e.nottdar.androidchat.ui.interfaces.FirebaseAuthCallbacks;

public class UtilAuthentication {

    private static final String TAG = UtilAuthentication.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;

    public static void signIN(final Context context, final FirebaseAuth mAuth, String email, String password, final FirebaseAuthCallbacks mCallbacks) {
        if (context != null) {

            if (LOG_DEBUG) Log.w(TAG, "signIN() :  passed : " + email + " : " + password);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (LOG_DEBUG) Log.w(TAG, "LOGIN : Success");

                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    mCallbacks.userDataCallback(user, UtilConstants.LOGIN_SUCCESS_MSG);
                                }

                            } else {
                                if (LOG_DEBUG) Log.w(TAG, "signInWithEmail : Failure", task.getException());
                                if (task.getException() != null) {
                                    mCallbacks.userDataCallback(null, task.getException().getMessage());
                                }
                            }
                        }
                    });

        }
    }


    public static void signUp(final Context context, final FirebaseAuth mAuth, String email, String password, final FirebaseAuthCallbacks mCallbacks) {
        if (context != null) {

            if (LOG_DEBUG) Log.w(TAG, "signUp() : " + email + " " + password);

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (LOG_DEBUG) Log.w(TAG, "SignUP  : Success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            mCallbacks.userDataCallback(user, UtilConstants.SIGN_UP_SUCCESS_MSG);
                        }
                    } else {
                        if (LOG_DEBUG) Log.w(TAG, "SignUP : Failure", task.getException());
                        if (task.getException() != null) {
                            mCallbacks.userDataCallback(null, task.getException().getMessage());
                        }
                    }

                }
            });

        }
    }

    //add userName to email&Pass MODE : Asynchronous call
    public static void addUserName(final FirebaseUser user, String userName, final FirebaseAuthCallbacks mCallbacks) {
        if (LOG_DEBUG) Log.d(TAG, " addUserName() : " + userName);
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if (LOG_DEBUG) Log.w(TAG, "Name Added ");
                            mCallbacks.userDataCallback(user, UtilConstants.PROFILE_SET_SUCCESS);
                        } else {
                            if (LOG_DEBUG) Log.w(TAG, "Name Added FAILED ");
                            mCallbacks.userDataCallback(null, UtilConstants.PROFILE_SET_FAILED);
                        }
                    }
                });
    }


    public static void deleteUser(FirebaseUser user) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete().
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "User account deleted.");

                            } else {
                                Log.d(TAG, "User account deletion Failed.");

                            }
                        }
                    });
        }
    }


}
