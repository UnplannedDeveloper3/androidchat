package atritechnocrat.com.locationchatapplication.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.ChangeEventListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.pojo.Message;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    private static final String TAG = "ChatActivity";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;
    @BindView(R.id.addMessageImageView)
    ImageView addMessageImageView;
    @BindView(R.id.messageEditText)
    EditText messageEditText;
    @BindView(R.id.sendButton)
    Button sendButton;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    ChatContract.UserAction userActionPresenter;
    @BindView(R.id.no_message)
    TextView noMessage;

    private User currentUser;
    private Stream currentStream;
    private Region currentRegion;
    private HashMap<String, String> currentStremMembers;

    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;

    private MyDataObserver myDataObserver;
    private DatabaseReference mFirebaseDatabaseReference;

    // Checks is user is valid for posting the message
    private boolean isValidUserLocation = true;
    private boolean isUserPresentInStream = false;


    DatabaseReference dbRefForMessages;


    /**
     * Chat Activity require
     * Users: user in the chat system ( to show username )
     * Current UserInfo
     * streamId to load data
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.title_chat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();


        userActionPresenter = new ChatPresenter(getApplicationContext(), ChatActivity.this);

        currentStream = getIntent().getParcelableExtra(Util.STREAM_DATA);
        currentRegion = getIntent().getParcelableExtra(Util.REGION_DATA);

        isValidUserLocation = Util.PREFERENCES.isUserValidForStream(ChatActivity.this, currentRegion.getName());

        getSupportActionBar().setTitle(currentStream.getName());

        currentUser = Util.PREFERENCES.getUserInfo(ChatActivity.this);
        // Use in displaying names in Chat
        Log.d(TAG, " Above  fetchAllUsersInStream "+currentStream.getStreamId());

        userActionPresenter.fetchAllUsersInStream(currentStream);

        Log.d(TAG, " below  fetchAllUsersInStream");


    }

    @Override
    public void onSubmitMessageSuccess() {

    }

    @Override
    public void onSubmitMessageFail() {

    }

    @Override
    public void onFetchUserInStream(HashMap<String, String> userInfo) {
        this.currentStremMembers = userInfo;

        Log.d(TAG," Current UserId "+currentUser.getUserId());
        Log.d(TAG," Current UserId "+currentStremMembers.keySet().toString());

        isUserPresentInStream = currentStremMembers.containsKey(currentUser.getUserId());

        //isUserPresentInStream = currentStremMembers.get(currentUser.getUserId());

        Log.d(TAG, userInfo.toString());

        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        if (mFirebaseAdapter != null) {
            mFirebaseAdapter.cleanup();
            if (myDataObserver != null) {
                mFirebaseAdapter.unregisterAdapterDataObserver(myDataObserver);
            }

        }
        dbRefForMessages = mFirebaseDatabaseReference.child(Util.FIREBASE.MESSAGES_CHILD).child(currentStream.getStreamId());

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                dbRefForMessages) {

            @Override
            protected Message parseSnapshot(DataSnapshot snapshot) {
                Message friendlyMessage = super.parseSnapshot(snapshot);
                if (friendlyMessage != null) {
                    friendlyMessage.setMessageId(snapshot.getKey());
                }
                return friendlyMessage;
            }

            @Override
            protected void onChildChanged(ChangeEventListener.EventType type, int index, int oldIndex) {
                super.onChildChanged(type, index, oldIndex);
            }

            @Override
            protected void populateViewHolder(final MessageViewHolder viewHolder,
                                              Message friendlyMessage, int position) {


                if (friendlyMessage.getMessage() != null) {
                    //viewHolder.userIdTextView.setText(friendlyMessage.getMessage());
                    viewHolder.userIdTextView.setText(friendlyMessage.getMessage());
                }


                Log.d(TAG, " messageOwnerId " + friendlyMessage.getUserId());
                String messageOwnerId = "";
                if (currentStremMembers != null) {
                    messageOwnerId = currentStremMembers.get(friendlyMessage.getUserId()) != null ? currentStremMembers.get(friendlyMessage.getUserId()) : friendlyMessage.getUserId();
                } else {
                    messageOwnerId = friendlyMessage.getUserId();
                }




                String messageOwnerName = messageOwnerId.equals("") ? friendlyMessage.getUserId() : messageOwnerId;

                if (friendlyMessage.getUserId().equals(currentUser.getUserId())) {
                    messageOwnerName = currentUser.getName();
                    viewHolder.parentContainer.setGravity(Gravity.RIGHT);

                } else {
                    viewHolder.parentContainer.setGravity(Gravity.CENTER_VERTICAL);
                }

                viewHolder.messageTextView.setText(messageOwnerName);
            }
        };

        Log.d(TAG,"Above addListenerForSingleValueEvent");
        dbRefForMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG,"  onDataChange > "+dataSnapshot.toString());
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                if(dataSnapshot.exists()){
                    noMessage.setVisibility(View.GONE);
                }else {
                    noMessage.setVisibility(View.VISIBLE);
                }
                dbRefForMessages.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        progressBar.setVisibility(ProgressBar.INVISIBLE);

        myDataObserver = new MyDataObserver();
        mFirebaseAdapter.registerAdapterDataObserver(myDataObserver);

        messageRecyclerView.setLayoutManager(mLinearLayoutManager);
        messageRecyclerView.setAdapter(mFirebaseAdapter);

        // Send Button functionality
        sendButton.setOnClickListener(new SendUserMessage());

        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView userIdTextView;
        TextView messageTextView;
        LinearLayout parentContainer;

        public MessageViewHolder(View v) {
            super(v);
            userIdTextView = (TextView) itemView.findViewById(R.id.userIdTextView);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            parentContainer = (LinearLayout) itemView.findViewById(R.id.parentContainer);
        }
    }

    private class MyDataObserver extends RecyclerView.AdapterDataObserver {
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            Log.d(TAG, " MyDataObserver " + itemCount);
            super.onItemRangeInserted(positionStart, itemCount);
            if(itemCount > 0 ){
                noMessage.setVisibility(View.GONE);
            }

            int friendlyMessageCount = mFirebaseAdapter.getItemCount();
            int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
            // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
            // to the bottom of the list to show the newly added message.
            if (lastVisiblePosition == -1 ||
                    (positionStart >= (friendlyMessageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                messageRecyclerView.scrollToPosition(positionStart);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void addUserIntoStream(String streamId, String userId){
        DatabaseReference memberStreamDbRef =  mFirebaseDatabaseReference.child(Util.FIREBASE.FIELD_STREAM_MEMBER).child(streamId) ;
        Map<String, Object> streamUpdate = new HashMap<String, Object>();
        streamUpdate.put(userId, true);
        memberStreamDbRef.updateChildren(streamUpdate);
    }

    private class SendUserMessage implements View.OnClickListener {
        /**
         * Logic
         * 1. Check if user is valid for that region location
         * 2. If user is not present in that stream than add that user into current Stream
         * 3. Post the message to firebase database
         * @param v
         */
        @Override
        public void onClick(View v) {
            if (isValidUserLocation) {
                if(!isUserPresentInStream){
                    addUserIntoStream(currentStream.getStreamId(),currentUser.getUserId());
                    isUserPresentInStream = true;
                }
                noMessage.setVisibility(View.GONE);
                Message friendlyMessage = new
                        Message(Util.PREFERENCES.getUserId(ChatActivity.this), messageEditText.getText().toString());

                Log.d(TAG, "On send button click : " + currentStream.getStreamId());

                mFirebaseDatabaseReference.child(Util.FIREBASE.MESSAGES_CHILD).child(currentStream.getStreamId())
                        .push().setValue(friendlyMessage);
                messageEditText.setText("");


            } else {
                Toast.makeText(ChatActivity.this, R.string.user_location_chat_warning, Toast.LENGTH_LONG).show();
            }
        }
    }
}
