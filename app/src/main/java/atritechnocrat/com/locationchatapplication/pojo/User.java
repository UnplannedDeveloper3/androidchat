package atritechnocrat.com.locationchatapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by admin on 04-08-2017.
 */

public class User implements Parcelable {

    private String userId;

    private String name ;

    private String deviceToken;

    public User() {
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public User(String name) {
        this.name = name;
    }

    protected User(Parcel in) {
        userId = in.readString();
        name = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(name);
    }
}
