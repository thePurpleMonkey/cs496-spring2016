package com.best_slopes.bestslopes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by Michael Humphrey on 4/20/2016.
 */
public final class DatabaseContract {
    public DatabaseContract() {
    }

    public static class TrailContract extends SQLiteOpenHelper implements BaseColumns {
        public static final int DATABASE_VERSION = 4;
        public static final String DATABASE_NAME = "trails.db";
        public static final String TABLE_NAME = "trails";
        public static final String IMAGE_TABLE_NAME = "images";
        public static final String COMMENTS_TABLE_NAME = "comments";

        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DIFFICULTY = "difficulty";
        public static final String COLUMN_NAME_RATING = "rating";

        public static final String COLUMN_NAME_FILENAME = "filename";
        public static final String COLUMN_NAME_TRAIL_ID = "trail";

        public static final String COLUMN_NAME_COMMENT = "comment";

        public static final int INVALID_DIFFICULTY = -1;
        public static final int EASY_DIFFICULTY = 1;
        public static final int MEDIUM_DIFFICULTY = 2;
        public static final int DIFFICULT_DIFFICULTY = 3;
        public static final int EXTREME_DIFFICULTY = 4;

        private static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + TrailContract.TABLE_NAME + " (" +
                        TrailContract._ID + " INTEGER PRIMARY KEY, " +
                        TrailContract.COLUMN_NAME_TITLE + " TEXT, " +
                        TrailContract.COLUMN_NAME_DIFFICULTY + " INTEGER, " +
                        TrailContract.COLUMN_NAME_RATING + " INTEGER)";

        private static final String SQL_DELETE_TRAILS =
                "DROP TABLE IF EXISTS " + TrailContract.TABLE_NAME;

        private static final String SQL_CREATE_IMAGE_TABLE =
                "CREATE TABLE " + TrailContract.IMAGE_TABLE_NAME + " (" +
                        "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailContract.COLUMN_NAME_FILENAME + " TEXT, " +
                        TrailContract.COLUMN_NAME_TRAIL_ID + " INTEGER, " +
                        "FOREIGN KEY(" + TrailContract.COLUMN_NAME_TRAIL_ID + ") REFERENCES " +
                        TrailContract.TABLE_NAME + "(" + TrailContract._ID + "))";
        private static final String SQL_DELETE_IMAGE_TABLE =
                "DROP TABLE IF EXISTS " + TrailContract.IMAGE_TABLE_NAME;

        private static final String SQL_CREATE_COMMENTS_TABLE =
                "CREATE TABLE " + TrailContract.COMMENTS_TABLE_NAME + " (" +
                        "_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TrailContract.COLUMN_NAME_COMMENT + " TEXT, " +
                        TrailContract.COLUMN_NAME_TRAIL_ID + " INTEGER, " +
                        "FOREIGN KEY(" + TrailContract.COLUMN_NAME_TRAIL_ID + ") REFERENCES " +
                        TrailContract.TABLE_NAME + "(" + TrailContract._ID + "))";
        private static final String SQL_DELETE_COMMENTS_TABLE =
                "DROP TABLE IF EXISTS " + TrailContract.COMMENTS_TABLE_NAME;


        public TrailContract(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_TABLE);
            db.execSQL(SQL_CREATE_IMAGE_TABLE);
            db.execSQL(SQL_CREATE_COMMENTS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_TRAILS);
            db.execSQL(SQL_DELETE_IMAGE_TABLE);
            db.execSQL(SQL_DELETE_COMMENTS_TABLE);

