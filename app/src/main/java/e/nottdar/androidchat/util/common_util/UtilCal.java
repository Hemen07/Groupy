package e.nottdar.androidchat.util.common_util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilCal {

    private final Calendar mCalendar;

    public UtilCal() {
        mCalendar = Calendar.getInstance();
    }

    public int getDayOfMonth() {
        if (mCalendar != null) {
            return mCalendar.get(Calendar.DAY_OF_MONTH);
        }
        return 0;
    }

    public String getMonthName() {
        if (mCalendar != null) {
            return mCalendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        }
        return "";
    }

    public String getDayName() {
        if (mCalendar != null) {
            return mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        }
        return "";
    }

    //current time in dd MMM HH:MM:A  format------------for scheduledWhen
    public static String formatDatePattern4(long dateInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" dd-MMM hh:mm:a", Locale.getDefault());
        Date date = new Date(dateInMillis);
        return simpleDateFormat.format(date);
    }

    //Format : patterns
    public static String formatDate(long dateInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault());
        Date date = new Date(dateInMillis);
        return dateFormat.format(date);
    }

    //note Row : alarm time
    public static String formatDatePattern1(long dateInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = new Date(dateInMillis);
        return dateFormat.format(date);
    }

    //NU :
    public static String formatDatePattern2(long dateInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        Date date = new Date(dateInMillis);
        String dateFormatted = dateFormat.format(date);

        Calendar mChosenCal = Calendar.getInstance();
        mChosenCal.setTimeInMillis(dateInMillis);
        int chosenDay = mChosenCal.get(Calendar.DAY_OF_YEAR);

        Calendar mCalCurrent = Calendar.getInstance();
        mCalCurrent.setTimeInMillis(System.currentTimeMillis());
        int currentDay = mCalCurrent.get(Calendar.DAY_OF_YEAR);

        if (chosenDay == currentDay) {
            return "TODAY";
        } else if (chosenDay - currentDay == 1) {
            return "TOMORROW";
        }

        return dateFormatted;
    }

    //nr : date tv : creation :
    public static String formatDatePattern3(long dateInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM", Locale.getDefault());
        Date date = new Date(dateInMillis);
        String dateFormatted = dateFormat.format(date);
        return dateFormatted;
    }

    //return am or pm
    public static String getAMPM(int selectedHour) {
        String AM_PM;
        if (selectedHour < 12) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        return AM_PM;
    }

    public static String getDoubleDigits(int num) {
        return num > 9 ? "" + num : "0" + num;
    }

    public static int format12Hour(int selectedHour) {
        if (selectedHour > 12) {
            selectedHour = selectedHour - 12;
        } else {
            //intentional
            selectedHour = selectedHour;
        }
        return selectedHour;
    }

}
