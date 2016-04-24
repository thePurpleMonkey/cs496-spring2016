package com.best_slopes.bestslopes;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    /* for camera code */
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        loadListViewMain();
    }


    @Override
    protected void onResume(){
        super.onResume();       //must be called first

        //TODO: might be a bad way to add the newest item to the list. Probably want to add it when saving (works for now)
        loadListViewMain();

    }

    private void loadListViewMain() {
        ListView myListView = (ListView) findViewById(R.id.trail_list);
        ArrayList<String> trailNames = new ArrayList<String>();
        ArrayList<Integer> trailDifficultyImage = new ArrayList<Integer>();

        DatabaseContract.LoadTask task = new DatabaseContract.LoadTask(this);
        task.execute();
        try {
            task.get();
        } catch (ExecutionException e) {
            Log.e("Database", e.getMessage());
            finish();
        } catch (InterruptedException e) {
            Log.e("Database", e.getMessage());
            finish();
        }

        Trail[] trails = task.getResults();

        for (Trail trail : trails) {
            trailNames.add(trail.getName());

            switch(trail.getDifficulty()){
                case 1:
                    trailDifficultyImage.add(R.drawable.easy);
                    break;
                case 2:
                    trailDifficultyImage.add(R.drawable.medium);
                    break;
                case 3:
                    trailDifficultyImage.add(R.drawable.difficult);
                    break;
                case 4:
                    trailDifficultyImage.add(R.drawable.extremely_difficult);
                    break;
            }
        }

        //verifies list is not empty!
        //TODO: add a row item that says "Add item..." when empty
        if(!trailDifficultyImage.isEmpty() && !trailNames.isEmpty()){
            myListView.setAdapter(new CustomAdapter(this, trailNames, trailDifficultyImage));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_file, menu);
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Context context = getApplicationContext();
        CharSequence text = "Currently the default settings layout";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);

        switch (item.getItemId()) {
            case R.id.menu_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                //toast.show();           //shows that the settings tab is temporary
                return true;

            case R.id.action_add_run:
                Intent editTrailIntent = new Intent(this, EditTrail.class);
                startActivity(editTrailIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void enterTrail(View view){
        Context context = getApplicationContext();

    }


}
