package com.best_slopes.bestslopes;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class TestDatabase extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        DatabaseContract.LoadTask task = new DatabaseContract.LoadTask(this);
        task.execute();
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.e("Database", e.getMessage());
            finish();
        } catch (InterruptedException e) {
            Log.e("Database", e.getMessage());
            finish();
        }

        Trail[] trails = task.getResults();

        for (Trail trail : trails) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.test_database_layout);

            TextView text = new TextView(getApplicationContext());
            text.setText(trail.toString());
            text.setTextColor(Color.RED);
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            myLayout.addView(text);
        }
    }
}
