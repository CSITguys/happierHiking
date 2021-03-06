package com.csitguys.happierhiking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

//Hike Display page shows user information about the hike EX. name, description, rating, difficulty REQUIREMENT #8
public class HikeDisplay extends AppCompatActivity implements View.OnClickListener {

    //Create button's for the page's buttons
    Button button1;
    Button button2;
    
    // variables for hike chosen
    Hike mHike;
    double mStartLat;
    double mStartlng;
    double mEndLat;
    double mEndLng;
    private fetchHike mFetch = null;


    //functions to change textview's to hike information REQUIREMENT #8
    private TextView changeHikeName;
    private TextView HikeDescription;
    private TextView changeRating;
    private TextView changeDifficult;

    //Dummy hike info to test if the string resources have been changed properly
    String name = "This Hike";
    String description = "This is a dummy description of the dummy hike";
    int rating = 5;
    int difficulty = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mStartLat = intent.getDoubleExtra("startLat",0);
        mStartlng = intent.getDoubleExtra("startLng",0);
        mEndLat = intent.getDoubleExtra("endLat",0);
        mEndLng = intent.getDoubleExtra("endLng",0);


        setContentView(R.layout.activity_hike_display);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
       
       //Activate button's
        button1 = (Button)findViewById(R.id.newcomment);
        button1.setOnClickListener(this);

        button2 = (Button)findViewById(R.id.returnmap);
        button2.setOnClickListener(this);


        //Change textview's to hikes information
        changeHikeName = (TextView) findViewById(R.id.hikename);
        changeHikeName.setText(name);

        HikeDescription = (TextView) findViewById(R.id.textView2);
        HikeDescription.setText(description);

        changeDifficult = (TextView) findViewById(R.id.difficulty);
        changeDifficult.setText(Integer.toString(difficulty));

        changeRating = (TextView) findViewById(R.id.rating2);
        changeRating.setText(Integer.toString(rating));
        mFetch = new fetchHike(new LatLng(mStartLat,mStartlng),new LatLng(mEndLat,mEndLng));
        mFetch.execute((Void) null);


    }

    //return to map
    private void returnToMap()
    {
        finish();
    }

    //go to add comment page
    private void addComment()
    {
        startActivity(new Intent(".AddComment"));
    }

    //function to make button's work properly
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.newcomment:
                addComment();
                break;
            case R.id.returnmap:
                returnToMap();
                break;
        }
    }
    
    //Class to get hike's information from the database REQUIREMENT #4
    private class fetchHike extends AsyncTask<Void,Void,Hike> {

        private LatLng mLatLngStart;
        private LatLng mLatLngEnd;

        fetchHike(LatLng lStart,LatLng lEnd){
            mLatLngStart = lStart;
            mLatLngEnd = lEnd;

        }

        @Override
        protected Hike doInBackground(Void... params){
            try {
                Log.e("mstart", mLatLngStart.toString());
                Log.e("mend",mLatLngEnd.toString());
                Hike hike = HikeGeoConnection.getHike(mLatLngStart,mLatLngEnd);
                Log.i("happyHiker", "Fetched contents of URL: count returned=" + hike);
                return hike;
            } catch (Exception e){
                Log.e("happyHiker", "Failed to fetch URL: ", e);
            }
            return new Hike();  // return empty list.
        }

        @Override
        protected void onPostExecute(Hike hike){
            mHike = hike;
           //changeHikeName.setText(hike.name);
           // Log.e("xxx name",hike.name);
            //Log.e("xxx diff",Integer.toString(hike.difficulty));
           // Log.e("xxx rating",Integer.toString(hike.rating));
           //changeDifficult.setText(hike.difficulty);
           // changeRating.setText(hike.rating);



        }
        @Override
        protected void onCancelled(){

        }
    }






}

