package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.usiellau.conferenceol.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * Created by UsielLau on 2018/3/6 0006 3:19.
 */

public class PartpListAdapter extends RecyclerView.Adapter<PartpListAdapter.ViewHolder> {

    private String TAG="PartpListAdapter";

    private Context mContext;
    private RtcEngine rtcEngine;

    private List<Integer> uidList=new ArrayList<>();
    private SparseArray<ViewHolder> holderMap=new SparseArray<>();


    public PartpListAdapter(Context context,RtcEngine rtcEngine){
        mContext=context;
        this.rtcEngine=rtcEngine;
    }

    public void setUidList(List<Integer> uidList){
        int index=uidList.indexOf(PreferenceManager.getDefaultSharedPreferences(mContext).getInt("uid",0));
        if(index!=-1){
            uidList.remove(index);
        }
        Log.d(TAG,"适配器更新数据，新的数据集为：");
        for(Integer i:uidList){
            Log.d(TAG,i+",");
        }
        this.uidList=uidList;
        notifyDataSetChanged();
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.view_partp,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int uid=uidList.get(position);
        SurfaceView surfaceView = (SurfaceView)holder.videoContainer.getChildAt(0);
        if(surfaceView==null){
            surfaceView=RtcEngine.CreateRendererView(mContext);
            surfaceView.setZOrderMediaOverlay(true);
            holder.videoContainer.addView(surfaceView);
        }
        rtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid);
        holderMap.append(uid,holder);
    }

    @Override
    public int getItemCount() {
        return uidList.size();
    }

    public void onRemoteUserLeft(int uid){
        uidList.remove(Integer.valueOf(uid));
        holderMap.remove(uid);
        notifyDataSetChanged();
    }

    public void onRemoteUserVideoMuted(int uid,boolean muted){
        ViewHolder holder=holderMap.get(uid);
        SurfaceView surfaceView=(SurfaceView)holder.videoContainer.getChildAt(0);
        surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        if(muted){
            holder.videoStatusIv.setImageResource(R.drawable.ic_videocam_off_white_24dp);
        }else{
            holder.videoStatusIv.setImageResource(R.drawable.ic_videocam_white_24dp);
        }

    }

    public void onRemoteUserAudioMuted(int uid,boolean muted){
        ViewHolder holder=holderMap.get(uid);
        if(muted){
            holder.micStatusIv.setImageResource(R.drawable.ic_mic_off_white_24dp);
        }else{
            holder.micStatusIv.setImageResource(R.drawable.ic_mic_white_24dp);
        }
    }


    public void onRemoteUserJoined(int uid){
        if(uidList.contains(uid))return;
        uidList.add(uid);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.video_container)
        FrameLayout videoContainer;
        @BindView(R.id.video_status_iv)
        ImageView videoStatusIv;
        @BindView(R.id.mic_status_iv)
        ImageView micStatusIv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }



}
