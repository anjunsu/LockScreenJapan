package com.example.lockscreenjapan.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lockscreenjapan.util.Util;

public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            if(!Util.isScreenOn(context)){
                Intent i = new Intent(context, LockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        }
    }
}
