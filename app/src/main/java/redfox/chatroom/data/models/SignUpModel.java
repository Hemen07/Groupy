package redfox.chatroom.data.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by notTdar on 1/9/2018.
 */

public class SignUpModel implements Parcelable {
    private String name;
    private String email;
    private String UID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }


    @Override
    public String toString() {

        return " SignUpModel Has -- " + "\n" +
                "Name  : " + name + "\n" +
                "Email : " + email + "\n " +
                " UID  : " + UID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.UID);
    }

    public SignUpModel() {
    }

    public SignUpModel(Parcel in) {
        this.name = in.readString();
        this.email = in.readString();
        this.UID = in.readString();
    }

    public static final Creator<SignUpModel> CREATOR = new Creator<SignUpModel>() {
        @Override
        public SignUpModel createFromParcel(Parcel source) {
            return new SignUpModel(source);
        }

        @Override
        public SignUpModel[] newArray(int size) {
            return new SignUpModel[size];
        }
    };
}
