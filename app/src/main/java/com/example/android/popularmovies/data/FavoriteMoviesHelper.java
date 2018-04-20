package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;

public class FavoriteMoviesHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favoriteMovies.db";
    private static final int DATABASE_VERSION = 1;

    public FavoriteMoviesHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVORITE_MOVIES_TABLE =
                "CREATE TABLE " + FavoriteMoviesEntry.TABLE_NAME + " ("
                + FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY, "
                + FavoriteMoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + FavoriteMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, "
                + FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + FavoriteMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + FavoriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL);"
                + " UNIQUE (" + FavoriteMoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
