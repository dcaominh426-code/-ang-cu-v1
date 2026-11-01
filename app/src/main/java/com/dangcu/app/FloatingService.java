
package com.dangcu.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Build;
import android.provider.Settings;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;
import android.widget.Toast;

public class FloatingService extends Service {
    private WindowManager wm;
    private LinearLayout panel;
    private WindowManager.LayoutParams params;
    private boolean expanded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(20, 15, 20, 15);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.rgb(25, 25, 25));
        bg.setCornerRadius(24);
        bg.setStroke(3, Color.RED);
        panel.setBackground(bg);

        TextView title = new TextView(this);
        title.setText("☰ DANG CU V2");
        title.setTextColor(Color.RED);
        title.setTextSize(19);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(10, 8, 10, 12);
        panel.addView(title);

        LinearLayout options = new LinearLayout(this);
        options.setOrientation(LinearLayout.VERTICAL);
        options.setVisibility(View.GONE);
        panel.addView(options);

        Switch aim = makeSwitch("AUTO HEAD (DEMO)");
        Switch radar = makeSwitch("ENEMY RADAR (DEMO)");
        Switch hp = makeSwitch("ENEMY HP (DEMO)");

        options.addView(aim);
        options.addView(radar);
        options.addView(hp);

        TextView info = new TextView(this);
        info.setText("Demo giao dien - khong can thiep game");
        info.setTextColor(Color.LTGRAY);
        info.setTextSize(11);
        options.addView(info);

        Button close = new Button(this);
        close.setText("DONG MENU");
        options.addView(close);

        title.setOnClickListener(v -> {
            expanded = !expanded;
            options.setVisibility(
                expanded ? View.VISIBLE : View.GONE
            );
            wm.updateViewLayout(panel, params);
        });

        close.setOnClickListener(v -> stopSelf());

        aim.setOnCheckedChangeListener((b, checked) ->
            showDemo("AUTO HEAD", checked));

        radar.setOnCheckedChangeListener((b, checked) ->
            showDemo("ENEMY RADAR", checked));

        hp.setOnCheckedChangeListener((b, checked) ->
            showDemo("ENEMY HP", checked));

        int type = Build.VERSION.SDK_INT >= 26
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 50;
        params.y = 200;

        title.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float touchX, touchY;
            private boolean moved;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = e.getRawX();
                        touchY = e.getRawY();
                        moved = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int)(e.getRawX() - touchX);
                        int dy = (int)(e.getRawY() - touchY);

                        if (Math.abs(dx) > 12 || Math.abs(dy) > 12) {
                            moved = true;
                        }

                        if (moved) {
                            params.x = startX + dx;
                            params.y = startY + dy;
                            wm.updateViewLayout(panel, params);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!moved) v.performClick();
                        return true;
                }
                return false;
            }
        });

        wm.addView(panel, params);
    }

    private Switch makeSwitch(String label) {
        Switch sw = new Switch(this);
        sw.setText(label);
        sw.setTextColor(Color.WHITE);
        sw.setTextSize(14);
        sw.setPadding(8, 12, 8, 12);
        return sw;
    }

    private void showDemo(String name, boolean enabled) {
        Toast.makeText(this,
            name + (enabled ? " ON (DEMO)" : " OFF"),
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (wm != null && panel != null) {
            wm.removeView(panel);
            panel = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    }
