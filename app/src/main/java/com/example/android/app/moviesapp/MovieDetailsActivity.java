package com.example.android.app.moviesapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {

    // Debug tag name of class
    private static final String TAG = MoviesMainActivity.class.getSimpleName();
    private static int movieID = 0;
    private static int index = 0;
    // Movie Title
    TextView mTitle;
    //Movie Rating
    TextView mRating;
    //Movie Release Date
    TextView mReleaseDate;
    // Movie Thumbnail
    ImageView mImage;
    //Movie Overview
    TextView mOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logAndAppend( Generator.LOG_ENTERING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        Intent triggeringIntent = getIntent();
        if(triggeringIntent.hasExtra(Intent.EXTRA_TEXT)){
            movieID = Integer.parseInt(triggeringIntent.getStringExtra(Intent.EXTRA_TEXT));
        }
        for(int i = 0; i<MoviesMainActivity.mMovies.size(); ++i){
            if(MoviesMainActivity.mMovies.get(i).getId() == movieID){
                index = i;
                break;
            }
        }
        Movie movie = MoviesMainActivity.mMovies.get(index);
        setContentView(R.layout.activity_movie_details);
        mImage = (ImageView)findViewById(R.id.thumbnail);
        Picasso
                .with(this)
                .load(movie.getPosterPath())
                .into(mImage);
        mTitle = (TextView)findViewById(R.id.tv_title);
        mTitle.setText(movie.getTitle());
        mRating = (TextView)findViewById(R.id.vote_average);
        mRating.setText(String.valueOf(movie.getVote_Average()));
        mReleaseDate = (TextView)findViewById(R.id.distrib_date);
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
        mReleaseDate.setText(dateFormat.format(movie.getReleaseDate()));
        mOverview = (TextView)findViewById(R.id.overview);
        mOverview.setText(movie.getOverview());
        logAndAppend( Generator.LOG_EXITING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    // logging output
    private void logAndAppend(String str){
        Log.v(TAG, str);
    }
}
