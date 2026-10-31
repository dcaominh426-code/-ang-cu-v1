
package com.dangcu.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(35, 70, 35, 30);
        layout.setBackgroundColor(Color.rgb(20, 20, 20));

        TextView title = new TextView(this);
        title.setText("DANG CU V1");
        title.setTextSize(28);
        title.setTextColor(Color.RED);
        layout.addView(title);

        Button menu = new Button(this);
        menu.setText("BAT MENU NOI");
        layout.addView(menu);

        Button ff = new Button(this);
        ff.setText("MO FREE FIRE");
        layout.addView(ff);

        Button ffmax = new Button(this);
        ffmax.setText("MO FREE FIRE MAX");
        layout.addView(ffmax);

        menu.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 23 &&
                !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
            } else {
                startService(new Intent(
                    this, FloatingService.class));
            }
        });

        ff.setOnClickListener(v ->
            openGame("com.dts.freefireth"));

        ffmax.setOnClickListener(v ->
            openGame("com.dts.freefiremax"));

        setContentView(layout);
    }

    private void openGame(String pkg) {
        Intent intent = getPackageManager()
            .getLaunchIntentForPackage(pkg);

        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this,
                "Chua cai game nay",
                Toast.LENGTH_SHORT).show();
        }
    }
}
