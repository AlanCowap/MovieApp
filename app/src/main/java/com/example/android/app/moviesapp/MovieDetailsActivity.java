package com.example.android.app.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.app.moviesapp.data.FavouriteMoviesContract;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    // Debug tag name of class
    private static final String TAG = MoviesMainActivity.class.getSimpleName();

    private static String TRAILER_LINK_TITLE = "TrailerLink";

    private static String TRAILER_REVIEWS = "Reviews";
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
    //reviews
    TextView mReviewTitle;
    TextView mReviews;
    //Favourites button
    Button mFaveButton;
    String TrailerLink = "";
    private int movieID = 0;
    private int index = 0;
    //video recycler
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logAndAppend( Generator.LOG_ENTERING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        Intent triggeringIntent = getIntent();
        if (triggeringIntent != null && triggeringIntent.hasExtra(Intent.EXTRA_TEXT)) {
            movieID = Integer.parseInt(triggeringIntent.getStringExtra(Intent.EXTRA_TEXT));
        }
        String tmpReviews = "";
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRAILER_LINK_TITLE)) {
                TrailerLink = savedInstanceState.getString(TRAILER_LINK_TITLE);
            }
            if (savedInstanceState.containsKey(TRAILER_REVIEWS)) {
                tmpReviews = savedInstanceState.getString(TRAILER_REVIEWS);
            }
        }
        setContentView(R.layout.activity_movie_details);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_details_trailers);
        for(int i = 0; i<MoviesMainActivity.mMovies.size(); ++i){
            if(MoviesMainActivity.mMovies.get(i).getId() == movieID){
                index = i;
                break;
            }
        }

        if (TrailerLink == null || TrailerLink == "") {
            new GetVideos().execute();
        }
        Movie movie = MoviesMainActivity.mMovies.get(index);
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
        mReviews = (TextView) findViewById(R.id.reviews);
        mReviewTitle = (TextView) findViewById(R.id.reviews_title);
        if (tmpReviews == null || tmpReviews == "") {
            new GetReviews().execute();
        } else {
            mReviews.setText(tmpReviews);
        }
        mFaveButton = (Button) findViewById(R.id.btn_favourite);
        setSelectedItemAsFavourite(movieIsInDatabase());
        logAndAppend( Generator.LOG_EXITING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if (outState != null) {
            outState.putString(TRAILER_LINK_TITLE, TrailerLink);
            outState.putString(TRAILER_REVIEWS, mReviews.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    public void favouriteButtonClicked(View view) {

        if (movieIsInDatabase()) {
            removeMovieFromDatabase();
        } else {
            Movie movieToAdd = null;
            for (Movie m : MoviesMainActivity.mMovies) {
                if (m.getId() == movieID) {
                    movieToAdd = m;
                    break;
                }
            }
            putMovieInDatabase(movieToAdd);

        }
    }

    private void removeMovieFromDatabase() {
        Uri uriToDelete = FavouriteMoviesContract.MovieTableEntry.CONTENT_URI;
        uriToDelete = uriToDelete.buildUpon().appendPath(Integer.toString(movieID)).build();
        int result = getContentResolver().delete(uriToDelete, null, null);
        if (result == 1) {
            setSelectedItemAsFavourite(false);
        }
    }

    private void putMovieInDatabase(Movie movieToAdd) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_MOVIE_ID, movieToAdd.getId());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_OVERVIEW, movieToAdd.getOverview());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_POPULARITY, movieToAdd.getPopularity());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_POSTER_PATH, movieToAdd.getPosterPath());
        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM);
        java.text.DateFormat df = new java.text.SimpleDateFormat(getString(R.string.day_iso_format));
        String isoDate = df.format(movieToAdd.getReleaseDate());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_RELEASE_DATE, isoDate);
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_TITLE, movieToAdd.getTitle());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_AVERAGE, movieToAdd.getVote_Average());
        contentValues.put(FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_COUNT, movieToAdd.getVoteCount());

        Uri result = getContentResolver().insert(FavouriteMoviesContract.MovieTableEntry.CONTENT_URI, contentValues);
        if (result != null) {
            setSelectedItemAsFavourite(true);
        }
    }

    private Boolean movieIsInDatabase() {
        Boolean result = false;
        Uri uri = FavouriteMoviesContract.MovieTableEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(Integer.toString(movieID)).build();
        Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null
                && cursor.moveToNext()
                && cursor.getInt(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_MOVIE_ID)) == movieID) {
            result = true;
        }
        return result;
    }

    private void setSelectedItemAsFavourite(Boolean favourite) {
        if (favourite) {
            mFaveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorFavourite));
        } else {
            mFaveButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNotFavourite));
        }
    }

    // logging output
    private void logAndAppend(String str){
        Log.v(TAG, str);
    }

    private class GetVideos extends AsyncTask<String, Void, String> implements TrailerAdapter.OnTrailerAdapterClickEvent {
        @Override
        protected String doInBackground(String... params) {
            try {
                return NetworkConnection.fetchMovieTrailers(String.valueOf(movieID));
            } catch (Exception e) {
                logAndAppend(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && s.length() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
                mRecyclerView.setLayoutManager(layoutManager);
                TrailerAdapter adapter = new TrailerAdapter(s, this);
                mRecyclerView.setAdapter(adapter);
            } else {
                mRecyclerView.setVisibility(View.INVISIBLE);
                TrailerLink = "";
            }
        }

        @Override
        public void LaunchTrailerIntent(String trailerString) {
            Uri uri = Uri.parse(trailerString);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
    }

    private class GetReviews extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                return NetworkConnection.fetchMovieReviews(String.valueOf(movieID));
            } catch (Exception e) {
                logAndAppend(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && s.length() > 0) {
                mReviews.setText(s);
                mReviews.setVisibility(View.VISIBLE);
                mReviewTitle.setVisibility(View.VISIBLE);
            } else {
                mReviews.setText("");
                mReviews.setVisibility(View.INVISIBLE);
                mReviewTitle.setVisibility(View.INVISIBLE);
            }
        }
    }
}
