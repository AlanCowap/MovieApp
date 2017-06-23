package com.example.android.app.moviesapp;

import android.util.Log;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Created by Laurence on 21/05/2017.
 *
 */

// default only within package
class NetworkConnection{

    //Debug tag name of class
    private static String TAG = NetworkConnection.class.getSimpleName();

    private static final String HTTP_REQUEST_DELIMITER = "\\A";

    private static final String API_BASE_URL = "https://api.themoviedb.org";
    private static final String API_BASE_VERSION = "3";
    private static final String API_KEY_QUERYSTRING = "api_key";
    private static final String API_PAGE = "page";

    private static final String API_KEY = BuildConfig.TMDB_API_KEY;


   static ArrayList<Movie> fetchMainPageData(String urlBasis, int page) throws IOException{
       Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        URL url = generateUrl(urlBasis, page);
        if(url == null || url.toString().isEmpty()){
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
                ////Process the data on this class
                return JSONProcessing.parseMovieDetails(data);
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


    private static URL generateUrl(String basis, int page){
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
