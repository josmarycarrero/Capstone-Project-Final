package com.carrero.josmary.casptone.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.carrero.josmary.casptone.Data.EventsContract.EventsEntry;
import com.carrero.josmary.casptone.Data.EventsContract.PicsEntry;

/**
 * Created by x on 2/28/2016.
 */
public class EventsDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 8;
    public static String DATABASE_PATH = "/data/data/com.carrero.josmary.casptone/databases/";
    public static final String DATABASE_NAME = "before.db";

    public EventsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public  int countEvents(){
        int count =0;
        String sql = "SELECT COUNT(*) FROM EVENTS";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            count = cursor.getInt(0);

        }
        return  count;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create Events table
        final String SQL_CREATE_EVENT_TABLE =
                "CREATE TABLE " + EventsEntry.TABLE_NAME + " (" +
                        EventsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        EventsEntry.COLUMN_ENAME + " TEXT NOT NULL, " +
                        EventsEntry.COLUMN_EDATE + " TEXT, " +
                        EventsEntry.COLUMN_ELOC + " TEXT, " +
                        EventsEntry.COLUMN_ENOTES + " TEXT " +
                        ")";
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);

        // create Pics table
        final String SQL_CREATE_PICS_TABLE =
                "CREATE TABLE " + com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME + " (" +
                        PicsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PicsEntry.COLUMN_PIC + " TEXT, " +
                        PicsEntry.COLUMN_PIC_NOTES + " TEXT, " +
                        PicsEntry.COLUMN_EVENT_ID + " INTEGER, " +
                        "FOREIGN KEY (" + PicsEntry.COLUMN_EVENT_ID + ") REFERENCES " +
                        EventsEntry.TABLE_NAME + "(" + EventsEntry._ID + "))";
        sqLiteDatabase.execSQL(SQL_CREATE_PICS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EventsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PicsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
