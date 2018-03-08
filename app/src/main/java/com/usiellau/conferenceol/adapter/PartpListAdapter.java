package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.juphoon.cloud.JCMediaChannel;
import com.juphoon.cloud.JCMediaChannelParticipant;
import com.juphoon.cloud.JCMediaDevice;
import com.juphoon.cloud.JCMediaDeviceVideoCanvas;
import com.usiellau.conferenceol.JCWrapper.JCManager;
import com.usiellau.conferenceol.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/3/6 0006 3:19.
 */

public class PartpListAdapter extends RecyclerView.Adapter<PartpListAdapter.ViewHolder> {

    private String TAG="PartpListAdapter";

    private Context mContext;
    private List<Item> items=new ArrayList<>();

    public PartpListAdapter(Context context){
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.view_partp,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Item item=items.get(position);

        if (isSelf(item)) {
            if (item.partp.isVideo()) {
                    item.canvas = JCManager.getInstance().mediaDevice.startCameraVideo(JCMediaDevice.RENDER_FULL_CONTENT);
                    SurfaceView surfaceView=item.canvas.getVideoView();
                    surfaceView.setZOrderMediaOverlay(true);
                    holder.videoContainer.addView(surfaceView, 0);

            }
        } else {
            if (item.partp.isVideo()) {
                    JCManager.getInstance().mediaChannel.requestVideo(item.partp, JCMediaChannel.PICTURESIZE_LARGE);
                    item.canvas = JCManager.getInstance().mediaDevice.startVideo(item.partp.getRenderId(), JCMediaDevice.RENDER_FULL_CONTENT);
                    SurfaceView surfaceView=item.canvas.getVideoView();
                    surfaceView.setZOrderMediaOverlay(true);
                    holder.videoContainer.addView(surfaceView, 0);
            }
        }
    }

    @Override
    public int getItemCount() {
        Log.e(TAG,"获取item项目数："+items.size());
        return items.size();
    }

    class Item {
        JCMediaChannelParticipant partp;
        JCMediaDeviceVideoCanvas canvas;

        public Item(JCMediaChannelParticipant partp) {
            this.partp = partp;

        }
        public void stop() {
            if (canvas != null) {
                // 关闭视频请求
                if (!isSelf(this)) {
                    JCManager.getInstance().mediaChannel.requestVideo(partp, JCMediaChannel.PICTURESIZE_NONE);
                }
                JCManager.getInstance().mediaDevice.stopVideo(canvas);
            }
        }
    }



    public void updatePartp() {
        Log.e(TAG,"updatePartp被调用");

        List<JCMediaChannelParticipant> participants=JCManager.getInstance().mediaChannel.getParticipants();
        Toast.makeText(mContext, "获取到成员数："+participants.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; ; i++) {
            if (i < participants.size()) {
                JCMediaChannelParticipant partp = participants.get(i);
                Item item;
                if (items.size() <= i) {
                    item = new Item(partp);
                    items.add(item);
                } else {
                    item = items.get(i);
                }
                if (item.partp != partp) {
                    item.stop();
                    item.partp = partp;
                }
                continue;
            } else if (i < items.size()) {
                for (int j = items.size() - 1; j >= i; j--) {
                    items.get(j).stop();
                    items.remove(j);
                }
            }
            break;
        }
        Log.e(TAG,"获取到成员数："+participants.size());

        notifyDataSetChanged();
    }

    private boolean isSelf(Item item) {
        return TextUtils.equals(item.partp.getUserId(), JCManager.getInstance().client.getUserId());
    }

    public void destory(){
        for(Item item:items){
            item.stop();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.video_container)
        FrameLayout videoContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
