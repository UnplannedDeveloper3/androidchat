package atritechnocrat.com.locationchatapplication;

import android.app.Application;
import android.location.Location;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by admin on 09-08-2017.
 */

public class MyApplication extends Application {

    public static RefWatcher refWatcher;
    public static Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

}