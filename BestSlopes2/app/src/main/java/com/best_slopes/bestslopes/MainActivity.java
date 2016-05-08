package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Trail> trails;
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

        loadListViewMain();

    }

    private void loadListViewMain() {
        ListView myListView = (ListView) findViewById(R.id.trail_list);

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

        Trail[] trailsArray = task.getResults();
        trails = new ArrayList<>(); // John: Used SparseArray<> for better performance
        for(Trail trail : trailsArray) {
            trails.add(trail);
        }
        //verifies list is not empty!
        if(trails.size() != 0){
            CustomAdapter customAdapter = new CustomAdapter(this, trails);
            myListView.setAdapter(customAdapter);
        }
        //Adds row telling user to add a trail
        else{
            //TODO: make this occur when the last item is deleted, not just onCreate/Resume
            ArrayAdapter<String> adapter;
            ArrayList<String> listItems=new ArrayList<String>();

            adapter = new ArrayAdapter<String>(this,
                    (R.layout.row_empty),
                    listItems);
            myListView.setAdapter(adapter);

            listItems.add("\n   Add trail\n");


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
        switch (item.getItemId()) {
            case R.id.menu_settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_add_run:
                Intent editTrailIntent = new Intent(this, EditTrailActivity.class);
                editTrailIntent.putExtra("Trail_ID", -1); // John: -1 means trail will be created, not edited
                startActivity(editTrailIntent);
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


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}
