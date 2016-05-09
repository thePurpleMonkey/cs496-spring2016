package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class ViewTrailActivity extends AppCompatActivity {
    static final int REQUEST_EDIT_TRAIL = 1;
    private Trail trail;
    Button camera_button;
    private static final int REQUEST_CODE = 2;
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
        setMyActionBar();
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

           ( (RatingBar) findViewById(R.id.trailRatingBar)).setRating(trail.getRating());

            GridView gridView = (GridView) findViewById(R.id.imageGridView);
            ImageAdapter imageAdapter = new ImageAdapter(this, trail, image_path, image_counter);
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

            case R.id.action_camera:
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, REQUEST_CODE);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (requestCode == REQUEST_EDIT_TRAIL) {
                    if (resultCode == RESULT_OK) {
                        this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, trail.getId());
                        fillFields();
                    }
                }
            case 2: //camera
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), imageBitmap);
                    if (image_counter < 20) { //maximum amount of images to store in one TrailView
                        image_path[image_counter] = getRealPathFromURI(tempUri);
                        ++image_counter;
                        fillFields();

                    }
                }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public void onDestroy() {
        new DatabaseContract.UpdateTrailTask(this).execute(trail);
        super.onDestroy();
    }
}