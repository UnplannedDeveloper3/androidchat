package atritechnocrat.com.locationchatapplication.dashboard.home;

import java.util.List;

import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;

/**
 * Created by admin on 09-08-2017.
 */

public class HomeControllerContract {
    public interface View{
        public void addStreamSuccess();
        public void addStreamFail();
        public void loadRegionSuccess();
        public void loadRegionFail();
        public void loadStreamSuccess();
        public void loadStreamFail();
        public void onRegionChildChange();

    }

    public interface UserAction{
        public void addStream(String streamTitle);
        public void loadRegions(List<Region> regionList);
        public void loadStream(Region region,int tabIndex);
        public void listenRegionValueChange(Region region);

    }
}
