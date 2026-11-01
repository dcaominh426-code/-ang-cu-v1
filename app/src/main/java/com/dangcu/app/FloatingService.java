
package com.dangcu.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class FloatingService extends Service {
    private WindowManager wm;
    private LinearLayout menu;
    private View crosshair;
    private WindowManager.LayoutParams menuParams;
    private boolean expanded = false;

    @Override
    public void onCreate() {
        super.onCreate();

        if (!Settings.canDrawOverlays(this)) {
            stopSelf();
            return;
        }

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        crosshair = new View(this) {
            Paint paint = new Paint(3);

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float x = getWidth() / 2f;
                float y = getHeight() / 2f;

                paint.setColor(Color.RED);
                paint.setStrokeWidth(4);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawCircle(x, y, 12, paint);
                canvas.drawLine(x - 22, y, x - 7, y, paint);
                canvas.drawLine(x + 7, y, x + 22, y, paint);
                canvas.drawLine(x, y - 22, x, y - 7, paint);
                canvas.drawLine(x, y + 7, x, y + 22, paint);
            }
        };

        WindowManager.LayoutParams aimParams =
            new WindowManager.LayoutParams(
                -1, -1,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            );

        wm.addView(crosshair, aimParams);
        crosshair.setVisibility(View.GONE);

        menu = new LinearLayout(this);
        menu.setOrientation(LinearLayout.VERTICAL);
        menu.setPadding(20, 15, 20, 15);
        menu.setBackgroundColor(Color.rgb(25, 25, 25));

        TextView title = new TextView(this);
        title.setText("☰ DANG CU V3");
        title.setTextColor(Color.RED);
        title.setTextSize(20);
        title.setPadding(10, 10, 10, 10);
        menu.addView(title);

        LinearLayout options = new LinearLayout(this);
        options.setOrientation(LinearLayout.VERTICAL);
        options.setVisibility(View.GONE);
        menu.addView(options);

        Switch aim = new Switch(this);
        aim.setText("TAM NGAM");
        aim.setTextColor(Color.WHITE);
        options.addView(aim);

        aim.setOnCheckedChangeListener((button, checked) ->
            crosshair.setVisibility(
                checked ? View.VISIBLE : View.GONE
            )
        );

        TextView note = new TextView(this);
        note.setText("Tam ngam hien thi - khong tu khoa dich");
        note.setTextColor(Color.LTGRAY);
        options.addView(note);

        Button close = new Button(this);
        close.setText("DONG MENU");
        options.addView(close);

        close.setOnClickListener(v -> stopSelf());

        title.setOnClickListener(v -> {
            expanded = !expanded;
            options.setVisibility(
                expanded ? View.VISIBLE : View.GONE
            );
            wm.updateViewLayout(menu, menuParams);
        });

        menuParams = new WindowManager.LayoutParams(
            -2, -2,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        );

        menuParams.gravity = Gravity.TOP | Gravity.LEFT;
        menuParams.x = 40;
        menuParams.y = 180;

        title.setOnTouchListener(new View.OnTouchListener() {
            float downX, downY;
            int startX, startY;
            boolean moved;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        startX = menuParams.x;
                        startY = menuParams.y;
                        moved = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int)(event.getRawX() - downX);
                        int dy = (int)(event.getRawY() - downY);

                        if (Math.abs(dx) > 12 ||
                            Math.abs(dy) > 12) {
                            moved = true;
                        }

                        if (moved) {
                            menuParams.x = startX + dx;
                            menuParams.y = startY + dy;
                            wm.updateViewLayout(menu, menuParams);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!moved) v.performClick();
                        return true;
                }
                return false;
            }
        });

        wm.addView(menu, menuParams);
    }

    @Override
    public void onDestroy() {
        if (wm != null) {
            if (menu != null) wm.removeView(menu);
            if (crosshair != null) wm.removeView(crosshair);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
                                }
