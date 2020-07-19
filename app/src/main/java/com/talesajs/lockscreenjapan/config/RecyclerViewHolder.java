package com.talesajs.lockscreenjapan.config;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.LevelData;
import com.talesajs.lockscreenjapan.util.Logg;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private View itemView;
    @BindView(R.id.textview_level_name)
    TextView tvLevelName;
    @BindView(R.id.checkbox_level)
    CheckBox cbLevel;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemView = itemView;
        mContext = itemView.getContext();
    }

    public void bind(LevelData data) {
        tvLevelName.setText(data.getName());
        cbLevel.setChecked(data.isUsed());
        cbLevel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Logg.d(data.getName() + " onclicked");
                data.setUsed(isChecked);
                cbLevel.setChecked(isChecked);

                Set<String> selectedLevels = new HashSet<>(ConfigPreference.getInstance(mContext).getConfigSelectedLevels());
                if (isChecked) { // remove
                    Logg.d("add selected level");
                    selectedLevels.add(data.getName());
                } else { // add
                    Logg.d("add remove level");
                    selectedLevels.remove(data.getName());
                }
                ConfigPreference.getInstance(mContext).setConfigSelectedLevels(selectedLevels);
                Logg.d(" set new selected levels : " + ConfigPreference.getInstance(mContext).getConfigSelectedLevels());
            }
        });
        itemView.setOnClickListener((view)->{
            Logg.d("clicked");
            cbLevel.performClick();
        });
    }
}
