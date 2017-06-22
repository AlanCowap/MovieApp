package com.example.android.app.moviesapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Laurence on 23/05/2017.
 *
 */

class JSONProcessing {
    //Debug tag name of class
    private static String TAG = JSONProcessing.class.getSimpleName();


    //JSON movie database keys
    private static final String JSON_PAGE = "page";
    private static final String JSON_TOP_LEVEL_PARAM = "results";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_ADULT = "adult";
    private static final String JSON_OVERVIEW = "overview";
    private static final String JSON_RELEASE_DATE = "release_date";
    private static final String JSON_GENRE_IDS = "genre_ids";
    private static final String JSON_ID = "id";
    private static final String JSON_ORIGINAL_TITLE = "original_title";
    private static final String JSON_ORIG_LANG = "original_language";
    private static final String JSON_ENG_TITLE = "title";
    private static final String JSON_BACKDROP_PATH = "backdrop_path";
    private static final String JSON_POPULARITY = "popularity";
    private static final String JSON_VOTE_COUNT = "vote_count";
    private static final String JSON_VIDEO = "video";
    private static final String JSON_VOTE_AVG = "vote_average";
    private static final String JSON_TOTAL_RESULTS = "total_results";
    private static final String JSON_TOTAL_PAGES = "total_pages";

    // Accepts a string from the movie database by popularity or movies by rating
    // and processing them returning an Array List of type Movie, with the list of movies
    static ArrayList<Movie> parseMovieDetails(String data){
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Log.d(TAG, data);
        ArrayList<Movie> movieThumbs = new ArrayList<>();
        JSONArray jsonMoviesArray;
        try{
            JSONObject fullJSONObject = new JSONObject(data);
            int pageNum = Integer.parseInt(fullJSONObject.getString(JSON_PAGE));
            if(pageNum == 1){
                MoviesMainActivity.PageCount = Integer.parseInt(fullJSONObject.getString(JSON_TOTAL_PAGES));
                MoviesMainActivity.TotalResults = Integer.parseInt(fullJSONObject.getString(JSON_TOTAL_RESULTS));
            }
            MoviesMainActivity.CurrentRetrievedPage = pageNum;
            jsonMoviesArray = fullJSONObject.getJSONArray(JSON_TOP_LEVEL_PARAM);
            for(int i = 0; i<jsonMoviesArray.length(); ++i){
                JSONObject movieJSONObject = jsonMoviesArray.getJSONObject(i);
                if(movieJSONObject.length() == 14){
                    Movie m = new Movie();
                    m.setPosterPath(movieJSONObject.getString(JSON_POSTER_PATH));
                    m.setAdult(movieJSONObject.getBoolean(JSON_ADULT));
                    m.setOverview(movieJSONObject.getString(JSON_OVERVIEW));
                    m.setReleaseDate(movieJSONObject.getString(JSON_RELEASE_DATE));
                    JSONArray genreArray = movieJSONObject.getJSONArray(JSON_GENRE_IDS);
                    Integer[] array = new Integer[genreArray.length()];
                    for(int j = 0; j < genreArray.length(); ++j){
                        array[j] = Integer.parseInt(genreArray.getString(j));
                    }
                    m.setGenre(array);
                    m.setId(movieJSONObject.getString(JSON_ID));
                    m.setOriginalTitle(movieJSONObject.getString(JSON_ORIGINAL_TITLE));
                    m.setOriginalLanguage(movieJSONObject.getString(JSON_ORIG_LANG));
                    m.setTitle(movieJSONObject.getString(JSON_ENG_TITLE));
                    m.setBackDropPath(movieJSONObject.getString(JSON_BACKDROP_PATH));
                    m.setPopularity(movieJSONObject.getString(JSON_POPULARITY));
                    m.setVoteCount(movieJSONObject.getString(JSON_VOTE_COUNT));
                    m.setVote_Average(movieJSONObject.getString(JSON_VOTE_AVG));
                    m.setVideo(movieJSONObject.getString(JSON_VIDEO));
                    m.setPageNumber(pageNum);
                    movieThumbs.add(m);
                }
            }
        }catch (JSONException e){
            return null;
        }
        Log.d(TAG, Generator.LOG_EXITING + Thread.currentThread().getStackTrace()[2].getMethodName());
        return movieThumbs;
    }
}
