package com.best_slopes.bestslopes;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
        this.trail = getTrailByID(id);
        setMyActionBar();
        fillFields();
    }

    private void setMyActionBar() {
        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
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
            ListView commentsView = ((ListView) findViewById(R.id.commentsListView));
            commentsView.setAdapter(new CommentAdapter(this, trail, commentsView));
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
