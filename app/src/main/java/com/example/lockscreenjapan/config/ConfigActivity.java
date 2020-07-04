package com.example.lockscreenjapan.config;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.lockscreenjapan.R;
import com.example.lockscreenjapan.util.Logg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class ConfigActivity extends AppCompatActivity {

    Context mContext;
    @BindView(R.id.switch_lock_screen)
    Switch swLockScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        mContext = this;


        swLockScreen.setChecked(ConfigPreference.getInstance(mContext).getLockScreen());
    }

    @OnCheckedChanged(R.id.switch_lock_screen)
    public void onClickLockScreenSwitch(CompoundButton compoundButton, boolean checked){
        Logg.d("button checked : " + checked);
        ConfigPreference.getInstance(mContext).setStateLockScreen(checked);
    }
}
