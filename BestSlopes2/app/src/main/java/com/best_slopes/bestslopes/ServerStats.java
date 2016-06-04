package com.best_slopes.bestslopes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ServerStats extends Activity {
    TextView content;
    List<String> serverData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_stats);

//        content = (TextView) findViewById(R.id.server_stats_list);
        LinearLayout myLayout = (LinearLayout) findViewById(R.id.server_stats_layout);
        TextView text = new TextView((getApplicationContext()));
        text.setTextColor(Color.BLUE);

        text.setText("Server Stats");
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);


        text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setText("HEY FUCKBOII2");
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        myLayout.addView(text);
    }
}
