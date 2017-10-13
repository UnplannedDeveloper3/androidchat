package atritechnocrat.com.locationchatapplication.dashboard.home.stream;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atritechnocrat.com.locationchatapplication.R;
import atritechnocrat.com.locationchatapplication.chat.ChatActivity;
import atritechnocrat.com.locationchatapplication.conductor.base.BaseController;
import atritechnocrat.com.locationchatapplication.conductor.util.BundleBuilder;
import atritechnocrat.com.locationchatapplication.pojo.AllStreams;
import atritechnocrat.com.locationchatapplication.pojo.Region;
import atritechnocrat.com.locationchatapplication.pojo.Stream;
import atritechnocrat.com.locationchatapplication.util.Util;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by admin on 09-08-2017.
 */

public class StreamController extends BaseController implements StreamControllerContract.View {
    private static final String KEY_TITLE = "ChildController.title";
    private static final String KEY_STREAMLIST = "ChildController.stream";
    private static final String TAG = "StreamController";
    private static final String KEY_REGION = "region";
    @BindView(R.id.streamRecycleView)
    RecyclerView streamRecycleView;
    List<Stream> streamList;
    StreamControllerContract.UserAction userActionPresenter ;
    Region currentRegion;
    public boolean isStreamClicked = false;



    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.layout_stream_controller, container, false);
    }

    public StreamController(String title,List<Stream>  streamList,Region region) {
        this(new BundleBuilder(new Bundle())
                .putString(KEY_TITLE, title)
                .putParcelable(KEY_STREAMLIST,new AllStreams(streamList))
                .putParcelable(KEY_REGION, region)
                .build());


        //
        Log.d(TAG,"StreamController :"+title);
        Log.d(TAG,"StreamController"+streamList.size());
        //stringListMap.put(title,new ArrayList<Stream>());

        //this.streamList = streamList;
        //Log.d(TAG, " StreamController " + streamList.size());
        // Contains Recycler view
        // On Click of Item it redirect to ChatActivity


    }

    public StreamController(Bundle args) {
        super(args);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        String title = getArgs().getString(KEY_TITLE);

        userActionPresenter = new StreamControllerPresenter(getActivity(),this);
        //userActionPresenter.loadStreams((Region) getArgs().getParcelable(KEY_STREAMLIST));



        streamList = ((AllStreams) getArgs().getParcelable(KEY_STREAMLIST)).getStreams();
        //Collections.reverse(streamList);
        currentRegion = getArgs().getParcelable(KEY_REGION) ;


        streamRecycleView.setHasFixedSize(true);
        streamRecycleView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        streamRecycleView.setAdapter(new RecyclerStreamAdapter(LayoutInflater.from(view.getContext()), streamList, getActivity()));
        //tvTitle.setText(getArgs().getString(KEY_TITLE));
        //Log.d(TAG, " onViewBound " + streamList.size());
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return super.onCreateView(inflater, container);
    }

    public void onStreamClick(Stream stream) {
        // and redirect user to that String
        Log.d(TAG,"onStreamClick");
        //streamRecycleView.setClickable(false);
        if(!isStreamClicked){
        userActionPresenter.validateStream(stream);
            isStreamClicked= true;
        }
    }

    @Override
    public void validateStreamSuccess(Stream stream) {
        isStreamClicked= false;
        Log.d(TAG,"validateStreamSuccess");
        //streamRecycleView.setClickable(true);
        // Call Chat Activity
        Intent chatIntent = new Intent(getActivity(),ChatActivity.class);
        chatIntent.putExtra(Util.STREAM_DATA,stream);
        chatIntent.putExtra(Util.REGION_DATA,currentRegion);
        startActivity(chatIntent);


    }

    @Override
    public void onLoadStreamSuccess() {

    }

    @Override
    public void onLoadStreamFail() {

    }


    // Adapter
    public class RecyclerStreamAdapter extends RecyclerView.Adapter<RecyclerStreamAdapter.RecyclerStreamViewHolder> {
        private final LayoutInflater inflater;
        private List<Stream> streamList;
        private Context context;


        public RecyclerStreamAdapter(LayoutInflater inflater, List<Stream> streamList, Context context) {
            this.inflater = inflater;
            this.streamList = streamList;
            this.context = context;
        }

        @Override
        public RecyclerStreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerStreamViewHolder(inflater.inflate(R.layout.layout_item_stream_info, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerStreamViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return streamList.size();
        }

        public class RecyclerStreamViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.title)
            TextView title;
            @BindView(R.id.userid)
            TextView userid;
            @BindView(R.id.date)
            TextView date;
            Stream currentStream;

            public RecyclerStreamViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bind(int position){
                currentStream = streamList.get(position);
                title.setText(currentStream.getName());
                userid.setText(currentStream.getOwner().getName());

                String start_dt = currentStream.getCreated_at();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                Date streamDate = null;
                try {
                    streamDate = (Date)formatter.parse(start_dt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat newFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
                String finalString = newFormat.format(streamDate);

                date.setText(finalString);
            }

            @OnClick(R.id.item_root)
            public void onRowClick() {

                onStreamClick(currentStream);
            }
        }
    }


}
