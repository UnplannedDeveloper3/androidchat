package atritechnocrat.com.locationchatapplication.chat;

import java.util.HashMap;

import atritechnocrat.com.locationchatapplication.pojo.Message;
import atritechnocrat.com.locationchatapplication.pojo.Stream;

/**
 * Created by admin on 09-08-2017.
 */

public class ChatContract {

    public interface View{
        public void onSubmitMessageSuccess();
        public void onSubmitMessageFail();
        public void onFetchUserInStream(HashMap<String,String> userInfo);
    }

    public interface UserAction{
        public void onSubmitMessage(Message message);
        public void fetchAllUsersInStream(Stream stream);
    }

}
