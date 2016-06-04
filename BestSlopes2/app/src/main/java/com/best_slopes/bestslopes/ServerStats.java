package com.best_slopes.bestslopes;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ServerStats extends Activity {
    TextView content;
    List<String> serverData;
    private Stats stats = new Stats();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_stats);

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.server_stats_layout);
        TextView text = new TextView((getApplicationContext()));
        text.setTextColor(Color.BLUE);

        /** Set title **/
        text.setText("Server Statistics");
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);


        /** Get stats from server **/
        ServerComms.LoadStatsFromServer getStats = new ServerComms.LoadStatsFromServer();
        try{
            stats = getStats.execute().get();
        } catch (Exception e){
            Log.e("Server load stats error", e.toString());
        }

        /** Load stats into text view **/
        //TODO: Update this with real data
        text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setText("Active trails in past Hour " + stats.getHourCount());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);

        text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setText("Active trails in past Day " + stats.getDayCount());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);

        text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setText("Active trails in past Week " + stats.getWeekCount());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);

        text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setText("Active trails in Total " + stats.getTotalCount());
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        myLayout.addView(text);
    }
}
