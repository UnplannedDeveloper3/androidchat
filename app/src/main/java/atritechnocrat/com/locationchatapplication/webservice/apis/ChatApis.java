package atritechnocrat.com.locationchatapplication.webservice.apis;



import atritechnocrat.com.locationchatapplication.webservice.URLS;
import atritechnocrat.com.locationchatapplication.webservice.requestPojo.SubmitStreamRequest;
import atritechnocrat.com.locationchatapplication.webservice.responsePojo.BaseResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

public interface ChatApis {

    @POST(URLS.ROOTPATH + "addStreamIntoRegion")
    Observable<BaseResponse> submitStream(@Body SubmitStreamRequest submitStreamRequest);

}
