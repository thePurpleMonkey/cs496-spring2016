package com.best_slopes.bestslopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_DIFFICULTY = "extra hard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_file, menu);
        return true;
    }

    public void startActivity(View view) {
        Intent intent = new Intent(this, EditTrail.class);
        startActivity(intent);
    }
}
