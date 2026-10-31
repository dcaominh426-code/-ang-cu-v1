app/src/main/java/com/dangcu/app/FloatingService.java
package com.dangcu.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class FloatingService extends Service {
    private WindowManager wm;
    private TextView menu;

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager)
            getSystemService(WINDOW_SERVICE);

        menu = new TextView(this);
        menu.setText("DANG CU\nMENU ON");
        menu.setTextColor(Color.WHITE);
        menu.setTextSize(17);
        menu.setPadding(25, 20, 25, 20);
        menu.setBackgroundColor(Color.rgb(180, 0, 0));

        int type = android.os.Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params =
            new WindowManager.LayoutParams(
                -2, -2, type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            );

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 50;
        params.y = 200;

        menu.setOnTouchListener(new View.OnTouchListener() {
            int startX, startY;
            float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        touchX = e.getRawX();
                        touchY = e.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = startX +
                            (int)(e.getRawX() - touchX);
                        params.y = startY +
                            (int)(e.getRawY() - touchY);
                        wm.updateViewLayout(menu, params);
                        return true;
                }
                return false;
            }
        });

        wm.addView(menu, params);
    }

    @Override
    public void onDestroy() {
        if (menu != null) {
            wm.removeView(menu);
            menu = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
