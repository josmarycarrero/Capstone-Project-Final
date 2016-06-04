package com.carrero.josmary.casptone.UI;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carrero.josmary.casptone.Data.EventsContract;
import com.carrero.josmary.casptone.R;

import java.io.File;
import java.util.ArrayList;

public class ShowPhotosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static int NUM_ITEMS;
    private String mID;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;

    private static final int PICS_LOADER = 0;
    public static ArrayList<String> imagePaths = new ArrayList<>();

    private static final String[] PICS_COLUMNS = {
            EventsContract.PicsEntry._ID,
            EventsContract.PicsEntry.COLUMN_EVENT_ID,
            EventsContract.PicsEntry.COLUMN_PIC
    };

    // These indices are tied to EVENT_COLUMNS
    static final int COL_PIC_ID = 0;
    static final int COL_EVENT_ID = 1;
    static final int COL_PIC = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ) {
            imagePaths = savedInstanceState.getStringArrayList("imgPaths");
        }

        setContentView(R.layout.fragment_pager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.show_photos_toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("First Everythings");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportLoaderManager().initLoader(PICS_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putStringArrayList("imgPaths", imagePaths);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        mID = getIntent().getStringExtra("eventID");

        String selection = EventsContract.PicsEntry.COLUMN_EVENT_ID + " = '"
                + mID + "'";
        Uri picsUri = EventsContract.PicsEntry.CONTENT_URI;
        return new CursorLoader(
                this,          // parent activity context
                picsUri,       // table to query
                PICS_COLUMNS,  // project to return
                selection,     // selection
                null,          // selection arguments
                null);         // default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        NUM_ITEMS = cursor.getCount();

        if (NUM_ITEMS == 0){
            Toast.makeText(this, "No pictures found!", Toast.LENGTH_LONG).show();
            // kill activity
            this.finish();
        }
        else {
            // get image paths
            cursor.moveToFirst();
            imagePaths.clear();
            while (!cursor.isAfterLast()) {
                imagePaths.add(cursor.getString(2));
                cursor.moveToNext();
            }
            cursor.close();

            // set the adapter since there are images to show
            imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
            viewPager = (ViewPager) findViewById(R.id.pager);
            viewPager.setAdapter(imageFragmentPagerAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}


    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);

            ImageView imageView = (ImageView) swipeView.findViewById(R.id.imageView);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            File file = new File(imagePaths.get(position));
            Glide
                    .with(getContext())
                    .load(file)
                    .into(imageView);

            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }
}
