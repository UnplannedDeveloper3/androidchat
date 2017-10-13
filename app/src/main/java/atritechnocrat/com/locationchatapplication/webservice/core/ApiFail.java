package atritechnocrat.com.locationchatapplication.webservice.core;


import android.util.Log;
import retrofit2.adapter.rxjava.HttpException;
import rx.functions.Action1;

/**
 * Created by ashwinnbhanushali on 04/11/16.
 */

public abstract class ApiFail implements Action1<Throwable> {
    @Override
    public void call(Throwable e) {
        if (e instanceof HttpException) {
            HttpException http = (HttpException) e;
            HttpErrorResponse response = new HttpErrorResponse(http.code(), http.response());
            response.setError(http.message());

            // Error response Should be read from error body ( reason field )
            try {
                String body = http.response().errorBody().string().trim();

                Log.d("ApiFail", body);




                response.setError(body);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }else{
            unknownError(e);
        }
    }

    public abstract void httpStatus(HttpErrorResponse response);

    public abstract void noNetworkError();

    public abstract void unknownError(Throwable e);

    //public abstract void socketTimeout();
}
