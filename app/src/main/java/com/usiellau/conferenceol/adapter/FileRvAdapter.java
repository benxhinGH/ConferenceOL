package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.usiellau.conferenceol.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by UsielLau on 2018/3/23 0023 10:10.
 */

public class FileRvAdapter extends RecyclerView.Adapter<FileRvAdapter.ViewHolder> {

    private Context mContext;
    private List<String> datas=new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    public FileRvAdapter(Context context){
        mContext=context;
    }

    public void setDatas(List<String> datas){
        this.datas=datas;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener=listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.item_file_list,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tvFileName.setText(datas.get(position));

        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onItemClick(position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.onItemLongClick(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void removeData(int position){
        datas.remove(position);
        notifyItemRemoved(position);
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_file_name)
        TextView tvFileName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


    }
}
