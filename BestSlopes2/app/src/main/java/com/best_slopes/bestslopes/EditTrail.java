package com.best_slopes.bestslopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class EditTrail extends AppCompatActivity {
    public final int[] toggles = {R.id.toggle_easy, R.id.toggle_medium, R.id.toggle_difficult,
            R.id.toggle_extremely_difficult};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trail);
    }

    public void difficultyChanged(View view) {
        ToggleButton clicked = (ToggleButton) view;

        for (int id : toggles) {
            ToggleButton button = (ToggleButton) findViewById(id);
            button.setChecked(false);
        }

        clicked.setChecked(true);
    }
}
