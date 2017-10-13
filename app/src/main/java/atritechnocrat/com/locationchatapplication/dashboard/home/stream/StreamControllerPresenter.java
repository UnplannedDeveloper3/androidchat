package atritechnocrat.com.locationchatapplication.dashboard.home.stream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;

/**
 * Created by admin on 09-08-2017.
 */

public class StreamControllerPresenter implements StreamControllerContract.UserAction {
    private static final String TAG = "StreamCntlrPresenter";
    private Context context;
    private StreamControllerContract.View view;
    private DatabaseReference mFirebaseDatabaseReference;
    private User currentLoginUser ;
    //private List<Stream> streamList;

    public StreamControllerPresenter(Context context, StreamControllerContract.View view) {
        this.context = context;
        this.view = view;
        mFirebaseDatabaseReference =  FirebaseDatabase.getInstance().getReference();
        currentLoginUser = Util.PREFERENCES.getUserInfo(context);
        //this.streamList = streamList;
    }

    @Override
    public void validateStream(final Stream stream) {
        // first check wether it available or not
        Query streamFinderQuery = mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_STREAMS)
                .orderByChild("name")
                .equalTo(stream.getName())
                .limitToFirst(1);

        streamFinderQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG,"onDataChange : "+dataSnapshot.exists());


                String streamId ="";
                if (dataSnapshot.exists()) {
                    // if stream is already present
                    // Get first element key
                    streamId = dataSnapshot.getChildren().iterator().next().getKey();
                    Log.d(TAG,"onDataChange if "+streamId+" : "+dataSnapshot.toString());


                }else{
                    // First add stream field into database
                    streamId = mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_STREAMS).push().getKey();
                    mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_STREAMS).child(streamId).setValue(stream);

                    Log.d(TAG,"onDataChange else"+streamId);

                    // As the stream is newly created add user into that Query Add User into That stream
                }
                //addUserIntoStream(streamId,currentLoginUser.getUserId());
                stream.setStreamId(streamId);
                view.validateStreamSuccess(stream);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // if available add User into that stream
        // then call


    }

    @Override
    public void loadStreams(Region region) {

    }





}
