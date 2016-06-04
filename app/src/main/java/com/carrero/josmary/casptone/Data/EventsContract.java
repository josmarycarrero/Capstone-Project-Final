package com.carrero.josmary.casptone.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by x on 2/28/2016.
 */
public class EventsContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.carrero.josmary.capstone";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "events";
    public static final String PATH_PICS = "pics";

    private EventsContract() {
    }


    /*
    Inner class that defines the contents of the events table
    */
    public static final class EventsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "events";
        public static final String COLUMN_ENAME = "e_name";
        public static final String COLUMN_EDATE = "e_date";
        public static final String COLUMN_ELOC = "e_loc";
        public static final String COLUMN_ENOTES = "e_notes";

        public static Uri buildEventsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEvent(long eID) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(eID)).build();
        }
    }

    /*
    Inner class that defines the contents of the pics table
    */
    public static final class PicsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PICS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PICS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PICS;

        public static final String TABLE_NAME = "pics";
        public static final String COLUMN_EVENT_ID = "event_id";
        public static final String COLUMN_PIC = "pic";
        public static final String COLUMN_PIC_NOTES = "pic_notes";

        public static Uri buildPicsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
