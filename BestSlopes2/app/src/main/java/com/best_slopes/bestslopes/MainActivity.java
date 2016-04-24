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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_DIFFICULTY = "extra hard";
    private ArrayAdapter<String> adapter ;

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
        ArrayList<String> testList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.trail_row, testList);

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
            adapter.add(trail.getName());
            myListView.setAdapter(adapter);
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

    public void addToDbButton(View view){
        Context context = getApplicationContext();

        CharSequence text = "Adding to DB";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();           //shows that the settings tab is temporary



    }


}
