package com.best_slopes.bestslopes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.best_slopes.bestslopes.http.HttpPost;

import java.util.ArrayList;

public class EditTrailActivity extends AppCompatActivity {
    private static final int[] toggles = {R.id.toggle_easy, R.id.toggle_medium, R.id.toggle_difficult,
            R.id.toggle_extremely_difficult};
    private Trail trail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        setContentView(R.layout.activity_edit_trail);
        Bundle b = getIntent().getExtras();
        int id = b.getInt("Trail_ID");
        if (id != -1) { // John: if an existing trail will be edited
            this.trail = DatabaseContract.LoadTrailTask.getTrailByID(this, id);
            setTitle(trail.getName());

            //Set icon on action bar for existing trail
            int icon = trail.getImageByDifficulty();

            actionBar.setIcon(icon);
//            getSupportActionBar().set
        }
        else {
            this.trail = new Trail();
            setTitle("New Trail...");
        }
        //Allow icon to be displayed in ActionBar
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);
        fillFields();
    }

    private void fillFields() {
        ListView lv = (ListView) findViewById(R.id.commentsListView);
        lv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        GridView gv = (GridView) findViewById(R.id.imageGridView);

        gv.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        //((GridView) findViewById(R.id.imageGridView)).setAdapter(new ImageAdapter(this, trail));
        ListView commentsView = ((ListView) findViewById(R.id.commentsListView));
        commentsView.setAdapter(new CommentAdapter(this, trail, commentsView));
        commentsView.setSelection(trail.getComments().length-1);

        //If new trail sets up layout and doesn't fill anything out
        if (this.trail.isNew()) {
            commentsView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        //else trail already exists and we just display it.
        else {
            TextView editName = ((TextView) findViewById(R.id.edit_trail_name));
            editName.setText(trail.getName());
            setOnEditorActionListener(editName);
            ((ToggleButton) findViewById(toggles[0])).setChecked(false);
            ((ToggleButton) findViewById(toggles[trail.getDifficulty()-1])).setChecked(true);
            ((RatingBar) findViewById(R.id.edit_rating)).setRating(trail.getRating());
        }
    }

    private void setOnEditorActionListener(TextView editText) {
        editText.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (!event.isShiftPressed()) {
                                return true;
                            }
                        }
                        return false;
                    }
                });
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
                final Trail editedTrail = redTrailFromScreen();

                //TODO: add this to DB contract to know the trail id
//                SendTrailToServer sendTrailsToServer = new SendTrailToServer();
//                sendTrailsToServer.execute(editedTrail);

                if (!this.trail.isNew()) {
                    Log.d("Database", "Starting update AsyncTask...");
                    editedTrail.setId(this.trail.getId());
                    new DatabaseContract.UpdateTrailTask(this).execute(editedTrail);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Trail_ID", trail.getId());
                    setResult(RESULT_OK,returnIntent);
                }
                else {
                    Log.d("Database", "Starting save AsyncTask...");
                    //editedTrail.setOld();
                    new DatabaseContract.SaveTrailTask(this).execute(editedTrail);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SendSingleTrailToServer sendTrailsToServer = new SendSingleTrailToServer();
                            sendTrailsToServer.execute(editedTrail);
                            Log.d("editedTrail", editedTrail.toString());
                        }
                    });
                }
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class SendSingleTrailToServer extends AsyncTask<Trail, Void, Void> {

        protected Void doInBackground(Trail... param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            if(param.length < 0){
                Log.e("Send Single To Server", "array_trail was empty, couldn't send");
                return null;
            }

            Trail trail = param[0];

            String id_concatenated = (Long.toString(Constants.OWNER_ID) + "_" + Integer.toString(trail.getId()));

            sendTrails.addFormField("id", id_concatenated);        //todo, change this to use ownerID
            sendTrails.addFormField("owner_id", Long.toString(Constants.OWNER_ID));        //TODO: don't hardcode ownerID
            sendTrails.addFormField("title", trail.getName());
            sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
            sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));

            String[] comments;
            StringBuilder everyComment = new StringBuilder();

            comments = trail.getComments();

            for (String s : comments)
                everyComment.append(s + ","); //comma separate comments to store on server

            if (comments != null && comments.length > 0)
                sendTrails.addFormField("comment", everyComment.toString());
            else
                sendTrails.addFormField("comment", Constants.NULL_STR);     //must send something to server for comment

            try {
                String valResult = sendTrails.finish();     //send to server
                Log.d("HttpPost result", valResult);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected Void onPostExecute(Void... voids){

            return null;
        }
    }

    public static class SendAllTrailsToServer extends AsyncTask<ArrayList<Trail>, Void, Void> {

        protected Void doInBackground(ArrayList<Trail>... array_param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            Long owner_id = 10L;

            if(array_param.length < 0){
                Log.e("Send All To Server", "array_trail was empty, couldn't send");
                return null;
            }

            for(int i = 0; i< array_param[0].size(); i++) {
                Trail trail = array_param[0].get(i);

                String id_concatenated = (Long.toString(owner_id) + "_" + Integer.toString(trail.getId()));

                sendTrails.addFormField("id", id_concatenated);        //todo, change this to use ownerID
                sendTrails.addFormField("owner_id", Long.toString(owner_id));        //TODO: don't hardcode ownerID
                sendTrails.addFormField("title", trail.getName());
                sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
                sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));

                String[] comments;
                StringBuilder everyComment = new StringBuilder();

                comments = trail.getComments();

                for (String s : comments)
                    everyComment.append(s + ","); //comma separate comments to store on server

                if (comments != null && comments.length > 0)
                    sendTrails.addFormField("comment", everyComment.toString());
                else
                    sendTrails.addFormField("comment", Constants.NULL_STR);     //must send something to server for comment

                try {
                    String valResult = sendTrails.finish();     //send to server
                    Log.d("HttpPost result", valResult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        protected Void onPostExecute(Void... voids){

            return null;
        }
    }

    private Trail redTrailFromScreen() {
        EditText trailName = (EditText) findViewById(R.id.edit_trail_name);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.edit_rating);

        Trail trail = new Trail();

        trail.setName(trailName.getText().toString());
        trail.setRating((int) ratingBar.getRating());
        for (String comment : this.trail.getComments()) {
            trail.addComment(comment);
        }

        for (String imagePath : this.trail.getImagePaths()) {
            trail.addImagePath(imagePath);
        }

        // Gather data from layout objects
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
        return trail;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int actionId = event.getAction();
        if (actionId == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    v.clearFocus();
                    if (v.getId() == R.id.commentField) {
                        ((EditText) v).setText("");
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
