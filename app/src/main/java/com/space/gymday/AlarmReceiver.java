package com.space.gymday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", 1);
        int hour = intent.getIntExtra("hour", 8);
        int minute = intent.getIntExtra("minute", 0);
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        String type = intent.getStringExtra("type");

        Intent alarm = new Intent(context, AlarmActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("title", title)
                .putExtra("body", body)
                .putExtra("type", type);
        context.startActivity(alarm);

        AlarmScheduler.schedule(context, id, hour, minute, title, body, type);
    }
}
