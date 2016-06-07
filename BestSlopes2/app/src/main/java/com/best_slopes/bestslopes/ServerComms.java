package com.best_slopes.bestslopes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.best_slopes.bestslopes.http.HttpGet;
import com.best_slopes.bestslopes.http.HttpPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pkess_000 on 5/21/2016.
 */
public class ServerComms {
    public static String calcServerID() {
//        String[ stringArray =
        ArrayList<Trail> trails = MainActivity.getTrails();

        String serverID;
        int id_int = 0;     //defaults to 0

        List<Integer> idList = new ArrayList<>();

//        String[] stringArray = trails.toString();

        //search through array list and compare serverIDs
        for(int i = 1; i < stringArray.length; i++){
            //have to sort the last item in list differently
            if(stringArray.length-1 == i)
                idList.add(Integer.parseInt((stringArray[i].split(":")[1].split(Constants.OWNER_ID_SEPARATOR)[1].split("]")[0])));
            else{
                idList.add(Integer.parseInt((stringArray[i].split(",")[0].split(Constants.OWNER_ID_SEPARATOR)[1])));

            }
        }

        Collections.sort(idList);       //sort idList

        //set id_int by comparing against every value in DB
        for(int i = 0; i < idList.size(); i++){
            //checks if id_int is in the list
            if(id_int == idList.get(i)){
                id_int++;       //id_int val was already in DB so increment
            }
        }

        if(idList.size() == 0){
            serverID = MainActivity.getUsername() + Constants.OWNER_ID_SEPARATOR + "0";
        }
        else{
            serverID = MainActivity.getUsername() + Constants.OWNER_ID_SEPARATOR + Integer.toString(id_int);
        }

        return serverID;

    }
    public static class LoadStatsFromServer extends AsyncTask<Void, Void, Stats> {
        private Context mContext;

        public LoadStatsFromServer(Context context) {
            this.mContext = context;
        }

        protected Stats doInBackground(Void... voids) {
            Stats stats = new Stats();
            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            try {

                HttpGet getStats = new HttpGet(Constants.STATS_URL, Constants.CHARSET);
                getStats.addFormField("id", Integer.toString(Constants.STATS_ID));
                String valResult = getStats.finish();
                JSONArray jsonArray = new JSONArray(valResult);

                JSONObject jsonObject;

                jsonObject = jsonArray.getJSONObject(0);        //TODO: parse the id, maybe don't have to.

                stats.setHourCount(Integer.parseInt(jsonObject.getString("hour_count")));
                stats.setDayCount(Integer.parseInt(jsonObject.getString("day_count")));
                stats.setWeekCount(Integer.parseInt(jsonObject.getString("week_count")));
                stats.setTotalCount(Integer.parseInt(jsonObject.getString("total_count")));
            } catch (Exception e) {
                Toast toast = Toast.makeText(mContext,
                        "Failed to get data from server...",
                        Toast.LENGTH_SHORT);
                toast.show();

                Log.e("Async get trails exc.", e.toString());
                return null;
            }

            return stats;
        }
    }

    public static class LoadTrailsFromServer extends AsyncTask<Void, Void, ArrayList<Trail>> {
        private Context mContext;
        private SwipeRefreshLayout swipeLayout;

        // Obtain a context
        public LoadTrailsFromServer(Context mContext, SwipeRefreshLayout swipeLayout) {
            this.mContext = mContext;
            this.swipeLayout = swipeLayout;
        }


