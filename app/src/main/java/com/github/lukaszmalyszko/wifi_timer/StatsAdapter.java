package com.github.lukaszmalyszko.wifi_timer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatsViewHolder> {

    private ArrayList<Router> mRouterList;
    private ArrayList<Action> mActionList;
    //tablica z nazwą routera oraz obliczonym czasem
    public HashMap<String, String> mData;
    public StatsAdapter(ArrayList<Router> routerList, ArrayList<Action> actionList, HashMap<String, String> stringLongHashMap){
        mRouterList = routerList; mActionList = actionList;  mData = stringLongHashMap;}

    @NonNull
    @Override
    public StatsAdapter.StatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.stat_item, viewGroup, false);
        StatsAdapter.StatsViewHolder rvh = new StatsAdapter.StatsViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull StatsAdapter.StatsViewHolder holder, int position) {
        Router currentItem = mRouterList.get(position);
        if(mData == null){
            holder.mTimeView.setText("unknown");
        }else{
            if(mData.containsKey(currentItem.networkId)){
                holder.mTimeView.setText(mData.get(currentItem.networkId));
            }else{
                holder.mTimeView.setText("unknown2");
            }
        }
        holder.mNameView.setText(currentItem.name);
    }

    @Override
    public int getItemCount() {
        return mRouterList.size();
    }


    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameView;
        public TextView mTimeView;

        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.nameTV);
            mTimeView = itemView.findViewById(R.id.timeTV);
        }
    }
}
