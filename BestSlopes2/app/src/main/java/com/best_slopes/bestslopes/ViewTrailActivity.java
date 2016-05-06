package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
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

        //Fills fields onCreate
        fillFields();

        //Peter: Makes keyboard not pop up on screen! #necessary :)
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void setMyActionBar() {
        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    private void fillFields() {
        if (this.trail != null) {
            setTitle(trail.getName());
            int icon = trail.getImageByDifficulty();
            if(getSupportActionBar() != null)
                getSupportActionBar().setIcon(icon);
            else
                Log.e("Null catch", "Actionbar = Null");

            //Rating bar values
            ( (RatingBar) findViewById(R.id.trailRatingBar)).setRating(trail.getRating());
//            ( (RatingBar) findViewById(R.id.trailRatingBar)).setStepSize(trail.getRating());        //step size

            float num_stars =  (((RatingBar) findViewById(R.id.trailRatingBar)).getRating());
            Log.d("Rating bar value", Float.toString(num_stars));

            GridView gridView = (GridView) findViewById(R.id.imageGridView);
            ImageAdapter imageAdapter = new ImageAdapter(this, trail);
            gridView.setAdapter(imageAdapter);
            ListView commentsView = ((ListView) findViewById(R.id.commentsListView));
            final CommentAdapter commentAdapter = new CommentAdapter(this, trail, commentsView);
            commentsView.setAdapter(commentAdapter);
        }
    }
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View v = getCurrentFocus();
//            if (v instanceof EditText) {
//                Rect outRect = new Rect();
//                v.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    ((EditText) v).setText("");
//                    v.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//                }
//            }
//        }
//        return super.dispatchTouchEvent(event);
//    }

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

    public void onDestroy() {
        new DatabaseContract.UpdateTrailTask(this).execute(trail);
        super.onDestroy();
    }
}