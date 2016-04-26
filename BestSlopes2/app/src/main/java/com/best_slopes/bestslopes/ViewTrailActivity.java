package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ViewTrailActivity extends AppCompatActivity {
    static final int REQUEST_EDIT_TRAIL = 1;
    private Trail trail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trail);
        Bundle b = getIntent().getExtras();
        int id = b.getInt("Trail_ID");
        this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, id);
        setMyActionBar();
        fillFields();
    }

    private void setMyActionBar() {
        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
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
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    ((EditText) v).setText("");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_file, menu);
        getMenuInflater().inflate(R.menu.edit_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getApplicationContext();
        CharSequence text = "Currently the default settings layout";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editTrailIntent = new Intent(this, EditTrailActivity.class);
                editTrailIntent.putExtra("Trail_ID", trail.getId());
                startActivityForResult(editTrailIntent, REQUEST_EDIT_TRAIL);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_TRAIL) {
            if (resultCode == RESULT_OK) {
                this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, trail.getId());
                fillFields();
            }
        }
    }
}