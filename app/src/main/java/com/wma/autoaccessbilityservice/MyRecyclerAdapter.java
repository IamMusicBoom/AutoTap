package com.wma.autoaccessbilityservice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by 王明骜 on 19-11-4 下午3:21.
 */
public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {

    private List<String> lists;

    public MyRecyclerAdapter(List<String> lists) {
        this.lists = lists;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mPageTv.setText("第" + (position+1) + "页点击：");
        holder.mContentTv.setText(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mPageTv,mContentTv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mPageTv = itemView.findViewById(R.id.tv_page);
            mContentTv = itemView.findViewById(R.id.tv_content);
        }
    }
}
