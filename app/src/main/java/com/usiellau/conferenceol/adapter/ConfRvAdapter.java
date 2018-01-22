package com.usiellau.conferenceol.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.usiellau.conferenceol.R;

/**
 * Created by UsielLau on 2018/1/22 0022 8:12.
 */

public class ConfRvAdapter extends RecyclerView.Adapter<ConfRvAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    private Context context;

    public ConfRvAdapter(Context context){
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_conf_list,parent);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
