package atritechnocrat.com.locationchatapplication.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import atritechnocrat.com.locationchatapplication.BuildConfig;
import atritechnocrat.com.locationchatapplication.pojo.Location;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;


/**
 * Created by admin on 09-08-2017.
 */

public class Util {

    public static final String DATA = "data";
    public static final String USER_DATA = "user";
    public static final String  STREAM_DATA= "stream";
    public static final String  REGION_DATA= "region";
    public static final String PATH = "path";
    public static final String SERVICES = "services";
    public static final String NOTIFICATION_FCM_TOKEN = "notification_token";
    public static final String DEFAULT_NOTIFICATION_TOPIC = "news";
    public static final String UNIQUE_DEVICE_ID = "uniqueID";
    private static final String TAG = "Util";

    public final class Constants {
        public static final int SUCCESS_RESULT = 0;
        public static final int FAILURE_RESULT = 1;
        public static final String PACKAGE_NAME =
                BuildConfig.APPLICATION_ID;
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
    }


    static public class FIREBASE{
        public static final String TOKEN = "iKIx6V1VOXvQqRo9bpsS7R6sMNIR1HU5P7gZMK1A";
        public static final String FIELD_STREAMS = "streams";
        public static final String FIELD_USERS = "users";
        public static final String FIELD_STREAM_MEMBER = "stream_members";
        public static final String MESSAGES_CHILD = "messages";
        public static final String FIELD_REGIONS = "regions";

    }

    static public class PREFERENCES {

        public static final String MyPREFERENCES = "app";
        public static final String TOKEN = "token";
        public static final String LOGIN = "login";
        public static final String USERNAME = "username";
        public static final String USER_LATITUDE = "user_latitude";
        public static final String USER_LONGITUDE = "user_longitude";
        public static final String USER_REGION_LOCALITY = "locality";
        public static final String USER_REGION_SUB_LOCALITY = "sublocality";
        public static final double REGION_RADIUS = 100;

        public static void setLogin(Context context, User user) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(LOGIN, user.getUserId());
            editor.putString(USERNAME, user.getName());
            editor.commit();
        }


        public static String getUserId(Context context) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(LOGIN, "");
        }
        public static User getUserInfo(Context context) {
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return new User(sharedpreferences.getString(LOGIN, ""),sharedpreferences.getString(USERNAME, ""));
        }

        public static void setNotificationRegId(Context context ,String token) {
            Log.d(TAG,"FCM token > "+token);
            SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(NOTIFICATION_FCM_TOKEN, token);
            editor.commit();
        }
        public static String getNotificationRegId(Context context){
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return sharedpreferences.getString(NOTIFICATION_FCM_TOKEN, "");
        }

        public static void setUserCurrentLocation(Context context , Location location) {
            SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USER_LATITUDE, location.getLatitude());
            editor.putString(USER_LONGITUDE, location.getLongitude());
            editor.commit();
        }
        public static Location getUserCurrentLocation(Context context){
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return new Location(sharedpreferences.getString(USER_LATITUDE, ""),sharedpreferences.getString(USER_LONGITUDE, ""));
        }

        public static void setUserRegion(Context context , Region region) {
            SharedPreferences pref = context.getSharedPreferences(MyPREFERENCES, 0);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(USER_REGION_LOCALITY, region.getLocality());
            editor.putString(USER_REGION_SUB_LOCALITY, region.getSublocality());
            editor.commit();
        }
        public static Region getUserRegion(Context context){
            SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            return new Region(sharedpreferences.getString(USER_REGION_SUB_LOCALITY, "-"),sharedpreferences.getString(USER_REGION_LOCALITY, "-"));
        }

        public static boolean isUserValidForStream(Context context, String regionTitle){
            /*android.location.Location regionLocation = new android.location.Location("point A");
            regionLocation.setLatitude(Double.parseDouble(regionLatitude));
            regionLocation.setLongitude(Double.parseDouble(regionLongitude));

            android.location.Location userLocation = new android.location.Location("point B");

            Location userLatLong = getUserCurrentLocation(context);
            userLocation.setLatitude(Double.parseDouble(userLatLong.getLatitude()));
            userLocation.setLongitude(Double.parseDouble(userLatLong.getLongitude()));

            float distance = userLocation.distanceTo(regionLocation);
            Log.d(TAG,"distancs"+distance);
            if( REGION_RADIUS < distance ){
                return false;
            }
            return true;*/
            if(regionTitle.equals(getUserRegion(context).getRegionName())){
                return true;
            }else {
                return false;
            }

        }




    }


}
