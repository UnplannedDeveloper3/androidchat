package atritechnocrat.com.locationchatapplication.chat;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import atritechnocrat.com.locationchatapplication.pojo.Message;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;

/**
 * Created by admin on 09-08-2017.
 */

public class ChatPresenter implements ChatContract.UserAction {

    private static final String TAG = "ChatPresenter";
    Context context;
    ChatContract.View view;
    private DatabaseReference mFirebaseDatabaseReference;

    public ChatPresenter(Context context, ChatContract.View view) {
        this.context = context;
        this.view = view;
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onSubmitMessage(Message message) {

    }

    @Override
    public void fetchAllUsersInStream(Stream stream) {
        final HashMap<String, String> userInfo = new HashMap<>();
        Log.d(TAG, " In  fetchAllUsersInStream");
        mFirebaseDatabaseReference
                .child(Util.FIREBASE.FIELD_STREAM_MEMBER)
                .child(stream.getStreamId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, dataSnapshot.toString() + " :: " + dataSnapshot.getChildren().iterator().hasNext());

                        Map<String, Boolean> tempAllMemeber = new HashMap<String, Boolean>();
                        for (DataSnapshot member : dataSnapshot.getChildren()) {
                            Boolean isAvailable = member.getValue(Boolean.class);
                            tempAllMemeber.put(member.getKey(), isAvailable);
                        }
                        final Map<String, Boolean> allMembers = tempAllMemeber;
                        Log.d(TAG, allMembers.keySet().toString());

                        if (allMembers.keySet().size() > 0) {

                            for (final String userId : allMembers.keySet()) {
                                final Query fetchMemberInfoQuery = mFirebaseDatabaseReference
                                        .child(Util.FIREBASE.FIELD_USERS)
                                        .child(userId);

                                fetchMemberInfoQuery.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot userdDataSnapshot) {
                                        String userName = "";
                                        try {

                                            userName = userdDataSnapshot.getValue(User.class).getName();
                                            Log.d(TAG, userdDataSnapshot.getValue(User.class).getDeviceToken());
                                        } catch (Exception e) {
                                            userName = "USER";
                                        }
                                        userInfo.put(userId, userName);

                                        fetchMemberInfoQuery.removeEventListener(this);
                                        Log.d(TAG, " userInfo.size >  " + userInfo.size() + " allMembers " + allMembers.size());
                                        if (userInfo.size() == allMembers.size()) {
                                            // If both HashMap has same size then
                                            // userInfo has all user involved in stream
                                            view.onFetchUserInStream(userInfo);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }else {
                            view.onFetchUserInStream(userInfo);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
