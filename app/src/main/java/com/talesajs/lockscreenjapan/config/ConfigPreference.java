package com.talesajs.lockscreenjapan.config;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class ConfigPreference {
    private static final String PREF_NAME = "pref_config"; // real File Name

    private static final String KEY_STATE_LOCK_SCREEN = "key_state_lock_screen";

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

    public void setStateLockScreen(boolean flag){
        mEditor.putBoolean(KEY_STATE_LOCK_SCREEN, flag);
        mEditor.commit();
    }

    public boolean getLockScreen(){
        return mSharedPreferences.getBoolean(KEY_STATE_LOCK_SCREEN, false);
    }
}
