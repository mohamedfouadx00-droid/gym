package com.space.gymday;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AlarmActivity extends Activity {
    private Ringtone ringtone;
    private Vibrator vibrator;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        String title = getIntent().getStringExtra("title");
        String body = getIntent().getStringExtra("body");
        String type = getIntent().getStringExtra("type");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(40), dp(28), dp(40));
        root.setBackgroundColor(getColor("wake".equals(type) ? com.space.gymday.R.color.danger : com.space.gymday.R.color.primary_dark));

        TextView emoji = new TextView(this); emoji.setText("wake".equals(type) ? "⏰" : "🔔"); emoji.setTextSize(58); emoji.setGravity(Gravity.CENTER);
        TextView t = new TextView(this); t.setText(title); t.setTextSize(30); t.setTextColor(0xFFFFFFFF); t.setGravity(Gravity.CENTER); t.setPadding(0, dp(20), 0, dp(12));
        TextView b = new TextView(this); b.setText(body); b.setTextSize(20); b.setTextColor(0xFFFFFFFF); b.setGravity(Gravity.CENTER); b.setPadding(0, 0, 0, dp(36));
        Button stop = new Button(this); stop.setText("إيقاف المنبه"); stop.setTextSize(20); stop.setMinHeight(dp(64)); stop.setOnClickListener(v -> finish());

        root.addView(emoji); root.addView(t); root.addView(b); root.addView(stop, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        setContentView(root);
        startAlarm("wake".equals(type));
    }

    private void startAlarm(boolean wake) {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        int max = audio.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        if (wake) audio.setStreamVolume(AudioManager.STREAM_ALARM, max, 0);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (uri == null) uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, uri);
        if (ringtone != null) {
            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            if (android.os.Build.VERSION.SDK_INT >= 28) ringtone.setLooping(true);
            ringtone.play();
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 700, 350, 700}, 0));
    }

    @Override protected void onDestroy() {
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();
        super.onDestroy();
    }

    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }
}
