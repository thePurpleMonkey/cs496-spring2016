package com.best_slopes.bestslopes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class ViewTrailActivity extends AppCompatActivity {
    private Trail trail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trail);
        Bundle b = getIntent().getExtras();
        int id = b.getInt("Trail_ID");
//        this.trail = getTrailByID(id);
        this.trail = MainActivity.getAllTrails().get(id); // Jhon: Temporary but an ugly fix for id
        fillFields();
    }

    private Trail getTrailByID(int id) {
        String ID = "" + id; // Short Cast
        DatabaseContract.LoadTrailTask task = new DatabaseContract.LoadTrailTask(this);
        task.execute(ID);
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.e("Database", e.getMessage());
            finish();
        } catch (InterruptedException e) {
            Log.e("Database", e.getMessage());
            finish();
        }
        return task.getResult();
    }

    private void fillFields() {
        if (this.trail != null) {
            ((TextView) findViewById(R.id.trailNameText)).setText(trail.getName());
            ((ImageView) findViewById(R.id.difficultyImage)).setImageResource(trail.getImageByDifficulty());
            ((RatingBar) findViewById(R.id.trailRatingBar)).setRating(trail.getRating());
            ((GridView) findViewById(R.id.imageGridView)).setAdapter(new ImageAdapter(this, trail));
        }
    }
}
