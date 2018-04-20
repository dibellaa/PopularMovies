package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry.TABLE_NAME;
import static com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

public class FavoriteMoviesProvider extends ContentProvider{
    public static final int CODE_FAVORITE_MOVIES = 100;
    public static final int CODE_FAVORITE_MOVIES_WITH_ID = 101;
    private FavoriteMoviesHelper helper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteMoviesContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES, CODE_FAVORITE_MOVIES);
        matcher.addURI(authority, FavoriteMoviesContract.PATH_FAVORITE_MOVIES + "/#", CODE_FAVORITE_MOVIES_WITH_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        helper = new FavoriteMoviesHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES: {
                cursor = helper.getReadableDatabase().query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_FAVORITE_MOVIES_WITH_ID: {
                String[] selectionArguments = new String[]{uri.getLastPathSegment()};
                cursor = helper.getReadableDatabase().query(
                        TABLE_NAME,
                        projection,
                        FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Uri returnUri;
        final SQLiteDatabase db = helper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES:
                long id = db.insert(TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_MOVIES:
                numRowsDeleted = helper.getWritableDatabase().delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case CODE_FAVORITE_MOVIES_WITH_ID:
                selection = "_id=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                numRowsDeleted = helper.getWritableDatabase().delete(
                        TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
