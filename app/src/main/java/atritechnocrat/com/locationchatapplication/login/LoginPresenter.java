package atritechnocrat.com.locationchatapplication.login;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;

/**
 * Created by admin on 09-08-2017.
 */
/*
* By default Newyork stream assign to each user
 */

public class LoginPresenter implements LoginContract.UserAction {
    Context context;
    LoginContract.View view;
    private DatabaseReference mFirebaseDatabaseReference;
    private String TAG = "LoginPresenter";


    public LoginPresenter(Context context, LoginContract.View view) {
        this.context = context;
        this.view = view;
        mFirebaseDatabaseReference =  FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void submitUserDetails(User user) {
        // Create Field of UserId in database
        String userId = mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_USERS).push().getKey();
        user.setDeviceToken(Util.PREFERENCES.getNotificationRegId(context));
        mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_USERS).child(userId).setValue(user);
        user.setUserId(userId);

        view.onUserSubmitSuccess(user);
        //view.onUserSubmitFail();
        // Perform Database operation here
        //mFirebaseDatabaseReference.


    }

    @Override
    public void validateUser(String userId) {
        Log.d(TAG,"validateUser");
        mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_USERS)
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.d(TAG,dataSnapshot.toString());
                            User currentUser = dataSnapshot.getValue(User.class);
                            currentUser.setUserId(dataSnapshot.getKey());
                            view.validateUserSuccess(currentUser);
                        }else{
                            view.validateUserFail();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
