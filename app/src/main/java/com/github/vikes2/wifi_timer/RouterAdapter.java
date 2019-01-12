package com.github.vikes2.wifi_timer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.TextClassification;
import android.widget.TextView;

import java.util.ArrayList;

public class RouterAdapter extends RecyclerView.Adapter<RouterAdapter.RouterViewHolder> {

    private ArrayList<Router> mRouterList;
    public RouterAdapter(ArrayList<Router> routerList){ mRouterList = routerList;}

    @NonNull
    @Override
    public RouterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.router_item, viewGroup, false);
        RouterViewHolder rvh = new RouterViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RouterViewHolder holder, int position) {
        Router currentItem = mRouterList.get(position);

        holder.mNameView.setText(currentItem.getName());
        holder.mMacView.setText(currentItem.getMac());
    }

    @Override
    public int getItemCount() {
        return mRouterList.size();
    }

    public static class RouterViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameView;
        public TextView mMacView;

        public RouterViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.nameTV);
            mMacView = itemView.findViewById(R.id.macTV);
        }
    }
}
