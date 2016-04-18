package edu.oregonstate.cs489.humphrmi.bestslopes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_DIFFICULTY = "edu.oregonstate.cs489.humphrmi.bestslopes.DIFFICULTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startActivity(View view) {
        Intent intent = new Intent(this, EditTrail.class);
        startActivity(intent);
    }
}
