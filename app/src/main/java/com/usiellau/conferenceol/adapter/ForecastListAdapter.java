package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.entity.ConfForecast;
import com.usiellau.conferenceol.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/3/21 0021 14:18.
 */

public class ForecastListAdapter extends RecyclerView.Adapter<ForecastListAdapter.ViewHolder> {

    private Context mContext;
    private OnItemClickListener onItemClickListener;
    private List<ConfForecast> datas=new ArrayList<>();

    public ForecastListAdapter(Context context){
        mContext=context;
    }

    public void setDatas(List<ConfForecast> datas){
        this.datas=datas;
        notifyDataSetChanged();
    }

    public List<ConfForecast> getDatas(){
        return datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_forecast_list,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ConfForecast forecast=datas.get(position);
        holder.tvConfTitle.setText(forecast.getTitle());
        holder.tvStartTime.setText(Utils.longTime2String(forecast.getStartTime()));

        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(holder.itemView,position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_conf_title)
        TextView tvConfTitle;
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

}
