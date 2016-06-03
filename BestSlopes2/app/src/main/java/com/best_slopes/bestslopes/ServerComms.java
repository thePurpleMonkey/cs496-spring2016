package com.best_slopes.bestslopes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.best_slopes.bestslopes.http.HttpGet;
import com.best_slopes.bestslopes.http.HttpPost;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by pkess_000 on 5/21/2016.
 */
public class ServerComms {
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

            try {
                HttpGet getTrails = new HttpGet(Constants.TRACKER_URL, Constants.CHARSET);
                getTrails.addFormField("owner_id", MainActivity.getUsername());
                String valResult = getTrails.finish();
                JSONArray jsonArray = new JSONArray(valResult);

                String get;
                JSONObject jsonObject;

                for(int i = 0; i< jsonArray.length(); i++){
                    Trail temp_trail = new Trail();
                    jsonObject = jsonArray.getJSONObject(i);        //TODO: parse the id, maybe don't have to.

                    temp_trail.setServerID(jsonObject.getString("id"));

                    temp_trail.setName(jsonObject.getString("title"));
                    temp_trail.setDifficulty(Integer.parseInt(jsonObject.getString("difficulty")));
                    temp_trail.setRating(Integer.parseInt(jsonObject.getString("rating")));

                    //Parse comments from server by commas
                    String[] commentArray = ((jsonObject.getString("comment").split(",")));
                    ArrayList<String> commentList = new ArrayList<>();

                    //iterate over all strings after split
                    for(String s : commentArray) {
                        if (!s.equals(Constants.NULL_STR))     //null when there is no comment
                            commentList.add(s);     //add to temp commentList to set later
                    }
                    temp_trail.setComment(commentList);     //set comments from server to phone.

                    trails.add(temp_trail);     //adds generated trial to ArrayList
                }

            } catch(Exception e){
                Log.e("Async get trails exc.", e.toString());
                return null;
            }

            return trails;
        }

        protected void onPostExecute(ArrayList<Trail> trails){
            if(trails == null) {
                swipeLayout.setRefreshing(false);

                Toast toast = Toast.makeText(mContext,
                        "Could not load trails, try again later",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else{
                swipeLayout.setRefreshing(false);

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
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            if(param.length < 0){
                Log.e("Send Single To Server", "array_trail was empty, couldn't send");
                return null;
            }

            Trail trail = param[0];

            sendTrails.addFormField("id", trail.getServerID());
            sendTrails.addFormField("owner_id", MainActivity.getUsername());        //TODO: don't hardcode ownerID
            sendTrails.addFormField("title", trail.getName());
            sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
            sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));
            sendTrails.addFormField("delete_trail","0");    //indicates do not delete trail

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
                return true;            // sent trails to server successfully
            } catch (Exception e) {
                e.printStackTrace();
                return false;           //could not send trails to server
            }
        }

        protected void onPostExecute(Boolean results) {
            if (results == true) {
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

        protected Boolean doInBackground(Trail... param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

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

        protected void onPostExecute(Boolean results) {
            if (results == true) {
//                mainActivity.load
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

        protected Boolean doInBackground(ArrayList<Trail>... array_param) {
            HttpPost sendTrails = new HttpPost(Constants.TRACKER_URL, Constants.CHARSET);

            if(array_param.length < 0){
                Log.e("Send All To Server", "array_trail was empty, couldn't send");
                return null;
            }

            for(int i = 0; i< array_param[0].size(); i++) {
                Trail trail = array_param[0].get(i);

                sendTrails.addFormField("id", trail.getServerID());        //todo, change this to use ownerID
                sendTrails.addFormField("owner_id", MainActivity.getUsername());        //TODO: don't hardcode ownerID
                sendTrails.addFormField("title", trail.getName());
                sendTrails.addFormField("rating", Integer.toString((int) trail.getRating()));
                sendTrails.addFormField("difficulty", Integer.toString((int) trail.getDifficulty()));
                sendTrails.addFormField("delete_trail","0");    //indicates do not delete trail

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
                    return true;            // sent trails to server successfully
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;           //could not send trails to server

                }
            }
            return false;

        }
        protected void onPostExecute(Boolean results) {
            if (results == true) {
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