        protected ArrayList<Trail> doInBackground(Void... voids) {
            ArrayList<Trail> trails = new ArrayList<Trail>();
            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

                //checks if user has logged in successfully!
                if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                    return null;        //so toast can be applied in post execute
                }
                else {
                    try {
                        HttpGet getTrails = new HttpGet(Constants.TRACKER_URL, Constants.CHARSET);
                        getTrails.addFormField("owner_id", MainActivity.getUsername());
                        String valResult = getTrails.finish();
                        JSONArray jsonArray = new JSONArray(valResult);

                        JSONObject jsonObject;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            Trail temp_trail = new Trail();
                            jsonObject = jsonArray.getJSONObject(i);

                            temp_trail.setServerID(jsonObject.getString("id"));

                            temp_trail.setName(jsonObject.getString("title"));
                            temp_trail.setDifficulty(Integer.parseInt(jsonObject.getString("difficulty")));
                            temp_trail.setRating(Integer.parseInt(jsonObject.getString("rating")));

                            //Parse comments from server by commas
                            String[] commentArray = ((jsonObject.getString("comment").split(Constants.COMMENT_SEPERATOR)));
                            ArrayList<String> commentList = new ArrayList<>();

                            //iterate over all strings after split
                            for (String s : commentArray) {
                                if (!s.equals(Constants.NULL_STR))     //null when there is no comment
                                    commentList.add(s);     //add to temp commentList to set later
                            }
                            temp_trail.setComment(commentList);     //set comments from server to phone.

                            trails.add(temp_trail);     //adds generated trial to ArrayList
                        }

                        return trails;
                }catch(Exception e){
                    Log.e("Async get trails exc.", e.toString());
                    }

            }
            return null;        //defualt return null
        }

        protected void onPostExecute(ArrayList<Trail> trails){
            swipeLayout.setRefreshing(false);       //turn off refreshing symbol

            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                Toast toast = Toast.makeText(mContext,
                        "Please login to use cloud functionality",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else if(trails == null) {
                Toast toast = Toast.makeText(mContext,
                        "Could not load trails, try again later",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(mContext,
                        "Successfully loaded trails from server!",
                        Toast.LENGTH_LONG);
                toast.show();
            }

        }
    }

    public static class SendSingleTrailToServer extends AsyncTask<Trail, Void, Boolean> {
        private Context mContext;

        // Obtain a context
        public SendSingleTrailToServer(Context mContext) {
            this.mContext = mContext;
        }

        protected Boolean doInBackground(Trail... param) {
            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            //checks if user has logged in successfully!
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                return null;        //so toast can be applied in post execute
            }
            else {
                HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

                if (param.length < 0) {
                    Log.e("Send Single To Server", "array_trail was empty, couldn't send");
                    return null;
                }

                Trail trail = param[0];

                sendTrails.addFormField("id", trail.getServerID());
                sendTrails.addFormField("owner_id", MainActivity.getUsername());        //TODO: don't hardcode ownerID
                sendTrails.addFormField("title", trail.getName());
                sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
                sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));
                sendTrails.addFormField("delete_trail", "0");    //indicates do not delete trail

                String[] comments;
                StringBuilder everyComment = new StringBuilder();

                comments = trail.getComments();

                for (String s : comments)
                    everyComment.append(s + Constants.COMMENT_SEPERATOR); //comma separate comments to store on server

                if (comments != null && comments.length > 0)
                    sendTrails.addFormField("comment", everyComment.toString());
                else
                    sendTrails.addFormField("comment", Constants.NULL_STR);     //must send something to server for comment

                try {
                    String valResult = sendTrails.finish();     //send to server
                    Log.d("HttpPost result", valResult);
                    return true;            // sent trails to server successfully
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;           //could not send trails to server
        }

        protected void onPostExecute(Boolean results) {
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                Toast toast = Toast.makeText(mContext,
                        "Please login to use cloud functionality",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else if (results == true) {
                Toast toast = Toast.makeText(mContext,
                        "Successfully sent trail to server!",
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(mContext,
                        "Could not send trail to server, try again later",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }

    public static class DeleteTrailFromServer extends AsyncTask<Trail, Void, Boolean> {
        private Context mContext;
        private Activity mainActivity;

        // Obtain a context
        public DeleteTrailFromServer(Context mContext, Activity mainActivity){
            this.mContext = mContext;
            this.mainActivity = mainActivity;
        }

        protected void onPreExecute(){
            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                return;         //do nothing
            }
            else {
                Toast toast = Toast.makeText(mContext,
                        "Deleting trail from server",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        protected Boolean doInBackground(Trail... param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            //checks if user has logged in successfully!
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                return null;        //so toast can be applied in post execute
            }
            else {
                if (param.length < 0) {
                    Log.e("Send Single To Server", "array_trail was empty, couldn't send");
                    return null;
                }

                Trail trail = param[0];

                //only need to send ID and delete_trail parameter!
                sendTrails.addFormField("id", trail.getServerID());        //todo, change this to use ownerID
                sendTrails.addFormField("owner_id", MainActivity.getUsername());        //TODO: don't hardcode ownerID
                sendTrails.addFormField("title", trail.getName());
                sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
                sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));
                sendTrails.addFormField("comment", Constants.NULL_STR);     //must send something to server for comment

                sendTrails.addFormField("delete_trail", "1");    //indicates delete trail

                try {
                    String valResult = sendTrails.finish();     //send to server
                    Log.d("HttpPost result", valResult);
                    return true;            // sent trails to server successfully
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;           //could not send trails to server
                }
            }
        }

        protected void onPostExecute(Boolean results) {
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                Toast toast = Toast.makeText(mContext,
                        "Please login to use cloud functionality",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else if (results == true) {
                ListView mainListView = (ListView) mainActivity.findViewById(R.id.trail_list);

                //need to synchronize when calling .notify on a ListView
                synchronized (mainListView) {
                    //Update the ListView that it changed
                    mainListView.notify();

                    //Checks if listView is empty, if it is, add item telling user to add trail.
                    //Less than or equal because the trail likely hasn't finished deleting
                    if(mainListView.getAdapter().getCount() <= 1) {
                        ArrayAdapter<String> adapter;
                        ArrayList<String> listItems = new ArrayList<String>();

                        adapter = new ArrayAdapter<String>(mContext,
                                (R.layout.row_empty),
                                listItems);
                        mainListView.setAdapter(adapter);

                        listItems.add("\n   Add trail using +\n");
                    }
                }
                Toast toast = Toast.makeText(mContext,
                        "Successfully deleted trail from server!",
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(mContext,
                        "Could not delete from server, try again later",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        }

    }

    public static class SendAllTrailsToServer extends AsyncTask<ArrayList<Trail>, Void, Boolean> {
        private Context mContext;

        // Obtain a context
        public SendAllTrailsToServer (Context mContext) {
            this.mContext = mContext;
        }

        protected void onPreExecute(){
            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            //check to see if we're NOT logged in currently
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                return;     //don't do anything
            }
            else {
                //We're logged in, send toast
                Toast toast = Toast.makeText(mContext,
                        "Sending trails to server...",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        protected Boolean doInBackground(ArrayList<Trail>... array_param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            if (Looper.myLooper() == null){
                Looper.prepare();               //syncs thread if Looper hasn't been created
            }

            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                return false;       //so post execute can handle toasts
            }
            else{
                if (array_param.length < 0) {
                    Log.e("Send All To Server", "array_trail was empty, couldn't send");
                    return null;
                }

                for (int i = 0; i < array_param[0].size(); i++) {
                    Trail trail = array_param[0].get(i);
                    String serverID = trail.getServerID();


                    //TODO: handle null case
                    if(serverID == null){
                        calcServerID();
                    }

                    sendTrails.addFormField("id", serverID);        //todo, change this to use ownerID
                    sendTrails.addFormField("owner_id", MainActivity.getUsername());        //TODO: don't hardcode ownerID
                    sendTrails.addFormField("title", trail.getName());
                    sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
                    sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));
                    sendTrails.addFormField("delete_trail", "0");    //indicates do not delete trail

                    String[] comments;
                    StringBuilder everyComment = new StringBuilder();

                    comments = trail.getComments();

                    for (String s : comments)
                        everyComment.append(s + Constants.COMMENT_SEPERATOR); //comma separate comments to store on server

                    if (comments != null && comments.length > 0)
                        sendTrails.addFormField("comment", everyComment.toString());
                    else
                        sendTrails.addFormField("comment", Constants.NULL_STR);     //must send something to server for comment

                    try {
                        String valResult = sendTrails.finish();     //send to server
                        Log.d("HttpPost result", valResult);
                        return true;            // sent trails to server successfully
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;           //could not send trails to server

                    }
                }
            }
            return false;
        }

        protected void onPostExecute(Boolean results) {
            if (MainActivity.getUsername() == null || MainActivity.getSession() == -1) {
                Toast toast = Toast.makeText(mContext,
                        "Please login to use cloud functionality",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else if (results == true) {
                Toast toast = Toast.makeText(mContext,
                        "Successfully uploaded trail to server!",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                Toast toast = Toast.makeText(mContext,
                        "Could not send to server, try again later",
                        Toast.LENGTH_LONG);
                toast.show();

            }
        }
    }




}
