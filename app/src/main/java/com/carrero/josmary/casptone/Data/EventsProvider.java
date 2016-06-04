package com.carrero.josmary.casptone.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by x on 2/28/2016.
 */
public class EventsProvider extends ContentProvider{

    private com.carrero.josmary.casptone.Data.EventsDBHelper mOpenHelper;

    private static final int EVENT = 100;
    private static final int EVENT_ID = 101;
    private static final int PICS = 200;
    private static final int PICS_ID = 201;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    static UriMatcher buildUriMatcher() {
        String content = com.carrero.josmary.casptone.Data.EventsContract.CONTENT_AUTHORITY;

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, com.carrero.josmary.casptone.Data.EventsContract.PATH_EVENT, EVENT);
        matcher.addURI(content, com.carrero.josmary.casptone.Data.EventsContract.PATH_EVENT + "/#", EVENT_ID);
        matcher.addURI(content, com.carrero.josmary.casptone.Data.EventsContract.PATH_PICS, PICS);
        matcher.addURI(content, com.carrero.josmary.casptone.Data.EventsContract.PATH_PICS + "/#", PICS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new com.carrero.josmary.casptone.Data.EventsDBHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case EVENT:
                return com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.CONTENT_TYPE;
            case EVENT_ID:
                return com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.CONTENT_ITEM_TYPE;
            case PICS:
                return com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.CONTENT_TYPE;
            case PICS_ID:
                return com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;
        Long _id;

        switch(sUriMatcher.match(uri)){
            case EVENT:
                retCursor = db.query(
                        com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case EVENT_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.TABLE_NAME,
                        projection,
                        com.carrero.josmary.casptone.Data.EventsContract.EventsEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case PICS:
                retCursor = db.query(
                        com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PICS_ID:
                _id = ContentUris.parseId(uri);
                retCursor = db.query(
                        com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME,
                        projection,
                        com.carrero.josmary.casptone.Data.EventsContract.PicsEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        //retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;

        //normalizeDate(values);
        switch(sUriMatcher.match(uri)){
            case EVENT:
                rowsUpdated = db.update(com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PICS:
                rowsUpdated = db.update(com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        Long _id;

        switch(sUriMatcher.match(uri)){
            case EVENT:
                _id = db.insert(com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri =  com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.buildEventsUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert row into: " + uri);
                }
                break;
            case PICS:
                _id = db.insert(com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri =  com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.buildPicsUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert row into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch(sUriMatcher.match(uri)){
            case EVENT:
                rowsDeleted = db.delete(
                        com.carrero.josmary.casptone.Data.EventsContract.EventsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PICS:
                rowsDeleted = db.delete(
                        com.carrero.josmary.casptone.Data.EventsContract.PicsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

}
