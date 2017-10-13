package atritechnocrat.com.locationchatapplication.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 14-08-2017.
 */

public class Location {
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;

    public Location(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude.equals("") ? "" + 0.0 : latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude.equals("") ? "" + 0.0 : longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
