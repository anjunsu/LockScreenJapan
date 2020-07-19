package com.talesajs.lockscreenjapan.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class ConfigPreference {
    private static final String PREF_NAME = "pref_config"; // real File Name

    private static final String KEY_CONFIG_LOCK_SCREEN = "key_config_lock_screen";
    private static final String KEY_CONFIG_MEANING = "key_config_meaning";
    private static final String KEY_CONFIG_WORD = "key_config_word";
    private static final String KEY_CONFIG_ALL_LEVELS = "key_config_all_levels";
    private static final String KEY_CONFIG_SELECTED_LEVELS = "key_config_selected_levels";
    private static final String KEY_LOCKSCREEN_SPEAKER_POSITION_X = "key_lockscreen_speaker_position_x";
    private static final String KEY_LOCKSCREEN_SPEAKER_POSITION_Y  = "key_lockscreen_speaker_position_y";

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static ConfigPreference mInstance;

    public synchronized static ConfigPreference getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ConfigPreference(context);
        }
        return mInstance;
    }
    private ConfigPreference(Context context){
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
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
        return mSharedPreferences.getStringSet(KEY_CONFIG_ALL_LEVELS, new HashSet<>());
    }

    public void setConfigSelectedLevels(Set<String> selectedLevels){
        mEditor.putStringSet(KEY_CONFIG_SELECTED_LEVELS,selectedLevels);
        mEditor.commit();
    }
    public Set<String> getConfigSelectedLevels(){
        return mSharedPreferences.getStringSet(KEY_CONFIG_SELECTED_LEVELS, new HashSet<>());
    }

    public void setSpeakerIconPositionX(float position){
        mEditor.putFloat(KEY_LOCKSCREEN_SPEAKER_POSITION_X, position);
        mEditor.commit();
    }
    public float getSpeakerIconPositionX(){
        return mSharedPreferences.getFloat(KEY_LOCKSCREEN_SPEAKER_POSITION_X, -1);
    }

    public void setSpeakerIconPositionY(float position){
        mEditor.putFloat(KEY_LOCKSCREEN_SPEAKER_POSITION_Y, position);
        mEditor.commit();
    }
    public float getSpeakerIconPositionY(){
        return mSharedPreferences.getFloat(KEY_LOCKSCREEN_SPEAKER_POSITION_Y, -1);
    }
}
