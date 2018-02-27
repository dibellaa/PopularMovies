package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetailActivity extends AppCompatActivity {

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
    private TextView originalTitleTV;
    private TextView plotTV;
    private TextView releaseDateTV;
    private TextView ratingTV;
    private ImageView posterIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Movie movie = getIntent().getParcelableExtra(getString(R.string.parcel_movie_data));
        Log.v(TAG, movie.toString());

        posterIV = findViewById(R.id.poster_IV_detail);
        movie.displayPoster(this, posterIV);

        originalTitleTV = findViewById(R.id.original_title_tv);
        originalTitleTV.setText(movie.getOriginalTitle());

        plotTV = findViewById(R.id.plot_tv);
        plotTV.setText(movie.getOverview());

        releaseDateTV = findViewById(R.id.release_date_tv);
        releaseDateTV.setText(movie.getReleaseDate());

        ratingTV = findViewById(R.id.rating_tv);
        ratingTV.setText(String.valueOf(movie.getVoteAverage()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detail, menu);
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
}
