package com.example.android.app.moviesapp;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.android.app.moviesapp.data.FavouriteMoviesContract;

import java.util.ArrayList;

class DataBaseConnection {

    private static String TAG = NetworkConnection.class.getSimpleName();

    static ArrayList<Movie> fetchPersistentFavourites(Context context) {
        Log.d(TAG, Generator.LOG_ENTERING + Thread.currentThread().getStackTrace()[2].getMethodName());
        Cursor cursor = null;
        ArrayList<Movie> lst = new ArrayList<>();
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
}
