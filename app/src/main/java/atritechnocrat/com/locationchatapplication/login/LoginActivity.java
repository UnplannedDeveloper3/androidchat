package atritechnocrat.com.locationchatapplication.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.dashboard.DashboardActivity;
import atritechnocrat.com.locationchatapplication.pojo.AllStreams;
import atritechnocrat.com.locationchatapplication.pojo.User;
import atritechnocrat.com.locationchatapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/*
*  Fields Present
*  1. Enter username
*  2. Login Button
*  Login Screen passes following data to Dashboard Activity
*  1. User pojo as parceble to Dashboard screen
*  2. Available Steams
* */
public class LoginActivity extends AppCompatActivity implements LoginContract.View {
    @BindView(R.id.usernameEditText)
    EditText usernameEditText;
    @BindView(R.id.submit)
    Button submit;

    private static final String TAG = "LoginActivity";
    LoginContract.UserAction userActionPresenter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    ProgressDialog progressDialog;
    @BindView(R.id.loginview)
    LinearLayout loginview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_login);

        FirebaseMessaging.getInstance().subscribeToTopic(Util.DEFAULT_NOTIFICATION_TOPIC);

        //Log.d(TAG," FCM TOKEN > "+FirebaseInstanceId.getInstance().getToken());
        Util.PREFERENCES.setNotificationRegId(LoginActivity.this, FirebaseInstanceId.getInstance().getToken());


        // If User is Already Login Goto dashboard screen directly
        userActionPresenter = new LoginPresenter(getApplicationContext(), LoginActivity.this);

        if (!Util.PREFERENCES.getUserId(LoginActivity.this).equals("")) {
            loginview.setVisibility(View.GONE);
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.title_login);
            progressDialog.setMessage(getString(R.string.processing));
            progressDialog.show();

            userActionPresenter.validateUser(Util.PREFERENCES.getUserId(LoginActivity.this));
            //onUserSubmitSuccess(Util.PREFERENCES.getUserInfo(LoginActivity.this));
        }


    }

    @Override
    public void onUserSubmitSuccess(User user) {
        Log.d(TAG, user.getName());
        // Save UserId to
        Util.PREFERENCES.setLogin(LoginActivity.this, user);
        Intent login = new Intent(LoginActivity.this, DashboardActivity.class);
        login.putExtra(Util.USER_DATA, user);
        // For Initial developmen, We send all Streams
        login.putExtra(Util.STREAM_DATA, new AllStreams());
        startActivity(login);
        finish();
    }

    @Override
    public void onUserSubmitFail() {
        Log.d(TAG, "User Detail Submit Fail");
    }

    @Override
    public void validateUserSuccess(User user) {
        progressDialog.dismiss();
        Log.d(TAG, "validateUserSuccess " + user.getName());
        onUserSubmitSuccess(user);
    }

    @Override
    public void validateUserFail() {
        progressDialog.dismiss();
        loginview.setVisibility(View.VISIBLE);
        Log.d(TAG, "validateUserFail ");
    }

    @OnClick(R.id.submit)
    public void onViewClicked() {
        Log.d(TAG, "Submit Btn Clicked");
        if (!usernameEditText.getText().toString().equals("")) {
            userActionPresenter.submitUserDetails(new User(usernameEditText.getText().toString()));
            Log.d(TAG, "UserName added to database");
        }
    }
}
