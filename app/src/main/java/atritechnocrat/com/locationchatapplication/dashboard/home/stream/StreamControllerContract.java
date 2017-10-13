package atritechnocrat.com.locationchatapplication.dashboard.home.stream;

import atritechnocrat.com.locationchatapplication.pojo.Message;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;

/**
 * Created by admin on 09-08-2017.
 */

public class StreamControllerContract {

    public interface View{
        public void validateStreamSuccess(Stream stream);
        public void onLoadStreamSuccess();
        public void onLoadStreamFail();
    }

    public interface UserAction{
        public void validateStream(Stream stream);
        public void loadStreams(Region region);
    }

}
