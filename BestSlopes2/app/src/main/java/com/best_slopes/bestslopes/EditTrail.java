package com.best_slopes.bestslopes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ToggleButton;

public class EditTrail extends AppCompatActivity {
    public static final int[] toggles = {R.id.toggle_easy, R.id.toggle_medium, R.id.toggle_difficult,
            R.id.toggle_extremely_difficult};

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
        switch (item.getItemId()) {
            case R.id.action_save:
                Log.d("Database", "Starting save AsyncTask...");
                saveTrail();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveTrail() {
        EditText trailName = (EditText) findViewById(R.id.edit_trail_name);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.edit_rating);
        EditText commentsText = (EditText) findViewById(R.id.edit_comments);

        // Gather data from layout objects
        String title = trailName.getText().toString();
        int rating = (int) ratingBar.getRating() * 2;
        String comments = commentsText.getText().toString();

        // Determine difficulty
        int difficulty = DatabaseContract.TrailContract.INVALID_DIFFICULTY;
        for (int id : toggles) {
            ToggleButton button = (ToggleButton) findViewById(id);
            if (button.isChecked()) {
                switch (button.getId()) {
                    case R.id.toggle_easy:
                        difficulty = DatabaseContract.TrailContract.EASY_DIFFICULTY;
                        break;

                    case R.id.toggle_medium:
                        difficulty = DatabaseContract.TrailContract.MEDIUM_DIFFICULTY;
                        break;

                    case R.id.toggle_difficult:
                        difficulty = DatabaseContract.TrailContract.DIFFICULT_DIFFICULTY;
                        break;

                    case R.id.toggle_extremely_difficult:
                        difficulty = DatabaseContract.TrailContract.EXTREME_DIFFICULTY;
                        break;

                    default:
                        difficulty = DatabaseContract.TrailContract.INVALID_DIFFICULTY;
                        break;
                }
            }
        }


        new DatabaseContract.SaveTask(this).execute(title, String.valueOf(difficulty), String.valueOf(rating), comments);
    }
}
