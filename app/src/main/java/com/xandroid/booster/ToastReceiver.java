package com.xandroid.booster;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ToastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.xandroid.booster.NOTIFY".equals(intent.getAction())) {
            String pesan = intent.getStringExtra("message");
            if (pesan == null) pesan = "xBooster Notification";
            Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
        }
    }
}