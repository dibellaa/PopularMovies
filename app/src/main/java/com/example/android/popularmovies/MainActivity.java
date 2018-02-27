package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.popularmovies.util.MovieDBJsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements
        PosterAdapter.PosterAdapterOnClickHandler,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;
    private static boolean PREFERENCES_UPDATED = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_posters);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mRecyclerView.setAdapter(mPosterAdapter);

        fetchMoviesData();

        PreferenceManager.getDefaultSharedPreferences(this)
        .registerOnSharedPreferenceChangeListener(this);
    }

    private void fetchMoviesData() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        String orderKey = this.getString(R.string.pref_order_key);
        String defaultValue = this.getString(R.string.pref_order_popular);
        String sortOrder = pref.getString(orderKey, defaultValue);
        new FetchMoviesTask().execute(sortOrder);
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

    private class FetchMoviesTask extends AsyncTask<String, Void, Movie[]>{
        @Override
        protected Movie[] doInBackground(String... strings) {
            String sortOrder;
            if (strings.length == 0) {
                sortOrder = "popular";
            }
            sortOrder = strings[0];
            URL requestURL = NetworkUtils.buildUrl(sortOrder);

            try {
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(requestURL);
                Movie[] jsonMoviesData = MovieDBJsonUtils
                        .getMoviesDataFromJson(MainActivity.this, jsonMoviesResponse);
                return jsonMoviesData;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (null != movies) {
                mPosterAdapter.setData(movies);
            }
        }
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
            fetchMoviesData();
            PREFERENCES_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
