package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.entity.ConfIng;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/1/22 0022 8:12.
 */

public class ConfRvAdapter extends RecyclerView.Adapter<ConfRvAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private Context context;
    private List<ConfIng> confListData;

    public ConfRvAdapter(Context context,List<ConfIng> confListData){
        this.context=context;
        this.confListData=confListData;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_conf_list,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvConfTitle.setText(confListData.get(position).getTitle());
        holder.tvRoomId.setText("房间号："+String.valueOf(confListData.get(position).getId()));
        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return confListData.size();
    }

    public void setData(List<ConfIng> data){
        this.confListData=data;
    }
    public List<ConfIng> getData(){
        return confListData;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_conf_title)
        TextView tvConfTitle;
        @BindView(R.id.tv_room_id)
        TextView tvRoomId;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
