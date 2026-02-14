package com.xandroid.booster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFilesDir();

        boolean isRooted = requestRoot();

        if (isRooted) {
            startMonitorService();
            Toast.makeText(this, "✅ xBooster: Root Granted & Active", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "❌ xBooster: Root Denied!", Toast.LENGTH_LONG).show();
        }

        finish();
    }

    private void startMonitorService() {
        Intent serviceIntent = new Intent(this, MonitorService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private boolean requestRoot() {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("su -c id");
            int exitCode = p.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (p != null) p.destroy();
        }
    }
}