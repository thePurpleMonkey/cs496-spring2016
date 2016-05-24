package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.best_slopes.bestslopes.DatabaseContract;
import com.best_slopes.bestslopes.Constants;
import com.best_slopes.bestslopes.http.HttpGet;
import com.best_slopes.bestslopes.http.HttpPost;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import com.best_slopes.bestslopes.ServerComms;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Trail> trails;
    /* for camera code */
    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private DatabaseContract.LoadTask task = new DatabaseContract.LoadTask(this);

    // User credentials for syncing
    private static String username = null;
    private static long session = -1;

    //initialize sortOrderIndex using Box class
    private Box<Integer> sortOrderIndex = new Box<>(SORT_DIFFICULTY);

    private static final int SORT_DIFFICULTY =    0;
    private static final int SORT_RATING =        1;
    private static final int SORT_TITLE =         2;

    private SwipeRefreshLayout swipeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Allow icon to be displayed in ActionBar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        setupSwipeLayout();
        loadListViewMain();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume(){
        super.onResume();       //must be called first
        loadListViewMain();

    }

    private void setupSwipeLayout(){
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeLayout.setSize(Constants.LARGE);

        //set swipe listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ServerComms.LoadTrailsFromServer loadTrails = new ServerComms.LoadTrailsFromServer();
                try{
                    //Get trails from server!
                    final ArrayList<Trail> temp_trail = loadTrails.execute().get();

                    //Delete trails from phone's db
                    DatabaseContract.RefreshDatabaseTask db = new DatabaseContract.RefreshDatabaseTask();
                    db.RefreshDatabaseTask(getApplicationContext());
                    db.execute(temp_trail);     //execute refresh, passing temp_trail

                } catch (Exception e){
                    Log.e("Async server load error", e.toString());
                }

                //turns off refresh symbol after
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadListViewMain();     //reload list on main

                        swipeLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });


    }

    public void loadListViewMain() {
        ListView myListView = (ListView) findViewById(R.id.trail_list);

        DatabaseContract.LoadTask task = new DatabaseContract.LoadTask(this);

        //Calls LoadTask
        task.execute(sortOrderIndex.getValue());

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
            case R.id.action_add_run:
                Intent editTrailIntent = new Intent(this, EditTrailActivity.class);
                editTrailIntent.putExtra("Trail_ID", -1); // John: -1 means trail will be created, not edited

                editTrailIntent.putExtra("trail_string", trails.toString());
                startActivity(editTrailIntent);
                return true;

            case R.id.menu_backup_trails:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerComms.SendAllTrailsToServer sendAll = new ServerComms.SendAllTrailsToServer();
                        sendAll.execute(trails);

                        //Send toast
                        Toast toast = Toast.makeText(   getApplicationContext(),
                                                        "Sending trails to server...",
                                                        Toast.LENGTH_LONG);
                        toast.show();

                    }
                });

                return true;

            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, About.class);
                startActivity(aboutIntent);
                return true;

            case R.id.menu_debug:
                startActivity(new Intent(this, TestDatabase.class));
                return true;

            case R.id.menu_login:
                startActivityForResult(new Intent(this, LoginActivity.class), 7);
                return true;

            case R.id.menu_sort_by:
                final CharSequence[] items = {"Trail Difficulty (hardest top)", "Trail Rating (highest top)", "Trail Title (Z-A)"};

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Sort By");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case SORT_DIFFICULTY:
                                sortOrderIndex.setValue(SORT_DIFFICULTY);
                                break;
                            case SORT_RATING:
                                sortOrderIndex.setValue(SORT_RATING);
                                break;
                            case SORT_TITLE:
                                sortOrderIndex.setValue(SORT_TITLE);
                                break;
                        }
                        loadListViewMain();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 7 && resultCode == RESULT_OK && data != null) {
            session = data.getLongExtra(LoginActivity.sessionResult, -1L);
            username = data.getStringExtra(LoginActivity.emailResult);
        }
    }

    public static String getUsername() {
        return username;
    }

    public static Long getSession() {
        return session;
    }

    public ArrayList<Trail> getTrails(){
        return trails;
    }
}
