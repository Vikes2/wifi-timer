package com.github.lukaszmalyszko.wifi_timer;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RouterAdapter extends RecyclerView.Adapter<RouterAdapter.RouterViewHolder> {

    // listy potrzebne pozniej
    private ArrayList<Router> mRouterList;
    public RouterAdapter(ArrayList<Router> routerList){ mRouterList = routerList;}
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public RouterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.router_item, viewGroup, false);
        RouterViewHolder rvh = new RouterViewHolder(v, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull RouterViewHolder holder, int position) {
        Router currentItem = mRouterList.get(position);

        holder.mNameView.setText(currentItem.getName());
        holder.mNetworkId.setText(currentItem.getNetworkId());
    }

    @Override
    public int getItemCount() {
        return mRouterList.size();
    }

    public static class RouterViewHolder extends RecyclerView.ViewHolder {
        public TextView mNameView;
        public TextView mNetworkId;
        public Button mEditButton;
        public Button mDeleteButton;

        public RouterViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.nameTV);
            mNetworkId = itemView.findViewById(R.id.networkIdTV);
            mEditButton = itemView.findViewById(R.id.editBTN);
            mDeleteButton = itemView.findViewById(R.id.deleteBTN);

            mEditButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onEditClick(position);
                        }
                    }
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
