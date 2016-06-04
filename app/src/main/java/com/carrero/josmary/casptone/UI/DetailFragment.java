package com.carrero.josmary.casptone.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carrero.josmary.casptone.Data.EventsContract;
import com.carrero.josmary.casptone.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";

    private static final String EVENT_SHARE_HASHTAG = "#FirstEverythingsApp";

    private ShareActionProvider mShareActionProvider;
    private String mEvent;
    private Uri mUri;
    private String mEventId;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            EventsContract.EventsEntry._ID,
            EventsContract.EventsEntry.COLUMN_ENAME,
            EventsContract.EventsEntry.COLUMN_EDATE,
            EventsContract.EventsEntry.COLUMN_ENOTES
    };

    // These indices are tied to EVENT_COLUMNS
    static final int COL_EVENT_ID = 0;
    static final int COL_EVENT_NAME = 1;
    static final int COL_EVENT_DATE = 2;
    static final int COL_EVENT_NOTES = 3;

    private TextView mEventNameView;
    private TextView mEventDateView;
    private TextView mEventNotesView;
    private TextView mEventIdView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mEventNameView = (TextView) rootView.findViewById(R.id.detail_event_name);
        mEventDateView = (TextView) rootView.findViewById(R.id.detail_event_date);
        mEventNotesView = (TextView) rootView.findViewById(R.id.detail_event_notes);
        mEventIdView = (TextView) rootView.findViewById(R.id.detail_event_id);

        Toolbar dToolbar = (Toolbar) rootView.findViewById(R.id.detail_toolbar);
        if(dToolbar != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(dToolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button mButton = (Button) rootView.findViewById(R.id.btnShowPhotos);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ((com.carrero.josmary.casptone.UI.DetailActivity) getActivity()).showEventPhotos(mEventIdView.getText().toString());
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.menu_share);

        // Get the provider and hold onti it to set/change the share intent
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, set the share intent now
        if (mEvent != null){
            mShareActionProvider.setShareIntent(createShareEventIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.menu_share){
            // handled by ShareActionProvider
        }
        else if (id ==  R.id.menu_delete){
            confirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    // delete confirmation
    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setMessage("Delete this event?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        deleteEvent();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void deleteEvent(){

        int rowsDeleted;
        // find and delete associated images in pics table where event_id = eId
        String s = "event_id = " + mEventId;
        rowsDeleted = getActivity().getContentResolver().delete(EventsContract.PicsEntry.CONTENT_URI, s, null);
        // delete the event
        s = "_id = " + mEventId;
        rowsDeleted = getActivity().getContentResolver().delete(EventsContract.EventsEntry.CONTENT_URI, s, null);
        if (rowsDeleted > 0)
            Toast.makeText(getContext(), "Event deleted!", Toast.LENGTH_SHORT).show();

        // go back to Main
        Intent i=new Intent(getContext(), com.carrero.josmary.casptone.UI.MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    private Intent createShareEventIntent () {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mEvent + EVENT_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        if (null != mUri){

            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        if (data != null && data.moveToFirst()){
            String eName = data.getString(COL_EVENT_NAME);
            mEventNameView.setText(eName);
            String eDate = data.getString(COL_EVENT_DATE);
            mEventDateView.setText(eDate);
            String eNotes = data.getString(COL_EVENT_NOTES);
            mEventNotesView.setText(eNotes);
            mEventId = data.getString(COL_EVENT_ID);
            mEventIdView.setText(mEventId);

            // need this for the share intent
            mEvent = String.format("%s: %s ", eName, eNotes);

            // if onCreateOptionsMenu has already happened, update the share intent now
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareEventIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){}

}