            onCreate(db);
        }
    }

    public static class SaveTrailTask extends AsyncTask<Trail, Void, Void> {
        Trail trail;
        private Context mContext;

        public SaveTrailTask (Context mContext) {
            this.mContext = mContext;
        }

        protected Void doInBackground(Trail... params) {
            if (params.length < 1) {
                Log.e("Database", "Insufficient parameters. Expected 1, got " + params.length);
                return null;
            } else {
                trail = params[0];
            }

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TrailContract.COLUMN_NAME_DIFFICULTY, trail.getDifficulty());
            values.put(TrailContract.COLUMN_NAME_RATING, trail.getRating());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.insert(
                    TrailContract.TABLE_NAME,
                    "null",
                    values);

            trail.setId((int) newRowId);

            // Save image paths to database
            for (String path : trail.getImagePaths()) {
                values = new ContentValues();
                values.put(TrailContract.COLUMN_NAME_FILENAME, path);
                values.put(TrailContract.COLUMN_NAME_TRAIL_ID, trail.getId());

                db.insert(
                        TrailContract.IMAGE_TABLE_NAME,
                        "null",
                        values);
            }

            // Save comments to database
            for (String comment : trail.getComments()) {
                values = new ContentValues();
                values.put(TrailContract.COLUMN_NAME_COMMENT, comment);
                values.put(TrailContract.COLUMN_NAME_TRAIL_ID, trail.getId());

                db.insert(
                        TrailContract.COMMENTS_TABLE_NAME,
                        "null",
                        values);
            }

            return null;
        }
    }

    public static class LoadTask extends AsyncTask<Void, Void, Trail[]> {
        private Context mContext;
        private Trail[] results;

        public LoadTask (Context context){
            mContext = context;
        }

        @Override
        protected Trail[] doInBackground(Void... params) {
            int index = 0;

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TrailContract._ID,
                    TrailContract.COLUMN_NAME_TITLE,
                    TrailContract.COLUMN_NAME_DIFFICULTY,
                    TrailContract.COLUMN_NAME_RATING,
            };

            // How you want the results sorted in the resulting Cursor
            //TODO: add app functionality to sort by different methods
            String sortOrder =
                    TrailContract.COLUMN_NAME_DIFFICULTY + " DESC";

            Cursor c = db.query(
                    TrailContract.TABLE_NAME,        // The table to query
                    projection,                               // The columns to return
                    null,                                     // The columns for the WHERE clause
                    null,                                     // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            results = new Trail[c.getCount()];

            c.moveToFirst();
            while (!c.isAfterLast()) {
                Trail trail = new Trail();

                trail.setId(c.getColumnIndexOrThrow(TrailContract._ID));
                trail.setName(c.getString(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_TITLE)));
                trail.setDifficulty(c.getInt(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_DIFFICULTY)));
                trail.setRating((float) (c.getInt(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_RATING))/2.0));

                Cursor commentCursor = db.query(
                        TrailContract.COMMENTS_TABLE_NAME,
                        new String[] {TrailContract.COLUMN_NAME_COMMENT},
                        TrailContract.COLUMN_NAME_TRAIL_ID + " = ?",
                        new String[] { String.valueOf(trail.getId()) },
                        null,
                        null,
                        null
                );

                commentCursor.moveToFirst();
                while (!commentCursor.isAfterLast()) {
                    trail.addComment(commentCursor.getString(commentCursor.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_COMMENT)));
                    commentCursor.moveToNext();
                }

                Cursor imageCursor = db.query(
                        TrailContract.IMAGE_TABLE_NAME,
                        new String[] {TrailContract.COLUMN_NAME_FILENAME},
                        TrailContract.COLUMN_NAME_TRAIL_ID + " = ?",
                        new String[] {String.valueOf((trail.getId()))},
                        null,
                        null,
                        null
                );

                imageCursor.moveToFirst();
                while (!imageCursor.isAfterLast()) {
                    trail.addImagePath(imageCursor.getString(imageCursor.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_FILENAME)));
                    imageCursor.moveToNext();
                }

                results[index] = trail;
                c.moveToNext();
                index++;
            }

            return results;
        }

        public Trail[] getResults() {
            return results;
        }
    }

    public static class LoadTrailTask extends AsyncTask<String, Void, Trail> {
        private Context mContext;
        private Trail result;
        private String id;

        public LoadTrailTask (Context context){
            mContext = context;
        }

        @Override
        protected Trail doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            } else {
                id = params[0];
            }

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TrailContract._ID,
                    TrailContract.COLUMN_NAME_TITLE,
                    TrailContract.COLUMN_NAME_DIFFICULTY,
                    TrailContract.COLUMN_NAME_RATING,
            };

            Cursor c = db.query(
                    TrailContract.TABLE_NAME,                 // The table to query
                    projection,                               // The columns to return
                    "_id = ?",                                  // The columns for the WHERE clause
                    new String[] {id},     // The values for the WHERE clause
                    null,                                     // Don't group the rows
                    null,                                     // Don't filter by row groups
                    null                                      // Don't sort
            );

            if (c.getCount() < 1) {
                Log.d("LoadTrailTask", "No result found");
                return null;
            } else {
                c.moveToFirst();

                result = new Trail();
                result.setId(c.getInt(c.getColumnIndexOrThrow(TrailContract._ID)));
                result.setName(c.getString(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_TITLE)));
                result.setDifficulty(c.getInt(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_DIFFICULTY)));
                result.setRating((float) (c.getInt(c.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_RATING)) / 2.0));

                Cursor cc = db.query(
                        TrailContract.IMAGE_TABLE_NAME,
                        new String[] {},
                        TrailContract.COLUMN_NAME_TRAIL_ID + " = ?",
                        new String[] {String.valueOf(result.getId())},
                        null,
                        null,
                        null
                );

                Cursor imageCursor = db.query(
                        TrailContract.IMAGE_TABLE_NAME,
                        new String[] {TrailContract.COLUMN_NAME_FILENAME},
                        TrailContract.COLUMN_NAME_TRAIL_ID + " = ?",
                        new String[] {String.valueOf((result.getId()))},
                        null,
                        null,
                        null
                );

                imageCursor.moveToFirst();
                while (!imageCursor.isAfterLast()) {
                    result.addImagePath(imageCursor.getString(imageCursor.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_FILENAME)));
                    imageCursor.moveToNext();
                }

                cc.moveToFirst();
                while (!cc.isAfterLast()) {
                    result.addImagePath(cc.getString(cc.getColumnIndexOrThrow(TrailContract.COLUMN_NAME_FILENAME)));
                }

                return result;
            }
        }

        public Trail getResult() {
            return result;
        }
    }

    public static class AttachImageTask extends AsyncTask<String, Void, Long> {
        private Context mContext;
        private int id;
        private String filename;

        public AttachImageTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Long doInBackground(String... params) {
            if (params.length < 2) {
                return null;
            } else {
                try {
                    id = Integer.parseInt(params[0]);
                    filename = params[1];
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TrailContract.COLUMN_NAME_FILENAME, filename);
            values.put(TrailContract.COLUMN_NAME_TRAIL_ID, id);

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    TrailContract.IMAGE_TABLE_NAME,
                    "null",
                    values);

            return newRowId;
        }
    }

    public static class DeleteTrailTask extends AsyncTask<Long, Void, Void> {
        private Context mContext;
        private long id;

        public DeleteTrailTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(Long... params) {
            if (params.length < 1) {
                return null;
            } else {
                try {
                    id = params[0];
                } catch (NumberFormatException e) {
                    return null;
                }
            }

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // Define 'where' part of query.
            String selection = TrailContract._ID + " LIKE ?";

            // Specify arguments in placeholder order.
            String[] selectionArgs = { String.valueOf(id) };

            // Issue SQL statement.
            db.delete(TrailContract.TABLE_NAME, selection, selectionArgs);

            return null;
        }
    }

    public static class UpdateTrailTask extends AsyncTask<Trail, Void, Void> {
        private Context mContext;
        private Trail trail;

        public UpdateTrailTask(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        protected Void doInBackground(Trail... params) {
            if (params.length < 1) {
                Log.e("Database", "Insufficient parameters. Expected 1, got " + params.length);
                return null;
            } else {
                trail = params[0];
            }

            TrailContract mDbHelper = new TrailContract(mContext);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            // New value for one column
            ContentValues values = new ContentValues();
            values.put(TrailContract.COLUMN_NAME_TITLE, trail.getName());
            values.put(TrailContract.COLUMN_NAME_RATING, trail.getRating());
            values.put(TrailContract.COLUMN_NAME_DIFFICULTY, trail.getDifficulty());

            // Which row to update, based on the ID
            String selection = TrailContract._ID + " = ?";
            String[] selectionArgs = { String.valueOf(trail.getId()) };

            int count = db.update(
                    TrailContract.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);

            // Define 'where' part of query.
            selection = TrailContract.COLUMN_NAME_TRAIL_ID + " = ?";
            // Specify arguments in placeholder order.
            selectionArgs[0] = String.valueOf(trail.getId());

            // Issue SQL statement.
            db.delete(TrailContract.IMAGE_TABLE_NAME, selection, selectionArgs);

            // Save image paths to database
            for (String path : trail.getImagePaths()) {
                values = new ContentValues();
                values.put(TrailContract.COLUMN_NAME_FILENAME, path);
                values.put(TrailContract.COLUMN_NAME_TRAIL_ID, trail.getId());

                db.insert(
                        TrailContract.IMAGE_TABLE_NAME,
                        "null",
                        values);
            }

            db.delete(TrailContract.COMMENTS_TABLE_NAME, TrailContract.COLUMN_NAME_TRAIL_ID + " = ?",
                    new String[] {String.valueOf(trail.getId())});

            for (String comment : trail.getComments()) {
                values = new ContentValues();
                values.put(TrailContract.COLUMN_NAME_COMMENT, comment);
                values.put(TrailContract.COLUMN_NAME_TRAIL_ID, trail.getId());

                db.insert(
                        TrailContract.COMMENTS_TABLE_NAME,
                        "null",
                        values);
            }

            return null;
        }
    }
}