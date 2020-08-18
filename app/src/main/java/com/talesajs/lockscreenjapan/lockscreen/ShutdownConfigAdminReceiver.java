package com.talesajs.lockscreenjapan.lockscreen;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ShutdownConfigAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "관리자 권한을 받아오지 못했습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "관리자 권한을 받았습니다.", Toast.LENGTH_SHORT).show();
    }
}