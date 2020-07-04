package com.talesajs.lockscreenjapan.util;

import android.content.Context;
import android.os.PowerManager;
import android.view.View;

public class Util {
    public static boolean isScreenOn(Context context){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        Logg.d(" isScreenOn : " + isScreenOn);
        return isScreenOn;
    }

    public static boolean isNullOrEmpty(String text){
        return (text==null) || text.isEmpty();
    }

    public static int getVisibleBooler(boolean visible){
        if(visible)
            return View.VISIBLE;
        else
            return View.GONE;
    }
}
