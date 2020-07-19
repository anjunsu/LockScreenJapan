package com.talesajs.lockscreenjapan.config;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.LevelData;

import java.util.ArrayList;

public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<LevelData> mLevelList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
        return new RecyclerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.bind(mLevelList.get(position));
    }

    @Override
    public int getItemCount() {
        return mLevelList.size();
    }

    public void addItem(LevelData item){
        ArrayList<LevelData> newList = new ArrayList<>(mLevelList);
        newList.add(item);
        update(newList);
    }
    public void addItem(ArrayList<LevelData> items){
        ArrayList<LevelData> newList = new ArrayList<>(mLevelList);
        newList.addAll(items);
        update(newList);
    }

    public void updateItem(ArrayList<LevelData> items){
        update(items);
    }

    private void update(ArrayList<LevelData> newList) {
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mLevelList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mLevelList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mLevelList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        });

        this.mLevelList.clear();
        this.mLevelList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

}
