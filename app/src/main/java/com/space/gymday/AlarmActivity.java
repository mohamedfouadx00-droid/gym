package com.space.gymday;

import android.app.Activity;
import android.app.NotificationManager;
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
import android.widget.Toast;

public class AlarmActivity extends Activity {
    private Ringtone ringtone;
    private Vibrator vibrator;
    private int alarmId;
    private String title, body, type;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        alarmId = getIntent().getIntExtra("id", 1);
        title = getIntent().getStringExtra("title"); body = getIntent().getStringExtra("body"); type = getIntent().getStringExtra("type");

        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER);
        root.setPadding(dp(28), dp(48), dp(28), dp(48)); root.setBackgroundColor(0xFF07100B);

        TextView brand = txt("GYM", 18, true); brand.setTextColor(0xFF8CFF4F); brand.setLetterSpacing(.18f);
        TextView emoji = txt("wake".equals(type) ? "⏰" : "🏋️", 64, false); emoji.setGravity(Gravity.CENTER); emoji.setPadding(0, dp(32), 0, dp(18));
        TextView t = txt(title, 31, true); t.setGravity(Gravity.CENTER);
        TextView b = txt(body, 19, false); b.setTextColor(0xFFB7C2BB); b.setGravity(Gravity.CENTER); b.setPadding(0, dp(12), 0, dp(38));

        Button stop = button("تم — إيقاف المنبه", 0xFF8CFF4F, 0xFF07100B);
        stop.setOnClickListener(v -> finish());
        Button snooze = button("غفوة 5 دقائق", 0xFF1A2820, 0xFFFFFFFF);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(58)); sp.setMargins(0, dp(12), 0, 0);
        snooze.setLayoutParams(sp);
        snooze.setOnClickListener(v -> {
            AlarmScheduler.scheduleSnooze(this, alarmId, 5, title, body, type);
            Toast.makeText(this, "هنفكرك كمان 5 دقائق", Toast.LENGTH_SHORT).show();
            finish();
        });

        root.addView(brand); root.addView(emoji); root.addView(t); root.addView(b); root.addView(stop); root.addView(snooze);
        setContentView(root); startAlarm("wake".equals(type));
    }

    private void startAlarm(boolean wake) {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (wake) audio.setStreamVolume(AudioManager.STREAM_ALARM, audio.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (uri == null) uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(this, uri);
        if (ringtone != null) {
            ringtone.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build());
            if (android.os.Build.VERSION.SDK_INT >= 28) ringtone.setLooping(true); ringtone.play();
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 700, 350, 700}, 0));
    }

    @Override protected void onDestroy() {
        if (ringtone != null && ringtone.isPlaying()) ringtone.stop();
        if (vibrator != null) vibrator.cancel();
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(alarmId);
        super.onDestroy();
    }

    private TextView txt(String s, int sp, boolean bold) { TextView v = new TextView(this); v.setText(s); v.setTextSize(sp); v.setTextColor(0xFFFFFFFF); if (bold) v.setTypeface(null, android.graphics.Typeface.BOLD); return v; }
    private Button button(String s, int bg, int fg) { Button b = new Button(this); b.setText(s); b.setTextSize(18); b.setTextColor(fg); b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(bg)); b.setAllCaps(false); b.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(62))); return b; }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density); }
}
