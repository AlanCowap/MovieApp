package com.example.android.app.moviesapp;

import android.net.Uri;
import android.util.Log;

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
    private static final String TAG = NetworkConnection.class.getSimpleName();

    static ArrayList<Movie> fetchMainPageData(String urlBasis, int page) throws IOException {
       Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        URL url = generateMainPageUrl(urlBasis, page);
        String response = webConnect(url);
        if (response == null) {
            return null;
        }
        return JSONProcessing.parseMovieDetails(response);
    }

    static ArrayList<String> fetchMovieTrailers(String movieId) throws IOException {
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
        ArrayList<String> trailerList = null;

        if (result != null && result.length() > 0) {
            trailerList = JSONProcessing.parseTrailers(result);
        }
        return trailerList;
    }

    static ArrayList<String> fetchMovieReviews(String movieId) throws IOException {
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
        ArrayList<String> reviewList = null;
        if (result != null && result.length() > 0) {
            reviewList = JSONProcessing.parseReviews(result);
        }
        return reviewList;
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
