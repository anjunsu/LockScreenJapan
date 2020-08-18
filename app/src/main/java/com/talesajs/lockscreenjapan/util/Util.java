package com.talesajs.lockscreenjapan.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;
import android.view.View;

import com.talesajs.lockscreenjapan.lockscreen.ShutdownConfigAdminReceiver;

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

    public static boolean hasDeviceAdmin(Context context){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, ShutdownConfigAdminReceiver.class);

        return devicePolicyManager.isAdminActive(componentName);
    }
    public static void lockNow(Context context){
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, ShutdownConfigAdminReceiver.class);

        if(devicePolicyManager.isAdminActive(componentName))
            devicePolicyManager.lockNow();
    }
}
