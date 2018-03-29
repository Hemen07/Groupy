package redfox.chatroom.data.models;

import redfox.chatroom.util.common_util.UtilCal;

/**
 * Created by notTdar on 1/12/2018.
 */

public class MessageModel {

    private String userName;
    private String text;
    private long time;
    private String uID;
    private String imageName;

    public MessageModel() {

    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return " MessageModel Has -- " + "\n" +
                " Name : " + userName + "\n" +
                " Text : " + text + "\n" +
                " Time : " + UtilCal.formatDatePattern4(time) + "\n " +
                " UID  : " + uID + "\n" +
                " imgname : " + imageName;
    }


}
