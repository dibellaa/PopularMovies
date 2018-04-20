package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmovies.data.FavoriteMoviesContract;
import com.example.android.popularmovies.util.MovieDBJsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        PosterAdapter.PosterAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener,
        LoaderCallbacks<List<Movie>> {

    private final static String TAG = MainActivity.class.getSimpleName();

    private static final String[] MAIN_FAVORITE_MOVIES_PROJECTION = {
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_MOVIE_ID,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_OVERVIEW,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_POSTER_PATH,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_RELEASE_DATE,
            FavoriteMoviesContract.FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE
    };

    private static final int INDEX_MOVIE_ID = 0;
    private static final int INDEX_ORIGINAL_TITLE = 1;
    private static final int INDEX_OVERVIEW = 2;
    private static final int INDEX_POSTER_PATH = 3;
    private static final int INDEX_RELEASE_DATE = 4;
    private static final int INDEX_VOTE_AVERAGE = 5;

    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;
    public static boolean PREFERENCES_UPDATED = false;
    public static final int MOVIES_LOADER_ID = 0;
    private boolean isFavoriteEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_posters);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns(this));
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        LoaderCallbacks<List<Movie>> callback = MainActivity.this;

        getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, callback);

        PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this);
    }

    private int numberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int nColumns = (int)(dpWidth / scalingFactor);
        if (nColumns < 2) return 2;
        return nColumns;
    }

    @Override
    public void onClick(Movie movie) {
        Log.v(TAG, movie.toString());
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(getString(R.string.parcel_movie_data), movie);
        startActivity(movieDetailIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_UPDATED = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popularmovies, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_UPDATED) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
            PREFERENCES_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, Bundle bundle) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mMovieData = null;

            @Override
            protected void onStartLoading() {
                if (mMovieData != null) {
                    deliverResult(mMovieData);
                } else {
                    forceLoad();
                }
            }

            public void deliverResult(List<Movie> movies) {
                mMovieData = movies;
                super.deliverResult(movies);
            }

            @Override
            public List<Movie> loadInBackground() {
                SharedPreferences pref = PreferenceManager
                        .getDefaultSharedPreferences(MainActivity.this);
                isFavoriteEnabled = pref.getBoolean(MainActivity.this.getString(R.string.cb_key), false);
                if (isFavoriteEnabled) {
                    List<Movie> returnMovies = new ArrayList<>();
                    Cursor cursor = getContentResolver().query(FavoriteMoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                            MAIN_FAVORITE_MOVIES_PROJECTION,
                            null,
                            null,
                            null);
                    if (cursor != null) {
                        for (int i = 0; i < cursor.getCount(); i++) {
                            cursor.moveToPosition(i);
                            int id = cursor.getInt(INDEX_MOVIE_ID);
                            String originalTitle = cursor.getString(INDEX_ORIGINAL_TITLE);
                            String posterPath = cursor.getString(INDEX_POSTER_PATH);
                            String overview = cursor.getString(INDEX_OVERVIEW);
                            Double voteAverage = cursor.getDouble(INDEX_VOTE_AVERAGE);
                            String releaseDate = cursor.getString(INDEX_RELEASE_DATE);

                            returnMovies.add(new Movie(id,
                                    originalTitle,
                                    NetworkUtils.buildPosterUrl(posterPath),
                                    posterPath,
                                    overview,
                                    voteAverage,
                                    releaseDate));
                        }
                        cursor.close();
                    }
                    return returnMovies;
                } else {
                    String orderKey = MainActivity.this.getString(R.string.pref_order_key);
                    String defaultValue = MainActivity.this.getString(R.string.pref_order_popular);
                    String sortOrder = pref.getString(orderKey, defaultValue);

                    URL requestURL = NetworkUtils.buildUrl(sortOrder);

                    try {
                        String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);
                        List<Movie> jsonMoviesData = Arrays.asList(MovieDBJsonUtils
                                .getMoviesDataFromJson(MainActivity.this, jsonMoviesResponse));
                        return jsonMoviesData;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mPosterAdapter.setData(movies);
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }
}
