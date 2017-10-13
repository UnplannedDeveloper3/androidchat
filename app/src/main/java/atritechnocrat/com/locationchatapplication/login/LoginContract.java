package atritechnocrat.com.locationchatapplication.login;

import atritechnocrat.com.locationchatapplication.pojo.User;

/**
 * Created by admin on 09-08-2017.
 */

public class LoginContract {

    public interface View{
        //public void
        public void onUserSubmitSuccess(User user);
        public void onUserSubmitFail();
        public void validateUserSuccess(User user);
        public void validateUserFail();


    }
    public interface UserAction{
        public void submitUserDetails(User user);
        public void validateUser(String userId);
    }

}
