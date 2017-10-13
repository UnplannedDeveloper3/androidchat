package atritechnocrat.com.locationchatapplication.dashboard;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import atritechnocrat.com.locationchatapplication.ActionBarProvider;
import atritechnocrat.com.locationchatapplication.MyApplication;
import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.dashboard.home.HomeController;
import atritechnocrat.com.locationchatapplication.dashboard.notification.NotificationController;
import atritechnocrat.com.locationchatapplication.pojo.AllStreams;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;

import static java.util.Locale.getDefault;

/*
* By default It shows following UI componenent
* 1. Bottom Navaigation bar
*
* Operations on the screen
* This screen by default shows Dashbord Controller
* Which has three tab
* By default Latest tab is open which has recycler view
* containing streams
*
*
 */

public class DashboardActivity extends AppCompatActivity implements ActionBarProvider, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "DashboardActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;
    private static final int LOCATION_SETTING_SCREEN = 120;
    public static final int REQUEST_CHECK_SETTINGS = 0;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.homeContent)
    FrameLayout homeContent;
    @BindView(R.id.dashboardContent)
    FrameLayout dashboardContent;
    @BindView(R.id.notificationContent)
    FrameLayout notificationContent;
    @BindView(R.id.container)
    RelativeLayout container;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String trimmedAddress;
    private AllStreams allAvailbleStream;
    private User currentLogginUser;
    private Stream currentSelectedStream;

    // Routers
    private Router homeRouter;
    private Router dashBoardRouter;
    private Router notificationRouter;

    // Controllers
    private HomeController homeController;
    private NotificationController notificationController;
    private String currentUserRegionTitle = "";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_chat);
        setSupportActionBar(toolbar);

        allAvailbleStream = getIntent().getParcelableExtra(Util.STREAM_DATA);
        currentLogginUser = getIntent().getParcelableExtra(Util.USER_DATA);
        // By default homeRouter is visible
        if (homeRouter == null) {
            homeRouter = Conductor.attachRouter(this, homeContent, savedInstanceState);
            if (!homeRouter.hasRootController()) {
                if (homeController == null) {
                    homeController = new HomeController(DashboardActivity.this);
                }
                homeRouter.setRoot(RouterTransaction.with(homeController));
            }
        }
        homeContent.setVisibility(View.VISIBLE);
        dashboardContent.setVisibility(View.GONE);
        notificationContent.setVisibility(View.GONE);
        getSupportActionBar().hide();


        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportActionBar().hide();
                        getSupportActionBar().setTitle(R.string.title_chat);
                        homeContent.setVisibility(View.VISIBLE);
                        dashboardContent.setVisibility(View.GONE);
                        notificationContent.setVisibility(View.GONE);
                        return true;
                    case R.id.navigation_dashboard:
                    case R.id.navigation_notifications:
                        getSupportActionBar().show();
                        getSupportActionBar().setTitle(R.string.title_notifications);
                        if (notificationRouter == null) {
                            notificationRouter = Conductor.attachRouter(DashboardActivity.this, notificationContent, savedInstanceState);
                            if (!notificationRouter.hasRootController()) {
                                if (notificationController == null) {
                                    notificationController = new NotificationController();
                                }
                                notificationRouter.setRoot(RouterTransaction.with(notificationController));
                            }
                        }

                        homeContent.setVisibility(View.GONE);
                        dashboardContent.setVisibility(View.GONE);
                        notificationContent.setVisibility(View.VISIBLE);
                     return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (homeContent.getVisibility() == View.VISIBLE) {
            if (!homeRouter.handleBack()) {
                super.onBackPressed();
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"Resume called");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
