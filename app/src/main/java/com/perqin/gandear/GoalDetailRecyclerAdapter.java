package com.perqin.gandear;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Author   : perqin
 * Date     : 17-4-6
 */

public class GoalDetailRecyclerAdapter extends RecyclerView.Adapter<GoalDetailRecyclerAdapter.ViewHolder> {
    private ArrayList<Dungeon> mDataSet = new ArrayList<>();
    private String mShishenId;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_goal_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Dungeon dungeon = mDataSet.get(position);
        holder.nameText.setText(dungeon.getName());
        holder.sushiText.setText(String.valueOf(dungeon.getSushi()));
        holder.moneyText.setText(String.valueOf(dungeon.getMoney()));
        holder.expText.setText(String.valueOf(dungeon.getExp()));
        int count = 0;
        for (Dungeon.Round round : dungeon.getRounds()) {
            if (round.getEnemies().containsKey(mShishenId)) {
                count += round.getEnemies().get(mShishenId);
            }
        }
        holder.countText.setText(String.valueOf(count));
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public void reloadPresence(String id, ArrayList<Dungeon> shishenPresences) {
        mShishenId = id;
        mDataSet.clear();
        mDataSet.addAll(shishenPresences);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView countText;
        TextView sushiText;
        TextView moneyText;
        TextView expText;

        ViewHolder(View itemView) {
            super(itemView);

            nameText = (TextView) itemView.findViewById(R.id.name_text);
            countText = (TextView) itemView.findViewById(R.id.count_text);
            sushiText = (TextView) itemView.findViewById(R.id.sushi_text);
            moneyText = (TextView) itemView.findViewById(R.id.money_text);
            expText = (TextView) itemView.findViewById(R.id.exp_text);
        }
    }
}
