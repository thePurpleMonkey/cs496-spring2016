package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

public class ViewTrailActivity extends AppCompatActivity {
    static final int REQUEST_EDIT_TRAIL = 1;
    private Trail trail;
    Button camera_button;
    GridView myGrid;
    String test;
    int image_counter = 0;
    private String[] image_path =  new String[20]; //storing images paths

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trail);
        Bundle b = getIntent().getExtras();
        int id = b.getInt("Trail_ID");
        this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, id);
        fillFields();
        setMyActionBar();

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

           ( (RatingBar) findViewById(R.id.trailRatingBar)).setRating(trail.getRating());
            GridView gridView = (GridView) findViewById(R.id.imageGridView);
            ImageAdapter imageAdapter = new ImageAdapter(this, trail);
            gridView.setAdapter(imageAdapter);


            /*for (int i = 0 ; i < image_counter ; ++i) { //loading all images taken into ImageAdapter (GridView)
                imageAdapter.getDebuggingImages(image_path[i]);
            }
            */

            ListView commentsView = ((ListView) findViewById(R.id.commentsListView));
            final CommentAdapter commentAdapter = new CommentAdapter(this, trail, commentsView);
            commentsView.setAdapter(commentAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.view_trail_menu_file, menu);
        getMenuInflater().inflate(R.menu.edit_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getApplicationContext();

        switch (item.getItemId()) {
            case R.id.action_edit:
                Intent editTrailIntent = new Intent(this, EditTrailActivity.class);
                editTrailIntent.putExtra("Trail_ID", trail.getId());
                startActivityForResult(editTrailIntent, REQUEST_EDIT_TRAIL);
                return true;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, About.class);
                startActivity(aboutIntent);
                return true;
            case R.id.menu_debug:
                startActivity(new Intent(this, TestDatabase.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT_TRAIL:
                if (resultCode == RESULT_OK) {
                    this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, trail.getId());
                    fillFields();
                }
                break;
            case ImageAdapter.REQUEST_TAKE_PICTURE:
                if (resultCode == RESULT_OK) {
                Bundle b = getIntent().getExtras();
                fillFields();
                }
                break;

        }
    }

    public void onDestroy() {
        new DatabaseContract.UpdateTrailTask(this).execute(trail);
        super.onDestroy();
    }
}