package zokscore.com.mobile.patternrecognition.db;

/**
 * Created by pafgoncalves on 12-04-2018.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "actividades";

    // Labels table name
    private static final String TABLE_ACTIVITIES = "actividades";

    // Labels Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_ACTIVITY = "actividade";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_ACTIVITIES_TABLE = "CREATE TABLE " + TABLE_ACTIVITIES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ACTIVITY + " TEXT)";
        db.execSQL(CREATE_ACTIVITIES_TABLE);
        String INSERT_ACTIVITIES_TABLE = "INSERT INTO " + TABLE_ACTIVITIES + "("
                + KEY_ID + " ," + KEY_ACTIVITY + ") VALUES ";
        INSERT_ACTIVITIES_TABLE += "  (1, 'None')";
        INSERT_ACTIVITIES_TABLE += ", (2, 'Walk')";
        INSERT_ACTIVITIES_TABLE += ", (3, 'Run')";
        INSERT_ACTIVITIES_TABLE += ", (4, 'Jump')";

        INSERT_ACTIVITIES_TABLE += ";";
        db.execSQL(INSERT_ACTIVITIES_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);

        // Create tables again
        onCreate(db);
    }

    /**
     * Inserting new lable into lables table
     * */
    public void insertActivity(String activity){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ACTIVITY, activity);

        // Inserting Row
        db.insert(TABLE_ACTIVITIES, null, values);
        db.close(); // Closing database connection
    }

    public void deleteActivity(String activity){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_ACTIVITIES, KEY_ACTIVITY+"=?",new String[] {activity});
        db.close(); // Closing database connection
    }

    /**
     * Getting all labels
     * returns list of labels
     * */
    public List<String> getAllActivities(){
        List<String> activities = new ArrayList<String>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACTIVITIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                activities.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return activities;
    }
}

