package com.perqin.gandear;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Author   : perqin
 * Date     : 17-4-6
 */

public class ShishensRecyclerAdapter extends RecyclerView.Adapter<ShishensRecyclerAdapter.ViewHolder> {
    private ArrayList<Shishen> mDataSet = new ArrayList<>();
    private OnItemClickListener mListener;

    public ShishensRecyclerAdapter(ArrayList<Shishen> shishens, OnItemClickListener listener) {
        mDataSet.clear();
        mDataSet.addAll(shishens);
        mListener = listener;
    }

    public void refreshShishens(ArrayList<Shishen> shishens) {
        mDataSet.clear();
        mDataSet.addAll(shishens);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_shishen_selection, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Shishen shishen = mDataSet.get(position);
        holder.nameText.setText(shishen.getName());
        holder.cluesText.setText(shishen.getClues());
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onShishenItemClick(shishen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView avatarImage;
        public TextView nameText;
        public TextView cluesText;

        public ViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.image_button);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            cluesText = (TextView) itemView.findViewById(R.id.clues_text);
        }
    }

    public interface OnItemClickListener {
        void onShishenItemClick(Shishen shishen);
    }
}
