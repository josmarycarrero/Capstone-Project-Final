package com.carrero.josmary.casptone.UI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.carrero.josmary.casptone.Data.EventsContract;
import com.carrero.josmary.casptone.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;


public class AddActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mEventName;
    DatePicker mEventDate;
    TextView mEventNotes;

    private LinearLayout lnrImages;
    private ArrayList<String> imagesPathList;
    private final int PICK_IMAGE_MULTIPLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mEventName = (TextView) findViewById(R.id.eventName);
        mEventDate = (DatePicker) findViewById(R.id.eventDate);
        mEventNotes = (TextView) findViewById(R.id.eventNotes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
        }

        lnrImages = (LinearLayout)findViewById(R.id.lnrImages);
        Button btnAddPhots = (Button)findViewById(R.id.btnAddPhots);
        btnAddPhots.setOnClickListener(this);


        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Add Photos button
            case R.id.btnAddPhots:
                Intent intent = new Intent(AddActivity.this, com.carrero.josmary.casptone.UI.CustomPhotoGalleryActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent,PICK_IMAGE_MULTIPLE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == PICK_IMAGE_MULTIPLE){
                imagesPathList = new ArrayList<String>();
                String[] imagesPath = data.getStringExtra("data").split("\\|");
                try{
                    lnrImages.removeAllViews();
                }catch (Throwable e){
                    e.printStackTrace();
                }
                for (int i=0;i<imagesPath.length;i++){

                    // imagesPathList needed for saving pics in db
                    imagesPathList.add(imagesPath[i]);
                    // display images
                    ImageView imageView = new ImageView(this);
                    File file = new File(imagesPath[i]);
                    Glide
                            .with(getBaseContext())
                            .load(file)
                            .into(imageView);
                    lnrImages.addView(imageView);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.menu_add_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.save_event){

            if (mEventName.length() == 0){
                Toast.makeText(this, "Add an event name first!", Toast.LENGTH_LONG).show();
                return false;
            }

            // insert event into events table
            Uri eventUri = insertEvent();
            long eventRowId = ContentUris.parseId(eventUri);
            // if successful
            if (eventRowId > 0) {
                // if images were selected for this event too
                if (null != imagesPathList && imagesPathList.size() > 0) {
                    // add selected photos to pics table
                    for (String p : imagesPathList) {
                        insertPic(p, eventRowId);
                    }
                }
                Toast.makeText(this, "Event added!", Toast.LENGTH_LONG).show();
                // return to Main
                Intent intent = new Intent(this, com.carrero.josmary.casptone.UI.MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, "ERROR ADDING EVENT!", Toast.LENGTH_LONG).show();
            }
        }
        else if (id == R.id.cancel_event){

            Intent intent = new Intent(this, com.carrero.josmary.casptone.UI.MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }


    public Uri insertEvent(){

        // add event data to events table
        ContentValues eventDataValues = getEventData();
        // Uri eventInsertUri = getContentResolver().insert(EventsContract.EventsEntry.CONTENT_URI, eventDataValues);
        return getContentResolver().insert(EventsContract.EventsEntry.CONTENT_URI, eventDataValues);
    }

    public void insertPic(String p, long eID){

        // add photos to pics table with foreign key
        ContentValues contentValues = new ContentValues();
        contentValues.put(EventsContract.PicsEntry.COLUMN_EVENT_ID, eID);
        contentValues.put(EventsContract.PicsEntry.COLUMN_PIC, p);
        getContentResolver().insert(EventsContract.PicsEntry.CONTENT_URI, contentValues);
    }

    private ContentValues getEventData(){
        ContentValues contentValues = new ContentValues();

        String eventName = mEventName.getText().toString();
        String eventDate = getEDateFormatted();
        String eventNotes = mEventNotes.getText().toString();
        contentValues.put(EventsContract.EventsEntry.COLUMN_ENAME, eventName);
        contentValues.put(EventsContract.EventsEntry.COLUMN_EDATE, eventDate);
        contentValues.put(EventsContract.EventsEntry.COLUMN_ENOTES, eventNotes);
        return contentValues;
    }


    private String getEDateFormatted(){
        int   d = mEventDate.getDayOfMonth();
        int   m = mEventDate.getMonth();
        int   y = mEventDate.getYear();
        return ""+ (m + 1) + "/" + d + "/" + y;
    }

}
