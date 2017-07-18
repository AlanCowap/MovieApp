package com.example.android.app.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.android.app.moviesapp.data.FavouriteMoviesContract;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

class NetworkConnection{

    private static final String HTTP_REQUEST_DELIMITER = "\\A";
    private static final String API_BASE_URL = "https://api.themoviedb.org";
    private static final String API_BASE_VERSION = "3";
    private static final String API_KEY_QUERYSTRING = "api_key";
    private static final String API_PAGE = "page";
    private static final String API_MOVIE = "movie";
    private static final String API_VIDEOS = "videos";
    private static final String API_REVIEWS = "reviews";
    private static final String API_KEY = BuildConfig.TMDB_API_KEY;
    //Debug tag name of class
    private static String TAG = NetworkConnection.class.getSimpleName();

    static ArrayList<Movie> fetchPersistentFavourites(Context context) {
        //TODO SUGGESTION This method might be better placed in another class, consider Encapsulation and the Single Responsibility principle.
        //TODO SUGGESTION  - it accesses a (local) sqlite DB via a Content Provider, not the Network.
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Cursor cursor = null;
        ArrayList<Movie> lst = new ArrayList<>();
        //TODO SUGGESTION You can use try-with-resources here.
        try {
            cursor = context.getContentResolver().query(FavouriteMoviesContract.MovieTableEntry.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() < 1) {
                return lst;
            }
            while (cursor.moveToNext()) {
                Movie m = new Movie();
                m.setId(cursor.getInt(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_MOVIE_ID)));
                m.setTitle(cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_TITLE)));
                m.setOverview(cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_OVERVIEW)));
                m.setPosterPath(cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_POSTER_PATH)));
                m.setReleaseDate(cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_RELEASE_DATE)));
                m.setPopularity(cursor.getDouble(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_POPULARITY)));
                m.setVoteCount(cursor.getLong(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_COUNT)));
                m.setVote_Average(cursor.getDouble(cursor.getColumnIndex(FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_AVERAGE)));
                lst.add(m);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return lst;
        } finally {
            cursor.close();
        }
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return lst;
    }

    static ArrayList<Movie> fetchMainPageData(String urlBasis, int page) throws IOException {
       Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        URL url = generateMainPageUrl(urlBasis, page);
        String response = webConnect(url);
        if (response == null) {
            return null;
        }
        return JSONProcessing.parseMovieDetails(response);
    }

    static String fetchMovieTrailers(String movieId) throws IOException {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Uri builtUri;
        builtUri = Uri.parse(API_BASE_URL)
                .buildUpon()
                .appendEncodedPath(API_BASE_VERSION)
                .appendEncodedPath(API_MOVIE)
                .appendEncodedPath(movieId)
                .appendEncodedPath(API_VIDEOS)
                .appendQueryParameter(API_KEY_QUERYSTRING, API_KEY)
                .build();
        URL url;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException ex) {
            Log.d(TAG, ex.getMessage());
            Log.d(TAG, builtUri.toString());
            return null;
        }
        String result = webConnect(url);

        if (result != null && result.length() > 0) {
            result = JSONProcessing.parseTrailers(result);
        }
        return result;
    }

    static String fetchMovieReviews(String movieId) throws IOException {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Uri builtUri;
        builtUri = Uri.parse(API_BASE_URL)
                .buildUpon()
                .appendEncodedPath(API_BASE_VERSION)
                .appendEncodedPath(API_MOVIE)
                .appendEncodedPath(movieId)
                .appendEncodedPath(API_REVIEWS)
                .appendQueryParameter(API_KEY_QUERYSTRING, API_KEY)
                .build();
        URL url;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException ex) {
            Log.d(TAG, ex.getMessage());
            Log.d(TAG, builtUri.toString());
            return null;
        }
        String result = webConnect(url);

        if (result != null && result.length() > 0) {
            result = JSONProcessing.parseReviews(result);
        }
        return result;
    }

    private static String webConnect(URL url) throws IOException {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        if(url == null || url.toString().isEmpty()){
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
            return null;
        }
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter(HTTP_REQUEST_DELIMITER);
            boolean hasInput = scanner.hasNext();
            if(hasInput){
                String data = scanner.next();
                Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
                return data;
            }else{
                return null;
            }
        }
        finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        }
    }

    private static URL generateMainPageUrl(String basis, int page) {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Uri builtUri;
        if(page > 0){
            builtUri = Uri.parse(API_BASE_URL)
                    .buildUpon()
                    .appendEncodedPath(API_BASE_VERSION)
                    .appendEncodedPath(basis)
                    .appendQueryParameter(API_KEY_QUERYSTRING, API_KEY)
                    .appendQueryParameter(API_PAGE, String.valueOf(page))
                    .build();
        }else{
            builtUri = Uri.parse(API_BASE_URL)
                    .buildUpon()
                    .appendEncodedPath(API_BASE_VERSION)
                    .appendEncodedPath(basis)
                    .appendQueryParameter(API_KEY_QUERYSTRING, API_KEY)
                    .build();
        }
        URL url;
        try{
            url = new URL(builtUri.toString());
        }catch (MalformedURLException ex){
            Log.d(TAG, ex.getMessage());
            Log.d(TAG, builtUri.toString());
            return null;
        }
        Log.d(TAG, url.toString());
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return url;
    }
}
