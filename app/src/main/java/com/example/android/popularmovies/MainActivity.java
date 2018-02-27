package com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import com.example.android.popularmovies.util.MovieDBJsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements PosterAdapter.PosterAdapterOnClickHandler {

    private final static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PosterAdapter mPosterAdapter;

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
    }

    private void fetchMoviesData() {
        //TODO sort order from settings
        String sortOrder = "top_rated";
        new FetchMoviesTask().execute(sortOrder);
    }

    @Override
    public void onClick(Movie movie) {
        Log.v(TAG, movie.toString());
        Intent movieDetailIntent = new Intent(this, MovieDetailActivity.class);
        movieDetailIntent.putExtra(getString(R.string.parcel_movie_data), movie);
        startActivity(movieDetailIntent);
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
}
