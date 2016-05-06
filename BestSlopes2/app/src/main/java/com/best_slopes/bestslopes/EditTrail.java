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

        //TODO: Fix error.
        //EditText commentsText = (EditText) findViewById(R.id.edit_comments);

        Trail trail = new Trail();

        // Gather data from layout objects
        trail.setName(trailName.getText().toString());
        trail.setRating(ratingBar.getRating());

        //TODO: Load comments into trail object to be saved.
        //trail.setComments(commentsText.getText().toString());

        // Determine difficulty
        trail.setDifficulty(DatabaseContract.TrailContract.INVALID_DIFFICULTY);
        for (int id : toggles) {
            ToggleButton button = (ToggleButton) findViewById(id);
            if (button.isChecked()) {
                switch (button.getId()) {
                    case R.id.toggle_easy:
                        trail.setDifficulty(DatabaseContract.TrailContract.EASY_DIFFICULTY);
                        break;

                    case R.id.toggle_medium:
                        trail.setDifficulty(DatabaseContract.TrailContract.MEDIUM_DIFFICULTY);
                        break;

                    case R.id.toggle_difficult:
                        trail.setDifficulty(DatabaseContract.TrailContract.DIFFICULT_DIFFICULTY);
                        break;

                    case R.id.toggle_extremely_difficult:
                        trail.setDifficulty(DatabaseContract.TrailContract.EXTREME_DIFFICULTY);
                        break;

                    default:
                        trail.setDifficulty(DatabaseContract.TrailContract.INVALID_DIFFICULTY);
                        break;
                }
            }
        }

        new DatabaseContract.SaveTrailTask(this).execute(trail);
    }
}
