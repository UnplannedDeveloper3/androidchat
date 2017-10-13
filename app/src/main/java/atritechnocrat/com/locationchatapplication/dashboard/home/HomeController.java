package atritechnocrat.com.locationchatapplication.dashboard.home;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.support.RouterPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import atritechnocrat.com.locationchatapplication.FetchAddressIntentService;
import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.conductor.base.BaseController;
import atritechnocrat.com.locationchatapplication.dashboard.DashboardActivity;
import atritechnocrat.com.locationchatapplication.dashboard.home.stream.StreamController;
import atritechnocrat.com.locationchatapplication.pojo.Location;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Locale.getDefault;


/**
 * Created by admin on 09-08-2017.
 */

public class HomeController extends BaseController implements HomeControllerContract.View {
    private static final String TAG = "HomeController";
    private static final String STREAM_KEY = "streams";
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int LOCATION_SETTING_SCREEN = 105;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 110;

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    //private StreamAdapter streamAdapter;
    HashMap<String, List<Stream>> stringListHashMap = new HashMap<>();
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private int currentSelectedStreamIndex = 0;
    private int currentSelectedRegionIndex = 0;
    private String currentUserRegionTitle = "";
    private AlertDialog alertPermissionDialog;

    protected android.location.Location mLastLocation;


    String[] tabs = {"Latest", "Popular", "Mychat"};
    //List<Stream> streamList = new ArrayList<>();

    List<Region> regionList = new ArrayList<>();

    @BindView(R.id.autocomplete_region_list)
    Spinner autocompleteRegionList;

    HomeControllerContract.UserAction userAction;

    private RouterPagerAdapter pagerAdapter;
    private Dialog builder;
    private RegionAdapter regionAdapter;
    private Context context;

    //Location
    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    boolean mRequestingLocationUpdates = true;
    private LocationCallback mLocationCallback;

    private AddressResultReceiver mResultReceiver;


    public HomeController(Context context) {
        Log.d(TAG, " HomeController ");
        intializeStreamListHashMap();
        userAction = new HomeContractPresenter(context, this, regionList, tabs, stringListHashMap);
        this.context = context;

    }

    public HomeController(Bundle args) {

        super(args);
        Log.d(TAG, " HomeController >Bundle");
    }


