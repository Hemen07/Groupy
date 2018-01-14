package e.nottdar.androidchat.ui.interfaces;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by notTdar on 1/10/2018.
 */

public interface FirebaseAuthCallbacks {
    public void userDataCallback(FirebaseUser user, String message);


}
