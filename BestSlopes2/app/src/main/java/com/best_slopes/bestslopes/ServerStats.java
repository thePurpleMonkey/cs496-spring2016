package com.best_slopes.bestslopes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ServerStats extends Activity {
    private Stats stats = new Stats();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_stats);

        /** Get stats from server **/
        ServerComms.LoadStatsFromServer getStats = new ServerComms.LoadStatsFromServer(getApplicationContext());
        try{
            stats = getStats.execute().get();
        } catch (Exception e){
            Log.e("Server load stats error", e.toString());
        }

        /** Load stats into text view **/
        TextView hourCount = (TextView) findViewById(R.id.hourCountText);
        hourCount.setText(String.valueOf(stats.getHourCount()));

        TextView dayCount = (TextView) findViewById(R.id.dayCountText);
        dayCount.setText(String.valueOf(stats.getDayCount()));

        TextView weekCount = (TextView) findViewById(R.id.weekCountText);
        weekCount.setText(String.valueOf(stats.getWeekCount()));

        TextView totalCount = (TextView) findViewById(R.id.totalCountText);
        totalCount.setText(String.valueOf(stats.getTotalCount()));
    }
}
