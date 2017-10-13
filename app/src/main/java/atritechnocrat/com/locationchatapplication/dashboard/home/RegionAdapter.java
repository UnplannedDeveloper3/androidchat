package atritechnocrat.com.locationchatapplication.dashboard.home;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;

/**
 * Created by admin on 16-08-2017.
 */

public class RegionAdapter  extends ArrayAdapter<Region> {

    LayoutInflater flater;
    List<Region> regionList;

    public RegionAdapter(@NonNull Activity context, @LayoutRes int resource, @NonNull List<Region> objects) {
        super(context, resource, objects);
        flater = context.getLayoutInflater();
        this.regionList = objects;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return  getView(position, convertView,parent);
        //super.getDropDownView(position, convertView, parent);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Region currentRegion = regionList.get(position);
        View rowview = flater.inflate(R.layout.textview_stream_spinner,null,true);
        TextView txtTitle = (TextView) rowview.findViewById(R.id.stream_name);
        txtTitle.setText(currentRegion.getName());
        return rowview;
    }



}

