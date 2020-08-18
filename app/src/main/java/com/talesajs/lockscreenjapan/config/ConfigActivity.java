package com.talesajs.lockscreenjapan.config;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.DBHandler;
import com.talesajs.lockscreenjapan.data.Excel;
import com.talesajs.lockscreenjapan.data.LevelData;
import com.talesajs.lockscreenjapan.dialog.DialogLoading;
import com.talesajs.lockscreenjapan.dialog.OneButtonDialog;
import com.talesajs.lockscreenjapan.lockscreen.LockScreenService;
import com.talesajs.lockscreenjapan.lockscreen.ShutdownConfigAdminReceiver;
import com.talesajs.lockscreenjapan.util.Logg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class ConfigActivity extends AppCompatActivity {

    private static final int OVERLAY_PERMISSION_REQUEST_CODE = 102;
    private static final int DEVICE_ADMIN_REQUEST_CODE = 103;
    private static final String fileName = "jlpt.xls";

    public static final float DEFAULT_X = 1200;
    public static final float DEFAULT_Y = 2100;

    Context mContext;
    @BindView(R.id.textview_config_lock_screen)
    TextView tvLockScreen;
    @BindView(R.id.switch_lock_screen)
    Switch swLockScreen;

    @BindView(R.id.textview_config_show_meaning)
    TextView tvConfigShowMeaning;
    @BindView(R.id.switch_show_meaning)
    Switch swShowMeaning;

    @BindView(R.id.textview_config_show_mode)
    TextView tvConfigShowMode;
    @BindView(R.id.switch_show_mode)
    Switch swShowMode;
    @BindView(R.id.button_screen_off_permission)
    Button btnScreenOffPermission;
    @BindView(R.id.button_overlay_permission)
    Button btnOverlayPermission;
    @BindView(R.id.button_reset)
    Button btnReset;

    @BindView(R.id.recyclerview_levels)
    RecyclerView rvLevels;
    RecyclerViewListAdapter recyclerViewListAdapter;
    Set<String> selectedLevels;
    Set<String> allLevels;

    private boolean isTest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mContext = this;

        checkDeviceAdmin();

        if (!checkOverlayPermission()) { // has no overlay permission
            OneButtonDialog dialog = new OneButtonDialog(mContext, R.style.DialogStyle);
            dialog.setTitle(R.string.dialog_overlay_request_title);
            dialog.setOneButtonDialogCallBack(() -> {
                requestOverlayPermission();
                dialog.dismiss();
            });
            dialog.show();
        }

        swLockScreen.setChecked(ConfigPreference.getInstance(mContext).getConfigLockScreen());
        swShowMeaning.setChecked(ConfigPreference.getInstance(mContext).getConfigMeaning());
        swShowMode.setChecked(ConfigPreference.getInstance(mContext).getConfigWord());
        if (ConfigPreference.getInstance(mContext).getSpeakerIconPositionX() == DEFAULT_X
                && ConfigPreference.getInstance(mContext).getSpeakerIconPositionY() == DEFAULT_Y)
            btnReset.setEnabled(false);

        allLevels = ConfigPreference.getInstance(mContext).getConfigAllLevels();

        selectedLevels = ConfigPreference.getInstance(mContext).getConfigSelectedLevels();

        Logg.d(" all levels : " + allLevels);
        Logg.d(" selected levels : " + selectedLevels);
        ArrayList<LevelData> levels = new ArrayList<>();
        for (String level : new ArrayList<>(allLevels)) {
            levels.add(new LevelData(level, selectedLevels.contains(level)));
        }
        recyclerViewListAdapter = new RecyclerViewListAdapter();
        rvLevels.setAdapter(recyclerViewListAdapter);
        recyclerViewListAdapter.addItem(levels);

        if (isTest) {
            findViewById(R.id.button_test).setVisibility(View.VISIBLE);
            findViewById(R.id.button_delete).setVisibility(View.VISIBLE);
        }
    }

    /////////////////// device policy manage ///////////////////
    private boolean checkDeviceAdmin() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(), ShutdownConfigAdminReceiver.class);

        boolean result = devicePolicyManager.isAdminActive(componentName);
        if (result) {
            btnScreenOffPermission.setEnabled(false);
            btnScreenOffPermission.setText(R.string.allowed);
        } else {
            btnScreenOffPermission.setEnabled(true);
            btnScreenOffPermission.setText(R.string.request);
        }
        return result;
    }

    private void requestDevicePolicy() {
        ComponentName componentName = new ComponentName(getApplicationContext(), ShutdownConfigAdminReceiver.class);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        startActivityForResult(intent, DEVICE_ADMIN_REQUEST_CODE);
    }

    /////////////////// device policy manage ///////////////////


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
            btnOverlayPermission.setEnabled(false);
            btnOverlayPermission.setText(R.string.allowed);
        } else {
            btnOverlayPermission.setEnabled(true);
            btnOverlayPermission.setText(R.string.request);
        }
        return result;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            checkOverlayPermission();
        }
        else if (resultCode == DEVICE_ADMIN_REQUEST_CODE){
            checkDeviceAdmin();
        }
    }

    @OnClick({R.id.button_screen_off_permission, R.id.button_overlay_permission})
    public void onClickOverlayPermission(View view) {
        switch (view.getId()){
            case R.id.button_screen_off_permission:{
                requestDevicePolicy();
                break;
            }
            case R.id.button_overlay_permission:{
                requestOverlayPermission();
                break;
            }
        }
    }
    /////////////////// overlay permission ///////////////////


    /////////////////// lock screen on ///////////////////
    @OnCheckedChanged({R.id.switch_lock_screen, R.id.switch_show_meaning, R.id.switch_show_mode})
    public void onClickLockScreenSwitch(CompoundButton compoundButton, boolean checked) {
        TextView textView = null;
        int textId = 0;
        switch (compoundButton.getId()) {
            case R.id.switch_lock_screen: {
                textView = tvLockScreen;

                ConfigPreference.getInstance(mContext).setConfigLockScreen(checked);

                Intent serviceIntent = new Intent(mContext, LockScreenService.class);
                stopService(serviceIntent);
                if (checked) {
                    DBHandler dbHandler = DBHandler.open(mContext);
                    int count = dbHandler.getWordCount();
                    buttonTest.setText("단어 총 개수 : " + count);
                    dbHandler.close();

                    if (count == 0) {
                        Toast.makeText(mContext, R.string.toast_no_word, Toast.LENGTH_SHORT).show();
                        swLockScreen.setChecked(false);
                    } else {
                        textId = R.string.config_menu_lock_screen_on;
                        startService(serviceIntent);
                        Toast.makeText(mContext, R.string.toast_lock_screen_on, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    textId = R.string.config_menu_lock_screen_off;
                    stopService(serviceIntent);
                    Toast.makeText(mContext, R.string.toast_lock_screen_off, Toast.LENGTH_SHORT).show();

                }

                break;
            }
            case R.id.switch_show_meaning: {
                textView = tvConfigShowMeaning;
                if (checked) {
                    textId = R.string.config_show_meaning_on;
                } else {
                    textId = R.string.config_show_meaning_off;
                }
                ConfigPreference.getInstance(mContext).setConfigMeaning(checked);
                break;
            }
            case R.id.switch_show_mode: {
                textView = tvConfigShowMode;
                if (checked) {
                    textId = R.string.config_show_kanji;
                } else {
                    textId = R.string.config_show_hiragana;
                }
                ConfigPreference.getInstance(mContext).setConfigWord(checked);
                break;
            }
        }
        if (textView != null && textId != 0)
            textView.setText(textId);
    }

    /////////////////// lock screen on ///////////////////

    /////////////////// word update ///////////////////
    @OnClick(R.id.button_word_update)
    public void onClickWordUpdate(View view) {
        Logg.d("onClickWordUpdate");
        DialogLoading dialogLoading = new DialogLoading(mContext, R.style.Transparent);
        dialogLoading.show();
        new Thread(() -> {
            Excel excel = new Excel(mContext);
            excel.updateWordData(fileName, () -> {
                Logg.d("updateWordData finish");
                allLevels = new HashSet<>();
                selectedLevels = new HashSet<>();

                ArrayList<LevelData> levelData = new ArrayList<>();
                for (String level : excel.getLevels(fileName)) {
                    levelData.add(new LevelData(level, false));
                    allLevels.add(level);
                }
                runOnUiThread(() -> {
                    recyclerViewListAdapter.updateItem(levelData);
                });
                ConfigPreference.getInstance(mContext).setConfigAllLevels(allLevels);
                ConfigPreference.getInstance(mContext).setConfigSelectedLevels(selectedLevels);

                new Handler(getMainLooper()).postDelayed(dialogLoading::dismiss, 500);
            });
        }).start();
    }

    @BindView(R.id.button_test)
    Button buttonTest;

    @OnClick(R.id.button_test)
    public void onClickTest(View view) {
        DBHandler dbHandler = DBHandler.open(mContext);
        int count = dbHandler.getWordCount();
        buttonTest.setText("단어 총 개수 : " + count);
        dbHandler.close();

        ConfigPreference.getInstance(mContext).setSpeakerIconPositionX(-1);
        ConfigPreference.getInstance(mContext).setSpeakerIconPositionY(-1);
    }

    @OnClick(R.id.button_delete)
    public void onClickDelete(View view) {
        DBHandler dbHandler = DBHandler.open(mContext);
        dbHandler.deleteWord();
        dbHandler.close();

        ConfigPreference.getInstance(mContext).setConfigAllLevels(new HashSet<>());
        ConfigPreference.getInstance(mContext).setConfigSelectedLevels(new HashSet<>());

        recyclerViewListAdapter.updateItem(new ArrayList<>());
    }

    /////////////////// word update ///////////////////

    @OnClick(R.id.button_reset)
    public void onClickReset(View view) {
        Logg.d("Button reset");
        ConfigPreference.getInstance(mContext).setSpeakerIconPositionX(DEFAULT_X);
        ConfigPreference.getInstance(mContext).setSpeakerIconPositionY(DEFAULT_Y);
        btnReset.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOverlayPermission(); // recheck overlay permission
    }
}
