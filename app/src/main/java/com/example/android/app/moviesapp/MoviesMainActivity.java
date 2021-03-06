package com.example.android.app.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;

public class MoviesMainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
        LoaderManager.LoaderCallbacks<ArrayList<Movie>>,
        ImageAdapter.ListItemClickListener{

    // Debug tag name of class
    private static final String TAG = MoviesMainActivity.class.getSimpleName();
    //path for choice appearance
    private static final String POPULAR_LIST = "movie/popular";
    private static final String RATING_LIST = "movie/top_rated";
    private static final String FAVOURITES_LIST = "favourites";
    private static final int NULL_PAGE = 0;
    private static final String DATABASE_POSITION_INDEX = "index_position";
    private static final String DATABASE_REQUEST_TYPE = "request_type";
    private static final String DATABASE_POSITION_TOP = "top_position";
    private static final String DATABASE_PAGE_RETRIEVAL = "page";
    private static final int PORTRAIT_WIDE_COUNT = 2;
    private static final int LANDSCAPE_WIDE_COUNT = 4;
    // Movie data downloaded
    static ArrayList<Movie> mMovies = new ArrayList<>();
    static int TotalResults = 0;
    static int PageCount = 0;
    static int CurrentRetrievedPage = 0;
    //Unique int for the AsyncLoader
    private static int LoaderId;
    // whether the view is currently by popular or rating
    // negative 1 indicates not currently set
    private static int mViewType = 0;
    private static int mOldViewType = -1;
    // Untranslated options of the spinner comparison to identify which was selected
    private static String[] mSpinnerOptions;
    // The poster display area recyclerView
    private RecyclerView mRecyclerView;

    private int mRecyclerViewTopPositionAtSave = 0;
    private int mRecyclerViewIndexAtSave = 0;

    //TODO DONE REQUIREMENT "Maintains list items positions on device rotation" - the Grid Layout loses it's position e.g. on device rotation
    //TODO-2.1 Excellent All Requirements met.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DATABASE_REQUEST_TYPE)) {
                mViewType = savedInstanceState.getInt(DATABASE_REQUEST_TYPE);
            }
            if (savedInstanceState.containsKey(DATABASE_POSITION_TOP)) {
                mRecyclerViewTopPositionAtSave = savedInstanceState.getInt(DATABASE_POSITION_TOP);
                mRecyclerViewIndexAtSave = savedInstanceState.getInt(DATABASE_POSITION_INDEX);
            }
        }
        setContentView(R.layout.activity_movies_main);
        Movie.setRootPosterUrl();
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_images);
        //Initialise the AsyncLoader
        mSpinnerOptions = getResources().getStringArray(R.array.spinner_choices_ind);
        LoaderId = Generator.getNewUniqueLoaderId();
        getSupportLoaderManager().initLoader(LoaderId, null, this);
        if (mSpinnerOptions[MoviesMainActivity.mViewType].equals(getString(R.string.Popular))) {
            createImageLayout(POPULAR_LIST);
        } else if (mSpinnerOptions[MoviesMainActivity.mViewType].equals(getString(R.string.Rating))) {
            createImageLayout(RATING_LIST);
        } else {
            createImageLayout(FAVOURITES_LIST);
        }
        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSpinnerOptions[MoviesMainActivity.mViewType].equals(getString(R.string.Favourites))) {
            mMovies.clear();
            createImageLayout(FAVOURITES_LIST);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if(savedInstanceState!=null) {
            if (savedInstanceState.containsKey(DATABASE_REQUEST_TYPE)){
                mViewType = savedInstanceState.getInt(DATABASE_REQUEST_TYPE);
            }
        }
        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    //GridLayout fill
    private void createImageLayout(String chosenLayout){
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        //THE ASYNC LOADER
        Bundle queryBundle = new Bundle();
        queryBundle.putString(DATABASE_REQUEST_TYPE, chosenLayout);
        queryBundle.putInt(DATABASE_PAGE_RETRIEVAL, NULL_PAGE);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieListLoader = loaderManager.getLoader(LoaderId);
        if(movieListLoader == null){
            loaderManager.initLoader(LoaderId, queryBundle, this);
        } else{
            loaderManager.restartLoader(LoaderId, queryBundle, this);
        }
        logAndAppend(  Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    /// ASYNC LOADER OVERRIDES
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(final int id, final Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(MoviesMainActivity.this) {
            @Override
            protected void onStartLoading() {
                if(args == null){
                    return;
                }

                String choice = args.getString(DATABASE_REQUEST_TYPE);
                //Initialising a loading indicator here if wanted
                if (mOldViewType == mViewType && !mSpinnerOptions[mViewType].equals(getString(R.string.Favourites))) {
                    mOldViewType = mViewType;
                    deliverResult(MoviesMainActivity.mMovies);
                }else{
                    mRecyclerView.setAdapter(null);
                    mOldViewType = mViewType;
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(ArrayList<Movie> data) {
                super.deliverResult(data);
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                String queryType = args.getString(DATABASE_REQUEST_TYPE);
                int pageNum = args.getInt(DATABASE_PAGE_RETRIEVAL);
                if (queryType != null && queryType.equals(FAVOURITES_LIST)) {
                    try {
                        return DataBaseConnection.fetchPersistentFavourites(getContext());
                    } catch (Exception e) {
                        logAndAppend(e.getMessage());
                        return null;
                    }

                } else {
                     /* Parse the URL from the passed in String and perform the search */
                    ArrayList<Movie> m = null;
                    if (isOnline()) {
                        try {
                            m = NetworkConnection.fetchMainPageData(queryType, pageNum);
                            return m;
                        } catch (IOException e) {
                            logAndAppend(e.getMessage());
                            return null;
                        }
                    } else {
                        return null;
                    }
                }
            }
        };
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        //Nothing to do
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> data) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        //Here hide the loading image if implemented
        if (null == data) {
            Generator.generateToastMessage(this, getResources().getString(R.string.io_exception_message));
        } else {
            if (mSpinnerOptions[mViewType].equals(getString(R.string.Favourites)) && data.size() == 0) {
                Generator.generateToastMessage(this, getResources().getString(R.string.database_empty));
            }
        }
        GenerateRecyclerView(data);
        logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    /// END OF ASYNC LOADER OVERRIDES

    private void GenerateRecyclerView(ArrayList<Movie> movies) {
        mMovies = movies;
        RecyclerView.LayoutManager layoutManager;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (size.x > size.y) {
            layoutManager = new GridLayoutManager(getApplicationContext(), LANDSCAPE_WIDE_COUNT);
        } else {
            layoutManager = new GridLayoutManager(getApplicationContext(), PORTRAIT_WIDE_COUNT);
        }
        mRecyclerView.setLayoutManager(layoutManager);
        ImageAdapter mImageAdapter = new ImageAdapter(movies, this);
        mRecyclerView.setAdapter(mImageAdapter);
        if (mMovies != null && mMovies.size() > 0 && mRecyclerViewIndexAtSave > 0) {
            ((GridLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(mRecyclerViewIndexAtSave, mRecyclerViewTopPositionAtSave);
        }
    }

    //Spinner Click Listener events
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        // suppresses the toast that is triggered during onCreate
        if(!Generator.mShowToast){
            Generator.mShowToast = true;
            return;
        }
        mViewType = position;
        if (mMovies != null && (mOldViewType != mViewType || mSpinnerOptions[mViewType].equals(getString(R.string.Favourites)))) {
            mMovies.clear();
        }
        // process the clicked item
        if(mSpinnerOptions[position].equals(getString(R.string.Popular))){ // Popular
            createImageLayout(POPULAR_LIST);
        }
        else if(mSpinnerOptions[position].equals(getString(R.string.Rating))){ // ratings
            createImageLayout(RATING_LIST);
        } else if (mSpinnerOptions[position].equals(getString(R.string.Favourites))) { // favourites
            createImageLayout(FAVOURITES_LIST);
        }
        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    ///Spinner override Listener event
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Generator.generateToastMessage(this, getResources().getString(R.string.spinnerChoiceNone));
        logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    // END SPINNER

    // Menu system next overrides
    // Handles the spinner state with rotation of view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        Spinner spinner = (Spinner)menu.findItem(R.id.nav_spinner).getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_choices, R.layout.spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(mViewType);
        spinner.setOnItemSelectedListener(this);
        logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return true;
    }

    public void onListItemClick(int ClickedMovieId){
        Generator.clearToast();
        Class destinationActivity = MovieDetailsActivity.class;
        Intent intent = new Intent(this, destinationActivity);
        intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(ClickedMovieId));
        startActivity(intent);
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    @NonNull
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }
    // END MENU SYSTEM
    // InstanceState Data Conservation override
    @Override
    public void onSaveInstanceState(Bundle outState) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if(outState!=null){
            outState.putInt(DATABASE_REQUEST_TYPE, mViewType);
            if (mRecyclerView.getAdapter() != null && mRecyclerView.getAdapter().getItemCount() > 0) {
                View v = mRecyclerView.getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - v.getPaddingTop());
                int index = ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                outState.putInt(DATABASE_POSITION_TOP, top);
                outState.putInt(DATABASE_POSITION_INDEX, index);
            }
        }
        super.onSaveInstanceState(outState);
    }

    //https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // logging output
    private void logAndAppend(String str){
        Log.d(TAG, str);
    }
}
