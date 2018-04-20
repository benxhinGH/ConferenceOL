package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usiellau.conferenceol.R;
import com.usiellau.conferenceol.network.entity.ConfOver;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/4/20 0020 16:36.
 */
public class ConfRecordRvAdapter extends RecyclerView.Adapter<ConfRecordRvAdapter.ViewHolder> {

    private Context context;
    private List<ConfOver> datas=new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public ConfRecordRvAdapter(Context context){
        this.context=context;
    }

    public void setDatas(List<ConfOver> datas){
        this.datas=datas;
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView= LayoutInflater.from(context).inflate(R.layout.item_conf_record_rv,parent,false);
        ViewHolder holder=new ViewHolder(rootView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvSerialdNumber.setText(position+1+"");
        holder.tvConfTitle.setText(datas.get(position).getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClick(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemLongClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_serial_number)
        TextView tvSerialdNumber;
        @BindView(R.id.tv_conf_title)
        TextView tvConfTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
