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

        DatabaseContract.LoadTask allTrails = new DatabaseContract.LoadTask(this);
        allTrails.execute();
        try {
            allTrails.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        DatabaseContract.LoadTrailTask task = new DatabaseContract.LoadTrailTask(this);
        task.execute("1");
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.e("Database", e.getMessage());
            finish();
        } catch (InterruptedException e) {
            Log.e("Database", e.getMessage());
            finish();
        }

        Trail trail = task.getResult();

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.test_database_layout);

        TextView text = new TextView(getApplicationContext());
        text.setTextColor(Color.RED);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        myLayout.addView(text);


        if (trail != null) {
            text.setText(trail.toString());
        } else {
            text.setText("No result");
        }
    }
}