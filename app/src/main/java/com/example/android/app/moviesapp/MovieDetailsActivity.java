package com.example.android.app.moviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.app.moviesapp.data.FavouriteMoviesContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieDetailsActivity extends AppCompatActivity {
    // Debug tag name of class
    private static final String TAG = MoviesMainActivity.class.getSimpleName();

    private static final String SCROLL_TOP = "scroll_top";
    private static final String SCROLL_Y = "scroll_y";
    private static final String SCROLL_X = "scroll_x";
    private static final String TRAILER_LINK_TITLE = "TrailerLink";
    private static final String TRAILER_REVIEWS = "Reviews";
    private static final String EMPTY_STRING = "";
    private static final String SINGLE_SPACE = " ";
    private static final String NEW_LINE = "\r\n";

    TextView mTitle;
    TextView mRating;
    TextView mReleaseDate;
    ImageView mMovieThumbnail;
    TextView mOverview;
    TextView mReviewTitle;
    TextView mReviews;
    Button mFavouriteButton;
    String mTrailerLink = EMPTY_STRING;
    private int movieID = 0;
    private int index = 0;
    private RecyclerView mVideoRecyclerView;
    private NestedScrollView mNestedScrollView;
    private int mNestedScrollViewPositionY;
    private int mNestedScrollViewPositionX;
    private int mNestedScrollViewPositionTop;
    private Boolean mIsWaitingForMovies = false;
    private Boolean mIsWaitingForReviews = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logAndAppend( Generator.LOG_ENTERING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        String tmpReviews = null;
        Intent triggeringIntent = getIntent();
        if (triggeringIntent != null && triggeringIntent.hasExtra(Intent.EXTRA_TEXT)) {
            movieID = Integer.parseInt(triggeringIntent.getStringExtra(Intent.EXTRA_TEXT));
            mNestedScrollViewPositionX = 0;
            mNestedScrollViewPositionY = 0;
            mNestedScrollViewPositionTop = 0;
        }
        setContentView(R.layout.activity_movie_details);
        mVideoRecyclerView = (RecyclerView) findViewById(R.id.rv_details_trailers);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.sv_detail);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRAILER_LINK_TITLE)) {
                mTrailerLink = savedInstanceState.getString(TRAILER_LINK_TITLE);
            }
            if (savedInstanceState.containsKey(TRAILER_REVIEWS)) {
                tmpReviews = savedInstanceState.getString(TRAILER_REVIEWS);
            }
        }


        for(int i = 0; i<MoviesMainActivity.mMovies.size(); ++i){
            if(MoviesMainActivity.mMovies.get(i).getId() == movieID){
                index = i;
                break;
            }
        }

        if (MoviesMainActivity.mMovies.size() == 0) {
            logAndAppend(Generator.LOG_EXITING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
            return;
        }
        Movie movie = MoviesMainActivity.mMovies.get(index);
        mMovieThumbnail = (ImageView) findViewById(R.id.thumbnail);
        Picasso
                .with(this)
                .load(movie.getPosterPath())
                .into(mMovieThumbnail);
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
        if (mTrailerLink == null || mTrailerLink.length() == 0) {
            mIsWaitingForMovies = true;
            new GetVideos().execute();
        }
        if (tmpReviews == null || tmpReviews.length() == 0) {
            mIsWaitingForReviews = true;
            new GetReviews().execute();
        } else {
            mReviews.setText(tmpReviews);
            mNestedScrollView.setScrollX(mNestedScrollViewPositionX);
            mNestedScrollView.setScrollY(mNestedScrollViewPositionY);
            if (!mIsWaitingForMovies) {
                addScrollListener();
            }
        }
        mFavouriteButton = (Button) findViewById(R.id.btn_favourite);
        setSelectedItemAsFavourite(movieIsInDatabase());
        logAndAppend( Generator.LOG_EXITING + Generator.SPACE_CHAR + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    private void addScrollListener() {
        mNestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mNestedScrollViewPositionTop = mNestedScrollView.getTop();
                int[] loc = new int[2];
                mNestedScrollView.getLocationOnScreen(loc);
                mNestedScrollViewPositionY = loc[1];
                mNestedScrollViewPositionX = loc[0];
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TRAILER_LINK_TITLE)) {
                mTrailerLink = savedInstanceState.getString(TRAILER_LINK_TITLE);
            }
            if (savedInstanceState.containsKey(SCROLL_Y)) {
                mNestedScrollViewPositionY = savedInstanceState.getInt(SCROLL_Y);
                mNestedScrollViewPositionTop = savedInstanceState.getInt(SCROLL_TOP);
                mNestedScrollViewPositionX = savedInstanceState.getInt(SCROLL_X);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if (outState != null) {
            outState.putString(TRAILER_LINK_TITLE, mTrailerLink);
            outState.putString(TRAILER_REVIEWS, mReviews.getText().toString());
            if (mNestedScrollView != null && mNestedScrollViewPositionY > 0) {
                outState.putInt(SCROLL_Y, mNestedScrollViewPositionY);
                outState.putInt(SCROLL_X, mNestedScrollViewPositionX);
                outState.putInt(SCROLL_TOP, mNestedScrollViewPositionTop);
            }
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
        //TODO DONE SUGGESTION Close your cursor to avoid memory leaks
        cursor.close();
        return result;
    }

    private void setSelectedItemAsFavourite(Boolean favourite) {
        if (favourite) {
            mFavouriteButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorFavourite));
        } else {
            mFavouriteButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorNotFavourite));
        }
    }

    // logging output
    private void logAndAppend(String str){
        Log.v(TAG, str);
    }

    private class GetVideos extends AsyncTask<String, Void, ArrayList<String>> implements TrailerAdapter.OnTrailerAdapterClickEvent {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                return NetworkConnection.fetchMovieTrailers(String.valueOf(movieID));
            } catch (Exception e) {
                logAndAppend(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if (s != null && s.size() > 0) {
                mVideoRecyclerView.setVisibility(View.VISIBLE);
                mVideoRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
                mVideoRecyclerView.setLayoutManager(layoutManager);
                TrailerAdapter adapter = new TrailerAdapter(s, this);
                mVideoRecyclerView.setAdapter(adapter);
            } else {
                mVideoRecyclerView.setVisibility(View.INVISIBLE);
                mTrailerLink = EMPTY_STRING;
            }
            mIsWaitingForMovies = false;
            if (!mIsWaitingForReviews) {
                addScrollListener();
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

    private class GetReviews extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... params) {
            try {
                return NetworkConnection.fetchMovieReviews(String.valueOf(movieID));
            } catch (Exception e) {
                logAndAppend(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if (s != null && s.size() > 0) {
                String reviews = EMPTY_STRING;
                for (int i = 0; i < s.size(); ++i) {
                    if (i > 0) {
                        reviews = reviews + NEW_LINE;
                    }
                    reviews = reviews + s.get(i);
                }
                mReviews.setText(reviews);
                mReviews.setVisibility(View.VISIBLE);
                mReviewTitle.setVisibility(View.VISIBLE);
            } else {
                mReviews.setText(EMPTY_STRING);
                mReviews.setVisibility(View.INVISIBLE);
                mReviewTitle.setVisibility(View.INVISIBLE);
            }
            mNestedScrollView.setScrollX(mNestedScrollViewPositionX);
            mNestedScrollView.setScrollY(mNestedScrollViewPositionY + mNestedScrollViewPositionTop);
            mIsWaitingForReviews = false;
            if (!mIsWaitingForMovies) {
                addScrollListener();
            }
        }
    }
}
