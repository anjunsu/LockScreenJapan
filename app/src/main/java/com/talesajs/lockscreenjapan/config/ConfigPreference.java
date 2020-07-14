package com.talesajs.lockscreenjapan.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class ConfigPreference {
    private static final String PREF_NAME = "pref_config"; // real File Name

    private static final String KEY_CONFIG_LOCK_SCREEN = "key_config_lock_screen";
    private static final String KEY_CONFIG_MEANING = "key_config_meaning";
    private static final String KEY_CONFIG_WORD = "key_config_word";
    private static final String KEY_CONFIG_ALL_LEVELS = "key_config_all_levels";
    private static final String KEY_CONFIG_SELECTED_LEVELS = "key_config_selected_levels";

    private static Context mContext;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private static ConfigPreference mInstance;

    public synchronized static ConfigPreference getInstance(Context ctx) {
        mContext = ctx;

        if(mContext == null) return null; // Aplication finish

        if (mInstance == null) {
            mInstance = new ConfigPreference();

            mSharedPreferences = ctx.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
            mEditor = mSharedPreferences.edit();
        }

        return mInstance;
    }

    public void setConfigLockScreen(boolean flag){
        mEditor.putBoolean(KEY_CONFIG_LOCK_SCREEN, flag);
        mEditor.commit();
    }
    public boolean getConfigLockScreen(){
        return mSharedPreferences.getBoolean(KEY_CONFIG_LOCK_SCREEN, false);
    }

    public void setConfigMeaning(boolean flag){
        mEditor.putBoolean(KEY_CONFIG_MEANING, flag);
        mEditor.commit();
    }
    public boolean getConfigMeaning(){
        return mSharedPreferences.getBoolean(KEY_CONFIG_MEANING, true);
    }

    public void setConfigWord(boolean flag){
        mEditor.putBoolean(KEY_CONFIG_WORD, flag);
        mEditor.commit();
    }
    public boolean getConfigWord(){
        return mSharedPreferences.getBoolean(KEY_CONFIG_WORD, false);
    }

    public void setConfigAllLevels(Set<String> allLevels){
        mEditor.putStringSet(KEY_CONFIG_ALL_LEVELS,allLevels);
        mEditor.commit();
    }
    public Set<String> getConfigAllLevels(){
        return mSharedPreferences.getStringSet(KEY_CONFIG_ALL_LEVELS, null);
    }

    public void setConfigSelectedLevels(Set<String> selectedLevels){
        mEditor.putStringSet(KEY_CONFIG_SELECTED_LEVELS,selectedLevels);
        mEditor.commit();
    }
    public Set<String> getConfigSelectedLevels(){
        return mSharedPreferences.getStringSet(KEY_CONFIG_SELECTED_LEVELS, null);
    }
}
