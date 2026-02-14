package com.xandroid.booster;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import java.io.File;

public class MonitorService extends Service {

    private static final String DAEMON_PATH = "/system/bin/autd";

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
            @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action) || Intent.ACTION_USER_PRESENT.equals(action)) {
            checkAndStartDaemon();
        }
    }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, createNotification());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(screenReceiver, filter);

        checkAndStartDaemon();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, createNotification());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(screenReceiver);
        } catch (Exception e) {}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkAndStartDaemon() {
        new Thread(new Runnable() {
				@Override
				public void run() {
					if (isDaemonRunning()) return;

					try {
						File f = new File(DAEMON_PATH);
						if (!f.exists()) return;

						String cmd =  DAEMON_PATH + " > /dev/null 2>&1 &";

						Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
    }

    private boolean isDaemonRunning() {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "pidof autd"});
            int exitCode = p.waitFor();

            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private Notification createNotification() {
        String channelId = "xBoosterService";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
				channelId, "Monitor Service", NotificationManager.IMPORTANCE_MIN);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, channelId);
        } else {
            builder = new Notification.Builder(this);
        }
        return builder
			.setContentTitle("xBooster")
			.setContentText("Monitoring...")
			.setSmallIcon(android.R.drawable.ic_popup_sync)
			.build();
    }
}