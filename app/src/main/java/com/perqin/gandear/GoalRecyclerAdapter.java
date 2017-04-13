package com.perqin.gandear;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Author   : perqin
 * Date     : 17-4-5
 */

public class GoalRecyclerAdapter extends RecyclerView.Adapter<GoalRecyclerAdapter.ViewHolder> {
    private ArrayList<Shishen> mShishensList = new ArrayList<>();
    private OnGoalClickListener mListener;

    public GoalRecyclerAdapter(List<Shishen> shishens, OnGoalClickListener listener) {
        mShishensList.clear();
        mShishensList.addAll(shishens);
        this.mListener = listener;
    }

    public boolean addShishen(Shishen shishen) {
        for (Shishen s : mShishensList) {
            if (s.getName().equals(shishen.getName())) {
                return false;
            }
        }
        mShishensList.add(shishen);
        notifyItemInserted(mShishensList.size() - 1);
        return true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_goal_button, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == mShishensList.size()) {
            holder.imageButton.setImageResource(R.drawable.add);
            holder.imageButton.setOnClickListener(v -> {
                if (mListener != null) mListener.onAdderClick();
            });
        } else {
            holder.imageButton.setImageResource(R.mipmap.ic_launcher);
            holder.imageButton.setOnClickListener(v -> {
                if (mListener != null) mListener.onGoalClick(mShishensList.get(holder.getAdapterPosition()));
            });
            holder.imageButton.setOnLongClickListener(v -> {
                if (mListener != null) mListener.onGoalLongClick(mShishensList.get(holder.getAdapterPosition()));
                return true;
            });
        }
    }

    @Override
    public int getItemCount() {
        return mShishensList.size() + 1;
    }

    public void removeShishen(Shishen shishen) {
        int i;
        for (i = mShishensList.size() - 1; i >= 0; --i) {
            if (mShishensList.get(i).getId().equals(shishen.getId())) {
                mShishensList.remove(i);
                break;
            }
        }
        if (i >= 0) {
            notifyItemRemoved(i);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageButton = (ImageButton) itemView.findViewById(R.id.image_button);
        }
    }

    public interface OnGoalClickListener {
        void onGoalClick(Shishen shishen);
        void onGoalLongClick(Shishen shishen);
        void onAdderClick();
    }
}
