package com.example.android.app.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "my_favourite_movies.db";
    public static final int DATABASE_VERSION = 1;
    private static String CREATE_TABLE = "CREATE TABLE ";
    private static String BEGIN_DEFINE_COLUMNS = " (";
    private static String END_DEFINE_COLUMNS = ");";
    private static String TYPE_INTEGER = " INTEGER";
    private static String TYPE_TEXT = " TEXT";
    private static String TYPE_DOUBLE = " REAL";
    private static String TYPE_LONG = " NUMERIC";
    private static String PRIMARY_KEY = " PRIMARY KEY ";
    private static String AUTO_INCREMENT = " AUTOINCREMENT, ";
    private static String NOT_NULL = " NOT NULL, ";
    private static String NOT_NULL_END = " NOT NULL";
    private static String DROP_TABLE = "DROP TABLE IF EXISTS ";

    public FavouriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_MOVIES_TABLE =
                CREATE_TABLE + FavouriteMoviesContract.MovieTableEntry.TABLE_NAME +
                        BEGIN_DEFINE_COLUMNS +
                        FavouriteMoviesContract.MovieTableEntry._ID + TYPE_INTEGER + PRIMARY_KEY + AUTO_INCREMENT +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_MOVIE_ID + TYPE_INTEGER + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_TITLE + TYPE_TEXT + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_OVERVIEW + TYPE_TEXT + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_POSTER_PATH + TYPE_TEXT + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_RELEASE_DATE + TYPE_TEXT + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_POPULARITY + TYPE_DOUBLE + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_COUNT + TYPE_LONG + NOT_NULL +
                        FavouriteMoviesContract.MovieTableEntry.COLUMN_VOTE_AVERAGE + TYPE_DOUBLE + NOT_NULL_END +
                        END_DEFINE_COLUMNS;
        db.execSQL(SQL_CREATE_FAVOURITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + FavouriteMoviesContract.MovieTableEntry.TABLE_NAME);
        // For this case, will not really implement an upgrade process
        onCreate(db);
    }
}
