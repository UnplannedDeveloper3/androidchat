package atritechnocrat.com.locationchatapplication.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by admin on 14-08-2017.
 */

public class Region implements Parcelable {
    private String regionId ;


    @SerializedName("sublocality")
    @Expose
    private String sublocality;
    @SerializedName("locality")
    @Expose
    private String locality;

    private String latitude;

    private String longitude;

    // name property is created on server
    private String name;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

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

    private Map<String ,Boolean> streams;

    protected Region(Parcel in) {
        regionId = in.readString();
        sublocality = in.readString();
        locality = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        name = in.readString();
        streams = new HashMap<>();


        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            Boolean value = in.readByte() != 0;
            streams.put(key,value);
        }

    }

    public static final Creator<Region> CREATOR = new Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    public Map<String, Boolean> getStreams() {
        return streams;
    }

    public void setStreams(Map<String, Boolean> streams) {
        this.streams = streams;
    }

    public Region() {
    }

    public Region(String sublocality, String locality) {
        this.sublocality = sublocality;
        this.locality = locality;
    }

    public Region(String latitude, String longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    // Create name of region
    // If region object  is created on appside (Local machine)
    // getRegionName is helps to get region name (Locally)
    public String getRegionName(){
        return sublocality.trim()+", "+locality.trim();
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getLatitude() {
        return latitude.equals("")? ""+0.0 : latitude ;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude.equals("")? ""+0.0:longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSublocality() {
        return sublocality;
    }

    public void setSublocality(String sublocality) {
        this.sublocality = sublocality;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(regionId);
        dest.writeString(sublocality);
        dest.writeString(locality);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(name);
        dest.writeInt(streams.size());
        for(Map.Entry<String,Boolean> entry : streams.entrySet()){
            dest.writeString(entry.getKey());
            dest.writeByte((byte) (entry.getValue() ? 1 : 0));
        }
    }
}
