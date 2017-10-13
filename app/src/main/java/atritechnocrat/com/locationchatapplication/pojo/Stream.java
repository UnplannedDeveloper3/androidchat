package atritechnocrat.com.locationchatapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by admin on 04-08-2017.
 */

public class Stream implements Parcelable,Comparable<Stream> {
    private String streamId;


    @SerializedName("created_at")
    @Expose
    private String created_at;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("owner")
    @Expose
    private User owner;
    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    public Stream() {
    }



    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Stream(String name) {
        this.name = name;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    protected Stream(Parcel in) {
        streamId = in.readString();
        name = in.readString();
    }

    public static final Creator<Stream> CREATOR = new Creator<Stream>() {
        @Override
        public Stream createFromParcel(Parcel in) {
            return new Stream(in);
        }

        @Override
        public Stream[] newArray(int size) {
            return new Stream[size];
        }
    };

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
        dest.writeString(streamId);
        dest.writeString(name);
    }


    @Override
    public int compareTo(@NonNull Stream o) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        //if( streamId == o.streamId )return 0;else if(streamId )

        try {
            return format.parse(o.getCreated_at()).compareTo(format.parse(created_at));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
