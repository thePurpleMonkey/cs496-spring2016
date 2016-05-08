package com.best_slopes.bestslopes;

import android.app.Activity;
import android.graphics.Color;
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

        DatabaseContract.LoadTask allTrailsTask = new DatabaseContract.LoadTask(this);
        allTrailsTask.execute(0);
        try {
            allTrailsTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Trail[] trails =  allTrailsTask.getResults();


        Trail first = trails[0];
        Log.d("Test", "ID of first: " + first.getId());
        first.addImagePath("/example/path/one.jpg");
        first.addImagePath("/example/path/two.png");
        first.addImagePath("/example/path/three.bmp");
        new DatabaseContract.UpdateTrailTask(this).execute(first);


        allTrailsTask = new DatabaseContract.LoadTask(this);
        allTrailsTask.execute(0);
        try {
            allTrailsTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        trails =  allTrailsTask.getResults();

        for (Trail trail : trails) {
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.test_database_layout);

            TextView text = new TextView(getApplicationContext());
            text.setTextColor(Color.RED);
            text.setText(trail.toString());
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            myLayout.addView(text);

        }
    }
}