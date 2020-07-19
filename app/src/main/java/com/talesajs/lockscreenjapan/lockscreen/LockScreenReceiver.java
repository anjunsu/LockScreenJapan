package com.talesajs.lockscreenjapan.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.talesajs.lockscreenjapan.util.Logg;
import com.talesajs.lockscreenjapan.util.Util;

public class LockScreenReceiver extends BroadcastReceiver {

    private static LockScreenReceiver receiver;

    private LockScreenReceiver() {
        super();
    }

    public static LockScreenReceiver getInstance() {
        if (receiver == null) {
            receiver = new LockScreenReceiver();
        }
        return receiver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == Intent.ACTION_SCREEN_OFF) {
            Logg.d("on Receive Screen Off");
            if (!Util.isScreenOn(context)) {
                Intent i = new Intent(context, LockScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(i);
            }
        }
    }
}
