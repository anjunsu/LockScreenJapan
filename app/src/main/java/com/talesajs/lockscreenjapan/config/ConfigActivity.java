package com.talesajs.lockscreenjapan.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
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

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.data.DBHandler;
import com.talesajs.lockscreenjapan.data.WordData;
import com.talesajs.lockscreenjapan.lockscreen.LockScreenService;
import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
            tvLockScreen.setText(R.string.config_menu_lock_screen_on);

            Intent serviceIntent = new Intent(mContext, LockScreenService.class);
            startService(serviceIntent);
            Toast.makeText(mContext, R.string.toast_lock_screen_on, Toast.LENGTH_SHORT).show();

        } else {
            tvLockScreen.setText(R.string.config_menu_lock_screen_off);
            Intent serviceIntent = new Intent(mContext, LockScreenService.class);
            stopService(serviceIntent);
            Toast.makeText(mContext, R.string.toast_lock_screen_off, Toast.LENGTH_SHORT).show();

        }
        ConfigPreference.getInstance(mContext).setStateLockScreen(checked);
    }

//    getRunningServices is deprecated
//
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    /////////////////// lock screen on ///////////////////

    /////////////////// word update ///////////////////
    @OnClick(R.id.button_word_update)
    public void onClickWordUpdate(View view) {
        DBHandler dbHandler = DBHandler.open(mContext);
        for(WordData data : xlsReader("jlpt.xls")){
            dbHandler.insertWord(data.getIndex(), data.getLevel(),data.getWord(),data.getKanji(),data.getMeaning());
        }
        dbHandler.close();
    }

    @BindView(R.id.button_test) Button buttonTest;
    @OnClick(R.id.button_test)
    public void onClickTest(View view){
        DBHandler dbHandler = DBHandler.open(mContext);
        int count = dbHandler.getWordCount();
        buttonTest.setText("count : " + count);
        dbHandler.close();
    }

    @OnClick(R.id.button_delete)
    public void onClickDelete(View view){
        DBHandler dbHandler = DBHandler.open(mContext);
        dbHandler.deleteWord();
        dbHandler.close();
    }

    private ArrayList<WordData> xlsReader(String filePath) {
        Logg.d("xlsReader");
        ArrayList<WordData> wordList = new ArrayList<>();

        final int CELL_NUM = 3;

        final int CELL_WORD = 0;
        final int CELL_KANJI = 1;
        final int CELL_MEANING = 2;

        ArrayList<WordData> list = new ArrayList<>();
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        POIFSFileSystem poifsFileSystem = null;
        HSSFWorkbook workbook = null;

        try {
            inputStream = assetManager.open(filePath);
            poifsFileSystem = new POIFSFileSystem(inputStream);
            workbook = new HSSFWorkbook(poifsFileSystem);

            HSSFSheet curSheet;
            HSSFRow curRow;
            HSSFCell curCell;

            for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
                curSheet = workbook.getSheetAt(sheetIdx);
                String level = curSheet.getSheetName();
                Logg.d("curSheet : " + curSheet.getSheetName());
                for (int rowIdx = 0; rowIdx < curSheet.getPhysicalNumberOfRows(); rowIdx++) {
                    if (rowIdx != 0) {
                        curRow = curSheet.getRow(rowIdx);
                        if (!Util.isNullOrEmpty(curRow.getCell(0).getStringCellValue())) {
                            String word = "";
                            String kanji = "";
                            String meaning = "";
                            for (int cellIdx = 0; cellIdx < CELL_NUM; cellIdx++) {
                                curCell = curRow.getCell(cellIdx);
                                switch (cellIdx) {
                                    case CELL_WORD: {
                                        word = curCell.toString();
                                        break;
                                    }
                                    case CELL_KANJI: {
                                        if (curCell != null)
                                            kanji = curCell.toString();
                                        break;
                                    }
                                    case CELL_MEANING: {
                                        meaning = curCell.toString();
                                        break;
                                    }
                                }
                            }

                            WordData newWord = WordData.builder()
                                    .index(rowIdx)
                                    .level(level)
                                    .word(word)
                                    .kanji(kanji)
                                    .meaning(meaning).build();
                            wordList.add(newWord);
                            Logg.d("row : " + rowIdx + " word : " + word + " kanji : " + kanji + " meaning : " + meaning);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Logg.e(" excel file not found exception");
        } catch (IOException e) {
            Logg.e(" IOException");
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                Logg.e(" close exceptioin");
            }
        }
        return wordList;
    }


    /////////////////// word update ///////////////////


    @Override
    protected void onResume() {
        super.onResume();

        checkOverlayPermission(); // recheck overlay permission
    }
}
