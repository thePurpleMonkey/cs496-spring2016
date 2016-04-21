package com.best_slopes.bestslopes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class EditTrail extends AppCompatActivity {
    public final int[] toggles = {R.id.toggle_easy, R.id.toggle_medium, R.id.toggle_difficult,
            R.id.toggle_extremely_difficult};
    public final int INVALID_DIFFICULTY = -1;
    public final int EASY_DIFFICULTY = 1;
    public final int MEDIUM_DIFFICULTY = 2;
    public final int DIFFICULT_DIFFICULTY = 3;
    public final int EXTREME_DIFFICULTY = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trail);

        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    public void difficultyChanged(View view) {
        ToggleButton clicked = (ToggleButton) view;

        for (int id : toggles) {
            ToggleButton button = (ToggleButton) findViewById(id);
            button.setChecked(false);
        }

        clicked.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.save_menu_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast toast = Toast.makeText(getApplicationContext(), "Saving...", Toast.LENGTH_SHORT);

        switch (item.getItemId()) {
            case R.id.action_save:
                toast.show();
                new SaveTask().execute();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        String title;
        String comments;
        float rating;
        int difficulty;

        protected void onPreExecute() {
            // Create references to layout objects
            EditText trailName = (EditText) findViewById(R.id.edit_trail_name);
            RatingBar ratingBar = (RatingBar) findViewById(R.id.edit_rating);
            EditText commentsText = (EditText) findViewById(R.id.edit_comments);

            // Gather data from layout objects
            title = trailName.getText().toString();
            rating = ratingBar.getRating();
            comments = commentsText.getText().toString();

            // Determine difficulty
            difficulty = INVALID_DIFFICULTY;
            for (int id : toggles) {
                ToggleButton button = (ToggleButton) findViewById(id);
                if (button.isChecked()) {
                    switch (button.getId()) {
                        case R.id.toggle_easy:
                            difficulty = EASY_DIFFICULTY;
                            break;

                        case R.id.toggle_medium:
                            difficulty = MEDIUM_DIFFICULTY;
                            break;

                        case R.id.toggle_difficult:
                            difficulty = DIFFICULT_DIFFICULTY;
                            break;

                        case R.id.toggle_extremely_difficult:
                            difficulty = EXTREME_DIFFICULTY;
                            break;

                        default:
                            difficulty = INVALID_DIFFICULTY;
                            break;
                    }
                }
            }
        }

        protected Void doInBackground(Void... params) {
            DatabaseContract.Trail mDbHelper = new DatabaseContract.Trail(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Trail.COLUMN_NAME_TITLE, title);
            values.put(DatabaseContract.Trail.COLUMN_NAME_COMMENTS, comments);
            values.put(DatabaseContract.Trail.COLUMN_NAME_DIFFICULTY, difficulty);
            values.put(DatabaseContract.Trail.COLUMN_NAME_RATING, rating);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    DatabaseContract.Trail.TABLE_NAME,
                    "null",
                    values);

            return null;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute() {
            Toast toast = Toast.makeText(getApplicationContext(), "Save complete!", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}
