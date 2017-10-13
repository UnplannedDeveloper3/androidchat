package atritechnocrat.com.locationchatapplication.webservice.requestPojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import atritechnocrat.com.locationchatapplication.pojo.Location;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;

/**
 * Created by admin on 14-08-2017.
 */

public class SubmitStreamRequest {
    @SerializedName("region")
    @Expose
    private Region region;
    @SerializedName("stream")
    @Expose
    private Stream stream;
    @SerializedName("location")
    @Expose
    private Location location;

    @SerializedName("user")
    @Expose
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SubmitStreamRequest(Region region, Stream stream, Location location) {
        this.region = region;
        this.stream = stream;
        this.location = location;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
