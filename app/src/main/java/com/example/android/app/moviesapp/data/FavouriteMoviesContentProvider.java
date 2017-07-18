package com.example.android.app.moviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.app.moviesapp.data.FavouriteMoviesContract.MovieTableEntry.TABLE_NAME;

public class FavouriteMoviesContentProvider extends ContentProvider {
    private static final int FAVOURITE_MOVIES = 100;

    private static final int FAVOURITE_MOVIES_WITH_MOVIE_ID = 101;

    private static final int FAVOURITE_MOVIES_URL_ID_INDEX = 1;

    private static final String UNSUPPORTED_EXCEPTION = "Unsupported : ";

    private static final String FAILED_INSERT_EXCEPTION = "Failed to insert row into : ";

    private static final String GET_TYPE_DIR = "vnd.android.cursor.dir";

    private static final String GET_TYPE_REC = "vnd.android.cursor.item";

    private static final String TABLE_TYPE_SEPARATOR = "/";

    private static final String ITEM_SEPARATOR = "/#";

    private static final String QUERY_WHERE_STRING = "movie_id=?";

    private static final String DELETE_WHERE_STRING = "movie_id=?";
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavouriteMoviesDbHelper mFaveMovieDbHelper;

    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the task directory and a single item by ID.
         */
        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAVOURITE_MOVIES, FAVOURITE_MOVIES);
        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAVOURITE_MOVIES + ITEM_SEPARATOR, FAVOURITE_MOVIES_WITH_MOVIE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFaveMovieDbHelper = new FavouriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFaveMovieDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case FAVOURITE_MOVIES:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case FAVOURITE_MOVIES_WITH_MOVIE_ID:
                String id = uri.getPathSegments().get(FAVOURITE_MOVIES_URL_ID_INDEX); // 0 = tasks portion of the path
                String mSelection = QUERY_WHERE_STRING;
                String[] mSelectionArgs = new String[]{id};
                retCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVOURITE_MOVIES:
                return GET_TYPE_DIR + TABLE_TYPE_SEPARATOR + FavouriteMoviesContract.AUTHORITY + TABLE_TYPE_SEPARATOR + FavouriteMoviesContract.PATH_FAVOURITE_MOVIES;
            case FAVOURITE_MOVIES_WITH_MOVIE_ID:
                return GET_TYPE_REC + TABLE_TYPE_SEPARATOR + FavouriteMoviesContract.AUTHORITY + TABLE_TYPE_SEPARATOR + FavouriteMoviesContract.PATH_FAVOURITE_MOVIES;
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFaveMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case FAVOURITE_MOVIES:
                long id = db.insert(TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavouriteMoviesContract.MovieTableEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException(FAILED_INSERT_EXCEPTION + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFaveMovieDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int taskDeleted = 0;
        switch (match) {
            case FAVOURITE_MOVIES_WITH_MOVIE_ID:
                String id = uri.getPathSegments().get(FAVOURITE_MOVIES_URL_ID_INDEX);
                taskDeleted = db.delete(TABLE_NAME, DELETE_WHERE_STRING, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException(UNSUPPORTED_EXCEPTION + uri);
        }
        if (taskDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return taskDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // not yet implemented
        return 0;
    }
}
