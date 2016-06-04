package com.carrero.josmary.casptone.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.carrero.josmary.casptone.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(com.carrero.josmary.casptone.UI.DetailFragment.DETAIL_URI, getIntent().getData());

            com.carrero.josmary.casptone.UI.DetailFragment fragment = new com.carrero.josmary.casptone.UI.DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    public void showEventPhotos(String eID) {

        Intent i = new Intent(this, com.carrero.josmary.casptone.UI.ShowPhotosActivity.class);
        i.putExtra("eventID", eID);
        startActivity(i);
    }

}
