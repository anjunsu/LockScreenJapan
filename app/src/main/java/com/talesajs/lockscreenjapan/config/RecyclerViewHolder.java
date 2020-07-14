package com.talesajs.lockscreenjapan.config;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.LevelData;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private View itemView;
    @BindView(R.id.textview_level_name)
    TextView tvLevelName;
    @BindView(R.id.checkbox_level)
    CheckBox cbLevel;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
    }

    public void bind(LevelData data) {
        tvLevelName.setText(data.getName());
        cbLevel.setChecked(data.isUsed());
        itemView.setOnClickListener(view -> {
            cbLevel.setChecked(!cbLevel.isChecked());
            //save cbLevel.isChecked;
        });
    }
}
