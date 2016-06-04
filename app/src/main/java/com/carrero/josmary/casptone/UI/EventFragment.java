package com.carrero.josmary.casptone.UI;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carrero.josmary.casptone.Data.EventsContract;
import com.carrero.josmary.casptone.R;


/**
 * A fragment representing a list of events.
 */
public class EventFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public final String LOG_TAG = EventFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected position";
    private static final int EVENT_LOADER = 0;


    private static final String[] EVENT_COLUMNS = {
            EventsContract.EventsEntry._ID,
            EventsContract.EventsEntry.COLUMN_ENAME,
            EventsContract.EventsEntry.COLUMN_EDATE,
            EventsContract.EventsEntry.COLUMN_ELOC,
            EventsContract.EventsEntry.COLUMN_ENOTES
    };

    // These indices are tied to EVENT_COLUMNS
    static final int COL_EVENT_ID = 0;
    static final int COL_EVENT_NAME = 1;
    static final int COL_EVENT_DATE = 2;
    static final int COL_EVENT_LOC = 3;
    static final int COL_EVENT_NOTES = 4;


    public interface Callback {
        //DetailFragmentCallback for when an item has been selected
        void onItemSelected(Uri dateUri);
    }

    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        getLoaderManager().initLoader(EVENT_LOADER, null, this);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.listview_events);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        if (mPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String sortOrder = EventsContract.EventsEntry.COLUMN_EDATE + " DESC";
        Uri eventsUri = EventsContract.EventsEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                    eventsUri,
                    EVENT_COLUMNS,
                    null,
                    null,
                    sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Adapter adapter = new Adapter(cursor);
        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            Toast.makeText(getContext(), "No events found!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {
        private Cursor mCursor;

        public Adapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(COL_EVENT_ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.list_item_event, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = vh.getAdapterPosition();
                    final long eID = getItemId(position);
                    if (position != RecyclerView.NO_POSITION) {
                        ((Callback) getActivity())
                                .onItemSelected(EventsContract.EventsEntry
                                        .buildEvent(eID));
                    }
               }
          });
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);
            holder.eventView.setText(mCursor.getString(1));
            holder.dateView.setText(mCursor.getString(2));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView eventView;
        public final TextView dateView;

        public ViewHolder(View view) {
            super(view);
            eventView = (TextView) view.findViewById(R.id.event_title);
            dateView = (TextView) view.findViewById(R.id.event_date);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
