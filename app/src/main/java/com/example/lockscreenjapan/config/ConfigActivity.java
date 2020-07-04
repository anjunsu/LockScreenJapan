package com.example.lockscreenjapan.config;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.lockscreenjapan.R;
import com.example.lockscreenjapan.util.Logg;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ConfigActivity extends Activity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 102;

    Context mContext;
    @BindView(R.id.textview_config_lock_screen)
    TextView tvLockScreen;
    @BindView(R.id.switch_lock_screen)
    Switch swLockScreen;
    @BindView(R.id.button_overlay_permission)
    Button btn_OverlayPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ButterKnife.bind(this);
        mContext = this;

        checkOverlayPermission();
        swLockScreen.setChecked(ConfigPreference.getInstance(mContext).getLockScreen());
    }

    /////////////////// overlay permission ///////////////////
    private void requestOverlayPermission() {
        if (!checkOverlayPermission()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private boolean checkOverlayPermission() {
        boolean result = true;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            if (!Settings.canDrawOverlays(this)) {
                result = false;
            }
        }
        if (result) { // overlay permission granted
            btn_OverlayPermission.setEnabled(false);
            btn_OverlayPermission.setText(R.string.allowed);
        } else {
            btn_OverlayPermission.setEnabled(true);
            btn_OverlayPermission.setText(R.string.request);
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            checkOverlayPermission();
        }
    }

    @OnClick(R.id.button_overlay_permission)
    public void onClickOverlayPermission(View view) {
        requestOverlayPermission();
    }
    /////////////////// overlay permission ///////////////////



    /////////////////// lock screen on ///////////////////
    @OnCheckedChanged(R.id.switch_lock_screen)
    public void onClickLockScreenSwitch(CompoundButton compoundButton, boolean checked) {
        if (checked) {
            tvLockScreen.setText(R.string.lock_screen_on);
        } else {
            tvLockScreen.setText(R.string.lock_screen_off);
        }
        ConfigPreference.getInstance(mContext).setStateLockScreen(checked);
    }
    /////////////////// lock screen on ///////////////////


    @Override
    protected void onResume() {
        super.onResume();

        checkOverlayPermission(); // recheck overlay permission
    }
}
