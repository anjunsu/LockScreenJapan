package com.talesajs.lockscreenjapan.config;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.talesajs.lockscreenjapan.data.WordData;
import com.talesajs.lockscreenjapan.lockscreen.LockScreenService;
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
    private static final String fileName = "jlpt.xls";

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
    @BindView(R.id.button_overlay_permission)
    Button btnOverlayPermission;

    @BindView(R.id.recyclerview_levels)
    RecyclerView rvLevels;
    RecyclerViewListAdapter recyclerViewListAdapter;
    Set<String> selectedLevels;
    Set<String> allLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mContext = this;


        checkOverlayPermission();

        swLockScreen.setChecked(ConfigPreference.getInstance(mContext).getConfigLockScreen());
        swShowMeaning.setChecked(ConfigPreference.getInstance(mContext).getConfigMeaning());
        swShowMode.setChecked(ConfigPreference.getInstance(mContext).getConfigWord());

        allLevels = ConfigPreference.getInstance(mContext).getConfigAllLevels();

        selectedLevels = ConfigPreference.getInstance(mContext).getConfigSelectedLevels();

        Logg.d(" all levels : " + allLevels);
        Logg.d(" selected levels : " + selectedLevels);
        ArrayList<LevelData> levels = new ArrayList<>();
        for(String level : new ArrayList<>(allLevels)){
            levels.add(new LevelData(level, selectedLevels.contains(level)));
        }
        recyclerViewListAdapter = new RecyclerViewListAdapter();
        rvLevels.setAdapter(recyclerViewListAdapter);
        recyclerViewListAdapter.addItem(levels);
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
    }

    @OnClick(R.id.button_overlay_permission)
    public void onClickOverlayPermission(View view) {
        requestOverlayPermission();
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
                Intent serviceIntent = new Intent(mContext, LockScreenService.class);
                if (checked) {
                    textId = R.string.config_menu_lock_screen_on;
                    startService(serviceIntent);
                    Toast.makeText(mContext, R.string.toast_lock_screen_on, Toast.LENGTH_SHORT).show();

                } else {
                    textId = R.string.config_menu_lock_screen_off;
                    stopService(serviceIntent);
                    Toast.makeText(mContext, R.string.toast_lock_screen_off, Toast.LENGTH_SHORT).show();

                }
                ConfigPreference.getInstance(mContext).setConfigLockScreen(checked);

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
        Excel excel = new Excel(mContext);
        DBHandler dbHandler = DBHandler.open(mContext);
        for (WordData data : excel.getWordData(fileName)) {
            dbHandler.insertWord(data.getIndex(), data.getLevel(), data.getWord(), data.getKanji(), data.getMeaning());
        }
        dbHandler.close();

        allLevels = new HashSet<>();
        selectedLevels = new HashSet<>();

        ArrayList<LevelData> levelData = new ArrayList<>();
        for (String level : excel.getLevels(fileName)) {
            levelData.add(new LevelData(level, false));
            allLevels.add(level);
        }
        recyclerViewListAdapter.updateItem(levelData);
        ConfigPreference.getInstance(mContext).setConfigAllLevels(allLevels);
        ConfigPreference.getInstance(mContext).setConfigSelectedLevels(selectedLevels);
    }

    @BindView(R.id.button_test)
    Button buttonTest;

    @OnClick(R.id.button_test)
    public void onClickTest(View view) {
        DBHandler dbHandler = DBHandler.open(mContext);
        int count = dbHandler.getWordCount();
        buttonTest.setText("count : " + count);
        dbHandler.close();
    }

    @OnClick(R.id.button_delete)
    public void onClickDelete(View view) {
        DBHandler dbHandler = DBHandler.open(mContext);
        dbHandler.deleteWord();
        dbHandler.close();
    }

    /////////////////// word update ///////////////////


    @Override
    protected void onResume() {
        super.onResume();
        checkOverlayPermission(); // recheck overlay permission
    }
}
