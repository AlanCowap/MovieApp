package com.example.android.app.moviesapp;

import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private static final String DATABASE_REQUEST_TYPE = "request_type";
    private static final String DATABASE_POSITION = "clicked_position";
    private static final String DATABASE_PAGE_RETRIEVAL = "page";

    //Unique int for the AsyncLoader
    private static int LoaderId;

    // Movie data downloaded
    static ArrayList<Movie> mMovies = new ArrayList<>();
    static int TotalResults = 0;
    static int PageCount = 0;
    static int CurrentRetrievedPage = 0;

    // whether the view is currently by popular or rating
    // negative 1 indicates not currently set
    private static int viewType = 0;



    // Untranslated options of the spinner comparison to identify which was selected
    private static String[] mSpinnerOptions;


    // The poster display area recyclerView
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            if(savedInstanceState.containsKey(DATABASE_REQUEST_TYPE)){
                //TODO EXCELLENT It's good practice to check for null and the key exists
                //TODO You can also combine these two if statements using the && operator
                viewType = savedInstanceState.getInt(DATABASE_REQUEST_TYPE);
            }
        }
        setContentView(R.layout.activity_movies_main);
        Movie.setRootPosterUrl();

        createSpinner();

        recyclerView = (RecyclerView)findViewById(R.id.rv_movie_images);


        //Initialise the AsyncLoader
        LoaderId = Generator.getNewUniqueLoaderId();
        getSupportLoaderManager().initLoader(LoaderId, null, this);
        if(viewType == 1 ){
            //TODO SUGGESTION Defining e.g. a String constant rather than an int, can make your code easier to read and less error prone.
            createImageLayout(RATING_LIST);
        }else{
            createImageLayout(POPULAR_LIST);
        }

        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if(savedInstanceState!=null) {
            if (savedInstanceState.containsKey(DATABASE_REQUEST_TYPE)) {
                viewType = savedInstanceState.getInt(DATABASE_REQUEST_TYPE);
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
        queryBundle.putInt(DATABASE_PAGE_RETRIEVAL, 0);
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
                //Initialising a loading indicator here if wanted
                if(MoviesMainActivity.mMovies!=null && MoviesMainActivity.mMovies.size() > 0){
                    deliverResult(MoviesMainActivity.mMovies);
                }else{
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
                /* Parse the URL from the passed in String and perform the search */
                try {
                    // HERE 0 parameter indicates it is a brand new query, and therefore not requesting a specific page
                    return NetworkConnection.fetchMainPageData(queryType, pageNum);
                    //TODO AWESOME You're loading and parsing the data in a background thread
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    return null;
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
        //assume error if null results
        if(null == data){
            Generator.GenerateToastMessage(this, getResources().getString(R.string.io_exception_message));
        }else{
            mMovies = data;
            RecyclerView.LayoutManager layoutManager;
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            if(size.x>size.y){
                layoutManager = new GridLayoutManager(getApplicationContext(), 4);
            }else{
                layoutManager = new GridLayoutManager(getApplicationContext(), 2);
            }
            recyclerView.setLayoutManager(layoutManager);
            ImageAdapter mImageAdapter = new ImageAdapter(this);
            recyclerView.setAdapter(mImageAdapter);
        }
        logAndAppend(  Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }
    /// END OF ASYNC LOADER OVERRIDES


    //Spinner Creation
    private void createSpinner(){
        logAndAppend(  Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        mSpinnerOptions = getResources().getStringArray(R.array.spinner_choices_ind);

        // get the spinner from the view
        // initialise the adapter,
        // and attach the adapter to the spinner
        Spinner spinner = (Spinner)findViewById(R.id.sp_sort_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_choices, R.layout.spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // set the listener function to the spinner
        spinner.setOnItemSelectedListener(this);
        logAndAppend(  Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
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

        // process the clicked item
        if(mSpinnerOptions[position].equals(mSpinnerOptions[0])){ // Popular
            mMovies.clear();
            viewType = 0;
            createImageLayout(POPULAR_LIST);
        }
        else if(mSpinnerOptions[position].equals(mSpinnerOptions[1])){ // ratings
            mMovies.clear();
            viewType = 1;
            createImageLayout(RATING_LIST);
        }

        String result = parent.getItemAtPosition(position).toString();
        Generator.GenerateToastMessage(this, getResources().getString(R.string.spinnerChoiceSelected) + getResources().getString(R.string.space_character) + result);
        //TODO SUGGESTION Too many toasts spoil the UX
        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());

    }

    ///Spinner override Listener event
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Generator.GenerateToastMessage(this, getResources().getString(R.string.spinnerChoiceNone));
        logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    // END SPINNER

    // Menu system next three overrides
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logAndAppend(Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        int menuItemSelected = item.getItemId();
        if(menuItemSelected == R.id.action_refresh){
            mMovies.clear();
            if(mSpinnerOptions[MoviesMainActivity.viewType].equals(mSpinnerOptions[0])){
                createImageLayout(POPULAR_LIST);
            }else{
                createImageLayout(RATING_LIST);
            }
            // TODO AWESOME As required you're displaying the highest ranked or most populat movies
            Generator.GenerateToastMessage(this, getResources().getString(R.string.refreshing_data));
            logAndAppend(Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
            return true;
        }

        logAndAppend( Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return super.onOptionsItemSelected(item);
    }

    public void onListItemClick(int ClickedMovieId){
        Generator.ClearToast();
            Class destinationActivity = MovieDetailsActivity.class;
            Intent intent = new Intent(this, destinationActivity);
            intent.putExtra(Intent.EXTRA_TEXT, String.valueOf(ClickedMovieId));
            startActivity(intent);
        //TODO EXCELLENT You're starting a new activity with the MovieID.
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
            outState.putInt(DATABASE_REQUEST_TYPE,viewType);
            outState.putInt(DATABASE_POSITION,((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
            //TODO ~~REQUIREMENT~~ "Performance and Stability" Your app crashes when there is no network connection and the device orientation changes
        }
        super.onSaveInstanceState(outState);
    }
    //TODO AWESOME Saving state here improves the UX.

    // logging output
    private void logAndAppend(String str){
        Log.d(TAG, str);
    }
}
