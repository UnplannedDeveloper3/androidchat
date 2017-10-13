package atritechnocrat.com.locationchatapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 09-08-2017.
 */

public class AllStreams implements Parcelable {
    private List<Stream> streams;

    public AllStreams() {
        // HardCode Stream for Initial development purpose
        //streams = new ArrayList<>();
        //streams.add(new Stream("New york"));
        //streams.add(new Stream("Bayside"));
        //streams.add(new Stream("Queens"));
    }

    public AllStreams(List<Stream> streams) {
        this.streams = streams;
    }

    protected AllStreams(Parcel in) {
        streams = in.readArrayList(Stream.class.getClassLoader());
    }

    public static final Creator<AllStreams> CREATOR = new Creator<AllStreams>() {
        @Override
        public AllStreams createFromParcel(Parcel in) {
            return new AllStreams(in);
        }

        @Override
        public AllStreams[] newArray(int size) {
            return new AllStreams[size];
        }
    };

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.streams);

    }
}
