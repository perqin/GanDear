package com.perqin.gandear.floatingwindow.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.perqin.gandear.R;
import com.perqin.gandear.data.models.Shishen;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   : perqin
 * Date     : 17-4-6
 */

public class ShishensRecyclerAdapter extends RecyclerView.Adapter<ShishensRecyclerAdapter.ViewHolder> {
    private static final String TAG = "ShishensRecyclerAdapter";
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
        Log.d(TAG, "onBindViewHolder: Shishen: " + shishen.getName());
        holder.cluesText.setText(joinClues(shishen.getClues()));
        Glide.with(holder.itemView.getContext()).load("https://gandear.perqin.com/img/" + mDataSet.get(position).getId() + "-square.jpg").into(holder.avatarImage);
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

    private String joinClues(List<String> clues) {
        if (clues == null) {
            Log.w(TAG, "joinClues: Clues is null!");
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String clue : clues) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(clue);
        }
        return sb.toString();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatarImage;
        TextView nameText;
        TextView cluesText;

        ViewHolder(View itemView) {
            super(itemView);
            avatarImage = (ImageView) itemView.findViewById(R.id.avatar_image);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            cluesText = (TextView) itemView.findViewById(R.id.clues_text);
        }
    }

    public interface OnItemClickListener {
        void onShishenItemClick(Shishen shishen);
    }
}