    @Override
    protected void onAttach(@NonNull View view) {
        Log.d(TAG, "onAttach");
        super.onAttach(view);


        if (checkLocationPermission()) {
            if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        if (!isGPSEnabled()) {
            getLocationSettingsResult();
        }

    }

    @Override
    protected void onDetach(@NonNull View view) {
        Log.d(TAG, "onDetach");
        super.onDetach(view);
        stopLocationUpdates();
        if (alertPermissionDialog != null) {
            alertPermissionDialog.dismiss();
            alertPermissionDialog = null;
        }
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        Log.d(TAG, " inflateView ");
        return inflater.inflate(R.layout.layout_home_controller, container, false);
    }


    @Override
    protected void onViewBound(@NonNull View view) {
        Log.d(TAG, " onViewBound ");
        super.onViewBound(view);
        fab.setOnClickListener(new AddStreamListener());
        regionAdapter = new RegionAdapter(getActivity(), R.layout.textview_stream_spinner, regionList);
        autocompleteRegionList.setAdapter(regionAdapter);
        autocompleteRegionList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //ecurrentSelectedRegionIndex = position;
                Log.d(TAG, " autocompleteRegionList " + currentSelectedRegionIndex + " : " + position);
                if (currentSelectedRegionIndex != position) {
                    currentSelectedRegionIndex = position;
                    progressBar.setVisibility(View.VISIBLE);
                    userAction.loadStream(regionList.get(currentSelectedRegionIndex), 0);
                    viewPager.setAdapter(null);
                    tabLayout.setupWithViewPager(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Location -----

        //checkLocationPermission();
        Log.d(TAG, "checkLocationPermission");
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();

                if (mLastLocation != null) {

                    double lat = mLastLocation.getLatitude();
                    double lon = mLastLocation.getLongitude();
                    Util.PREFERENCES.setUserCurrentLocation(context, new atritechnocrat.com.locationchatapplication.pojo.Location("" + lat, "" + lon));

                    startIntentService();
                }
            }
        };

        createLocationRequest();

        userAction.loadRegions(regionList);

    }

    protected void startIntentService() {
        mResultReceiver = new AddressResultReceiver(new android.os.Handler());
        Intent intent = new Intent(getActivity(), FetchAddressIntentService.class);
        intent.putExtra(Util.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Util.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    protected void onDestroyView(@NonNull View view) {
        Log.d(TAG, " onDestroyView ");
        if (!getActivity().isChangingConfigurations()) {
            viewPager.setAdapter(null);
        }
        tabLayout.setupWithViewPager(null);
        super.onDestroyView(view);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        Log.d(TAG, " onCreateView ");
        return super.onCreateView(inflater, container);
    }

    @Override
    public void addStreamSuccess() {
        Log.d(TAG, " addStreamSuccess ");
        Toast.makeText(getActivity(), R.string.stream_success, Toast.LENGTH_LONG).show();

        builder.dismiss();
        userAction.loadRegions(regionList);
        //userAction.loadStream(regionList.get(currentSelectedRegionIndex), 0);
    }

    @Override
    public void addStreamFail() {
        Log.d(TAG, " addStreamFail ");
        //Toast.makeText(getActivity(),R.string.stream_success,Toast.LENGTH_LONG).show();

        Toast.makeText(getActivity(), R.string.stream_fail, Toast.LENGTH_LONG).show();
        builder.dismiss();
        //userAction.loadStream(regionList.get(currentSelectedRegionIndex), 0);
    }

    @Override
    public void loadRegionSuccess() {
        Log.d(TAG, " loadRegionSuccess ");
        // If it loads for first time

        sortRegionList();

        //pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadRegionFail() {
        Log.d(TAG, " loadRegionFail ");
        regionAdapter.notifyDataSetChanged();
        //pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadStreamSuccess() {
        Log.d(TAG, " loadStreamSuccess ");
        // For first time it will load first region


        pagerAdapter = new RouterPagerAdapter(this) {
            @Override
            public void configureRouter(@NonNull Router router, int position) {
                if (!router.hasRootController()) {
                    Log.d(TAG, "configureRouter");
                    Controller page = new StreamController(tabs[position], stringListHashMap.get(tabs[position]), regionList.get(currentSelectedRegionIndex));
                    router.setRoot(RouterTransaction.with(page));
                }
            }

            @Override
            public int getCount() {
                return tabs.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabs[position];
            }
        };
        if (viewPager != null) {
            viewPager.setAdapter(pagerAdapter);
            tabLayout.setupWithViewPager(viewPager);

            progressBar.setVisibility(View.GONE);
            userAction.listenRegionValueChange(regionList.get(currentSelectedRegionIndex));
        }
    }

    @Override
    public void loadStreamFail() {
        Log.d(TAG, " loadStreamFail ");
    }

    @Override
    public void onRegionChildChange() {

    }

    public class AddStreamListener implements View.OnClickListener {
        @BindView(R.id.streamTitle)
        EditText streamTitle;
        @BindView(R.id.submitStream)
        Button submitStream;

        @Override
        public void onClick(View v) {
            if (isGPSEnabled()) {

                builder = new Dialog(getActivity());
                builder.setContentView(R.layout.layout_add_stream);
                ButterKnife.bind(this, builder);
                builder.setCancelable(true);
                Window window = builder.getWindow();
                window.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                submitStream.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, " AddStreamListener ");
                        if (!streamTitle.getText().toString().equals("")) {
                            builder.setCancelable(false);
                            streamTitle.setEnabled(false);
                            submitStream.setEnabled(false);
                            submitStream.setText(R.string.submitting);
                            userAction.addStream(streamTitle.getText().toString());
                            //streamTitle.setText("");
                        }
                    }
                });
                builder.show();
            } else {
                getLocationSettingsResult();
            }
        }
    }

    public void intializeStreamListHashMap() {
        for (int tabIndex = 0; tabIndex < tabs.length; tabIndex++) {
            stringListHashMap.put(tabs[tabIndex], new ArrayList<Stream>());
        }

    }

    /**
     * Sort regionlist according  to users location
     * Logic :
     * Check each region lat long with user lat long
     * get distance and according to distance
     * then sort regionlist
     */

    public void sortRegionList() {
        Log.d(TAG, " sortRegionList " + regionList.size());
        if (regionList.size() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            final Location userCurrentLocation = Util.PREFERENCES.getUserCurrentLocation(context);
            Collections.sort(regionList, new Comparator<Region>() {
                @Override
                public int compare(Region o1, Region o2) {
                    float[] result1 = new float[3];
                    android.location.Location.distanceBetween(Double.parseDouble(userCurrentLocation.getLatitude()), Double.parseDouble(userCurrentLocation.getLongitude()), Double.parseDouble(o1.getLatitude()), Double.parseDouble(o1.getLongitude()), result1);
                    Float distance1 = result1[0];

                    float[] result2 = new float[3];
                    android.location.Location.distanceBetween(Double.parseDouble(userCurrentLocation.getLatitude()), Double.parseDouble(userCurrentLocation.getLongitude()), Double.parseDouble(o2.getLatitude()), Double.parseDouble(o2.getLongitude()), result2);
                    Float distance2 = result2[0];
                    Log.d(TAG, "sortRegionList Distance : " + distance1 + " : " + distance2);
                    Log.d(TAG, "sortRegionList Distance : " + distance1.compareTo(distance2));
                    return distance1.compareTo(distance2);
                }
            });
            Log.d(TAG, "sortRegionList After Sorting :" + currentSelectedRegionIndex);
            //for (Region region : regionList) {
            //    Log.d(TAG, "sortRegionList in for " + region.getName());
            //}

            regionAdapter.notifyDataSetChanged();
            autocompleteRegionList.setSelection(currentSelectedRegionIndex);
            autocompleteRegionList.setAdapter(regionAdapter);
            Log.d(TAG, " sortRegionList Completed ");

            userAction.loadStream(regionList.get(currentSelectedRegionIndex), 0);
            Log.d(TAG, "loadRegionSuccess " + regionList.get(currentSelectedRegionIndex).getRegionId());
            //userAction.listenRegionValueChange(regionList.get(currentSelectedRegionIndex));
        }
    }

    //*******************************************Location Permission*********************************************

    public boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                ) {
            Log.d(TAG, " checkLocationPermission under if ");

            // Should we show an explanation?
            if ((shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION))) {
                Log.d(TAG, " checkLocationPermission under Second if ");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                if (alertPermissionDialog == null) {
                    alertPermissionDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.title_location_permission)
                            .setMessage(R.string.text_location_permission)
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                    intent.setData(uri);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivityForResult(intent, LOCATION_SETTING_SCREEN);

                                }
                            })
                            .create();
                    alertPermissionDialog.show();
                }

            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            Log.d(TAG, "checkLocationPermission return false");
            return false;
        } else {
            //createLocationRequest();
            Log.d(TAG, "checkLocationPermission return true");
            return true;
        }
    }


    public void createLocationRequest() {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000 / 2);

        //makeLocationSettingsResult();
    }

    public void getLocationSettingsResult() {

        LocationSettingsRequest.Builder locationRequestBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        locationRequestBuilder.setAlwaysShow(true);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(locationRequestBuilder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, " task.addOnSuccessListener");
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode) {
                    case CommonStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(getActivity(),
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        if (checkLocationPermission() && mLocationRequest != null) {
            Log.d(TAG, "startLocationUpdates under if");
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult" + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (isGPSEnabled()) {
                            getLocationSettingsResult();
                        } else {
                            Log.d(TAG, " Get Location Update ");
                            startLocationUpdates();
                        }

                    }
                } else {
                    checkLocationPermission();
                }
                return;
            }
            //case

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " onActivityResult " + requestCode);
        switch (requestCode) {
            case LOCATION_SETTING_SCREEN: {
                checkLocationPermission();
                return;
            }
            case REQUEST_CHECK_SETTINGS: {
                Log.d(TAG, " :: " + requestCode + " " + resultCode);
                //if (!(resultCode == Activity.RESULT_OK)) {
                //}
                startLocationUpdates();
            }

        }
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    public boolean isGPSEnabled() {
        return ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            Log.d(TAG, "In AddressResultReceiver > onReceiveResult");

            if (resultCode == Util.Constants.SUCCESS_RESULT) {
                    Address address = resultData.getParcelable(Util.Constants.RESULT_DATA_KEY)  ;
                    Region currentRegion = new Region(address.getSubLocality(), address.getLocality());
                    Log.d(TAG, "onConnected >> " + address.getSubLocality() + " : " + address.getLocality());
                    Log.d(TAG, "onConnected >> " + address.toString());
                    Util.PREFERENCES.setUserRegion(context, currentRegion);
                    if (!currentRegion.getRegionName().equals(currentUserRegionTitle)) {
                        sortRegionList();
                        currentUserRegionTitle = currentRegion.getRegionName();
                    }
            }

        }
    }
}
