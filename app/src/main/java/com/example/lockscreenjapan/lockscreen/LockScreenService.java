package com.example.lockscreenjapan.lockscreen;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.lockscreenjapan.R;

public class LockScreenService extends Service {
    private LockScreenReceiver mReceiver = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new LockScreenReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent !=null){
            if(intent.getAction() == null){
                mReceiver = new LockScreenReceiver();
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
                registerReceiver(mReceiver, intentFilter);
            }
        }

        // IMPORTANCE_MIN and PRIORITY_MIN -> single line notification
        String CHANNEL_ID = "channel_1";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"scree test", NotificationManager.IMPORTANCE_MIN);

        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_hiragana);
        builder.setPriority(NotificationCompat.PRIORITY_MIN);
        builder.setShowWhen(false);

        // start ScreenService to Foreground service
        startForeground(1,builder.build());


        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);
    }
}
