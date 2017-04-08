package com.perqin.gandear;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author   : perqin
 * Date     : 17-4-6
 */

public class GoalDetailRecyclerAdapter extends RecyclerView.Adapter<GoalDetailRecyclerAdapter.ViewHolder> {
    private boolean mType;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO: Implement onCreateViewHolder
        throw new RuntimeException("Method not implemented: onCreateViewHolder");
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // TODO: Implement onBindViewHolder
        throw new RuntimeException("Method not implemented: onBindViewHolder");
    }

    @Override
    public int getItemCount() {
        // TODO: Implement getItemCount
        throw new RuntimeException("Method not implemented: getItemCount");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class GoalDetailViewHolder extends ViewHolder {
        public GoalDetailViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ShishenSelectionViewHolder extends ViewHolder {
        public ShishenSelectionViewHolder(View itemView) {
            super(itemView);
        }
    }
}
