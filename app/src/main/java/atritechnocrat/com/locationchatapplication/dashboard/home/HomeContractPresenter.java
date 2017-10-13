package atritechnocrat.com.locationchatapplication.dashboard.home;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.util.Util;
import atritechnocrat.com.locationchatapplication.webservice.Api;
import atritechnocrat.com.locationchatapplication.webservice.core.ApiFail;
import atritechnocrat.com.locationchatapplication.webservice.core.ApiSuccess;
import atritechnocrat.com.locationchatapplication.webservice.core.HttpErrorResponse;
import atritechnocrat.com.locationchatapplication.webservice.requestPojo.SubmitStreamRequest;
import atritechnocrat.com.locationchatapplication.webservice.responsePojo.BaseResponse;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by admin on 09-08-2017.
 */

public class HomeContractPresenter implements HomeControllerContract.UserAction {
    private static final String TAG = "HomeContractPresenter";
    Context context;
    HomeControllerContract.View view;
    private DatabaseReference mFirebaseDatabaseReference;
    private DatabaseReference allRegionDbReference;
    private DatabaseReference singleRegionDbReference;
    private List<Region> regionList;
    //private List<Stream> streamList;
    Map<String, List<Stream>> stringListMap;
    String[] tabs;
    long availableStreamsInRegion = 0;
    DatabaseReference mRegionsStramDbRef;
    ValueEventListener mStreamsChangeListener;
    Region currentRegionSelected;

    public HomeContractPresenter(Context context, HomeControllerContract.View view, List<Region> regionList, String[] tabs, Map<String, List<Stream>> stringListMap) {
        this.context = context;
        this.view = view;
        this.regionList = regionList;
        this.tabs = tabs;
        this.stringListMap = stringListMap;
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void addStream(String streamTitle) {

        SubmitStreamRequest submitStreamRequest =
                new SubmitStreamRequest(Util.PREFERENCES.getUserRegion(context), new Stream(streamTitle), Util.PREFERENCES.getUserCurrentLocation(context));
        submitStreamRequest.setUser(Util.PREFERENCES.getUserInfo(context));

        Api.chatApis().submitStream(submitStreamRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ApiSuccess<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        Log.d(TAG, "subscribe > call");
                        view.addStreamSuccess();
                    }
                }, new ApiFail() {
                    @Override
                    public void httpStatus(HttpErrorResponse response) {
                        Log.d(TAG, "addStream > httpStatus");
                        view.addStreamFail();
                    }

                    @Override
                    public void noNetworkError() {
                        Log.d(TAG, "addStream > noNetworkError");
                        view.addStreamFail();
                    }

                    @Override
                    public void unknownError(Throwable e) {
                        e.printStackTrace();
                        Log.d(TAG, "addStream > unknownError");
                        view.addStreamFail();
                    }
                });
    }

    @Override
    public void loadRegions(List<Region> streamList) {
        // Get all Regions
        allRegionDbReference = mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_REGIONS);
        allRegionDbReference.addValueEventListener(new LoadAllRegions());

    }

    @Override
    public void loadStream(final Region region, final int tabIndex) {
        Log.d(TAG, "loadStream. " + region.getStreams().keySet().size());

        stringListMap.get(tabs[tabIndex]).clear();

        final int totleStreamsInRegion = region.getStreams().entrySet().size();

        for (final Map.Entry<String, Boolean> entry : region.getStreams().entrySet()) {
            Log.d(TAG, "Streams. " + entry.getKey());

            final Query getStreamRef = mFirebaseDatabaseReference
                    .child(Util.FIREBASE.FIELD_STREAMS)
                    .child(entry.getKey())
                    .orderByKey();

            final ValueEventListener getStreamByIdListener = new ValueEventListener() {


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Stream currentStream = dataSnapshot.getValue(Stream.class);
                    currentStream.setStreamId(dataSnapshot.getKey());
                    getStreamRef.removeEventListener(this);

                    Log.d(TAG, "StreamId " + currentStream.getStreamId());
                    stringListMap.get(tabs[tabIndex]).add(currentStream);

                    if (totleStreamsInRegion == stringListMap.get(tabs[tabIndex]).size()) {
                        /**
                         * tabIndex is by default 0 for latest tab
                         * As All the Streams are added into stringListMap.get(tabs[tabIndex]) ( i.e,-> streamlist for latest tab )
                         * for particular Regions selected by user
                         * then call sort method
                         * and then call loadStreamSuccess method of view
                         */
                        Collections.sort(stringListMap.get(tabs[tabIndex]));
                        Log.d(TAG, "loadStream:onDataChange " + currentStream.getName());
                        view.loadStreamSuccess();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            getStreamRef.addListenerForSingleValueEvent(getStreamByIdListener);
        }

    }

    private class LoadAllRegions implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, "LoadAllRegions : " + dataSnapshot.toString());

            regionList.clear();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                Region currentRegion = child.getValue(Region.class);
                currentRegion.setRegionId(child.getKey());
                Log.d(TAG, "currentRegion " + currentRegion.getRegionId() + " : " + currentRegion.getStreams().keySet());
                regionList.add(currentRegion);
            }
            allRegionDbReference.removeEventListener(this);
            view.loadRegionSuccess();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            view.loadRegionFail();
        }
    }


    /**
     * listenRegionValueChange
     * Will listen value change in user current regions > streams node in firebase database
     * It help to update the streams row
     */
    @Override
    public void listenRegionValueChange(final Region region) {
        Log.d(TAG, " listenRegionValueChange ");
        // Remove previouse listener if available
        if (mRegionsStramDbRef != null && mStreamsChangeListener != null) {
            mRegionsStramDbRef.removeEventListener(mStreamsChangeListener);
        }

        availableStreamsInRegion = region.getStreams().keySet().size();
        currentRegionSelected = region;
        mRegionsStramDbRef = mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_REGIONS)
                .child(region.getRegionId())
                .child(Util.FIREBASE.FIELD_STREAMS);

        mStreamsChangeListener = new StreamValueChangeListener();
        mRegionsStramDbRef.addValueEventListener(mStreamsChangeListener);
    }


    private class StreamValueChangeListener implements ValueEventListener {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Log.d(TAG, " onDataChange ");
            long currentStramsCountInUpdatedRegion = dataSnapshot.getChildrenCount();
            Log.d(TAG, "listenRegionValueChange onChildAdded " + dataSnapshot.toString());
            if (currentStramsCountInUpdatedRegion > availableStreamsInRegion) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    if (!currentRegionSelected.getStreams().keySet().contains(snap.getKey())) {
                        currentRegionSelected.getStreams().put(snap.getKey(), true);
                    }
                }
                loadStream(currentRegionSelected, 0);
                availableStreamsInRegion = currentStramsCountInUpdatedRegion;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
