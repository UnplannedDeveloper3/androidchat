package atritechnocrat.com.locationchatapplication.dashboard.notification;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.conductor.base.BaseController;

/**
 * Created by admin on 09-08-2017.
 */

public class NotificationController extends BaseController {
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.layout_notification_controller, container, false);
    }


    @Override
    protected String getTitle() {
        return getActivity().getString(R.string.title_activity_dashboard);
    }
}
