package com.talesajs.lockscreenjapan.lockscreen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.talesajs.lockscreenjapan.R;
import com.talesajs.lockscreenjapan.util.Logg;

public class LockScreenService extends Service {
    private LockScreenReceiver mReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logg.d("register Receiver1");
        mReceiver = LockScreenReceiver.getInstance();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logg.d("LockScreenService Start");
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            if (mReceiver == null && intent.getAction() == null) {
                Logg.d("register Receiver2");
                mReceiver = LockScreenReceiver.getInstance();
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                registerReceiver(mReceiver, intentFilter);
            }
        }

        // IMPORTANCE_MIN and PRIORITY_MIN -> single line notification
        String CHANNEL_ID = "channel_1";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "LockScreenJapan", NotificationManager.IMPORTANCE_MIN);

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_hiragana_big);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setShowWhen(false);

        // start ScreenService to Foreground service
        startForeground(1, builder.build());


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }
}
