package redfox.chatroom.util.common_util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by notTdar on 1/10/2018.
 */

public class UtilExtra {
    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void hideKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }

    public static void hideKeyboard2(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
    }

    public static void playSound(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), notification);
        r.play();

    }

    public static void doVibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));

            } else {
                vibrator.vibrate(500);
            }
        }
    }

    /*
     * DON'T READ , YOU STILL HAVE TIME | GO AWAY | GANDALF
     *
     *
     * TOO LATE-------------------------
     * Don't judge ... we all eventually go through ? ain't we
     */

    public static String profanityFilter(String userInput) {
        String[] curseArray = {"Fuck", "Suck", "Dick", "Lund", "Chutiya", "bhosdi", "fucker"};

        for (String aCurseArray : curseArray) {
            if (aCurseArray.equalsIgnoreCase(userInput)) {
                System.out.println(" You donkey : " + aCurseArray);
                return "#@?%&";
            }
        }
        return userInput;
    }


}
