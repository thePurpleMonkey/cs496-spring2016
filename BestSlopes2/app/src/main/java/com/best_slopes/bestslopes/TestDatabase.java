package com.best_slopes.bestslopes;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TestDatabase extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);

        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DatabaseContract.Trail mDbHelper = new DatabaseContract.Trail(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    DatabaseContract.Trail._ID,
                    DatabaseContract.Trail.COLUMN_NAME_TITLE,
                    DatabaseContract.Trail.COLUMN_NAME_DIFFICULTY,
                    DatabaseContract.Trail.COLUMN_NAME_RATING,
                    DatabaseContract.Trail.COLUMN_NAME_COMMENTS
            };

// How you want the results sorted in the resulting Cursor
            String sortOrder =
                    DatabaseContract.Trail.COLUMN_NAME_RATING + " DESC";

            Cursor c = db.query(
                    DatabaseContract.Trail.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            c.moveToFirst();

            while (!c.isAfterLast()) {
                String row = "";

                row += "Name: " + (c.getString(c.getColumnIndexOrThrow(DatabaseContract.Trail.COLUMN_NAME_TITLE)));
                row += ", Difficulty: " + c.getInt(c.getColumnIndexOrThrow(DatabaseContract.Trail.COLUMN_NAME_DIFFICULTY));
                row += ", Rating: " + c.getInt(c.getColumnIndexOrThrow(DatabaseContract.Trail.COLUMN_NAME_RATING));
                row += ", Comments: " + c.getString(c.getColumnIndexOrThrow(DatabaseContract.Trail.COLUMN_NAME_COMMENTS));

                publishProgress(row);
                c.moveToNext();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d("Test", values[0]);
            LinearLayout myLayout = (LinearLayout) findViewById(R.id.test_database_layout);

            TextView text = new TextView(getApplicationContext());
            text.setText(values[0]);
            text.setTextColor(Color.RED);
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            myLayout.addView(text);
        }
    }
}
