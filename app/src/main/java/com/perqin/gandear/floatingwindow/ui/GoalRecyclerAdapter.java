package com.perqin.gandear.floatingwindow.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.perqin.gandear.R;
import com.perqin.gandear.data.models.Shishen;

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
            Context context = holder.itemView.getContext();
//            holder.imageButton.setImageResource(R.mipmap.ic_launcher);
            Glide.with(context).load("https://gandear.perqin.com/img/" + mShishensList.get(position).getId() + "-square.jpg").into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    holder.imageButton.setImageDrawable(resource);
                }
            });
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
