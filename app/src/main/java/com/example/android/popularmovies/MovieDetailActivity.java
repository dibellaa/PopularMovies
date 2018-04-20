package com.example.android.popularmovies;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.FavoriteMoviesContract.FavoriteMoviesEntry;
import com.example.android.popularmovies.util.MovieDBJsonUtils;
import com.example.android.popularmovies.util.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailActivity extends AppCompatActivity
        implements TrailerAdapter.TrailerAdapterOnClickHandler,
        ReviewAdapter.ReviewAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List> {

    private final static String TAG = MovieDetailActivity.class.getSimpleName();
    private static final int TRAILERS_LOADER_ID = 1;
    private static final int REVIEWS_LOADER_ID = 2;
    private TextView originalTitleTV;
    private TextView plotTV;
    private TextView releaseDateTV;
    private TextView ratingTV;
    private ImageView posterIV;
    private ImageButton starButton;
    private Movie movie;
    private RecyclerView mTrailerRecyclerView;
    private TrailerAdapter mTrailerAdapter;
    private RecyclerView mReviewRecyclerView;
    private ReviewAdapter mReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        movie = getIntent().getParcelableExtra(getString(R.string.parcel_movie_data));
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

        starButton = findViewById(R.id.button_star);
        Cursor cursor = getContentResolver().query(FavoriteMoviesEntry.CONTENT_URI,
                new String[] {FavoriteMoviesEntry.COLUMN_MOVIE_ID},
                FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ",
                new String[] {String.valueOf(movie.getId())},
                null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                starButton.setSelected(true);
                starButton.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                starButton.setSelected(false);
                starButton.setImageResource(android.R.drawable.btn_star_big_off);
            }
            cursor.close();
        }
        mTrailerRecyclerView = findViewById(R.id.rv_trailers);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        mTrailerRecyclerView.setLayoutManager(trailerLayoutManager);
        mTrailerRecyclerView.setHasFixedSize(true);
        mTrailerAdapter = new TrailerAdapter(this);
        mTrailerRecyclerView.setAdapter(mTrailerAdapter);
        getSupportLoaderManager().initLoader(TRAILERS_LOADER_ID, null, this);

        mReviewRecyclerView = findViewById(R.id.rv_reviews);
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this);
        mReviewRecyclerView.setLayoutManager(reviewLayoutManager);
        mReviewRecyclerView.setHasFixedSize(true);
        mReviewAdapter = new ReviewAdapter(this);
        mReviewRecyclerView.setAdapter(mReviewAdapter);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER_ID, null, this);
    }

    public void onClickStar(View view) {
        Log.v(MovieDetailActivity.class.getSimpleName(), "clicked");
        ImageButton star = (ImageButton) view;
        if (!star.isSelected()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteMoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(FavoriteMoviesEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
            contentValues.put(FavoriteMoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(FavoriteMoviesEntry.COLUMN_POSTER_PATH, movie.getStringPathPoster());
            contentValues.put(FavoriteMoviesEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
            contentValues.put(FavoriteMoviesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
            getContentResolver().insert(FavoriteMoviesEntry.CONTENT_URI, contentValues);
            Log.v(MovieDetailActivity.class.getSimpleName(), "after insert");
            star.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            getContentResolver().delete(FavoriteMoviesEntry.CONTENT_URI,
                    FavoriteMoviesEntry.COLUMN_MOVIE_ID + " = ? ", new String[] {String.valueOf(movie.getId())});
            Log.v(MovieDetailActivity.class.getSimpleName(), "after delete");
            star.setImageResource(android.R.drawable.btn_star_big_off);
            MainActivity.PREFERENCES_UPDATED = true;
        }
        star.setSelected(!star.isSelected());
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

    @Override
    public void onClick(Trailer trailer) {
        Intent intent = new Intent(Intent.ACTION_VIEW, trailer.getAppUri());
        if (intent.resolveActivity(getPackageManager()) == null) {
            intent = new Intent(Intent.ACTION_VIEW, trailer.getBrowserUri());
        }
        startActivity(intent);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case TRAILERS_LOADER_ID:
                return trailersAsyncTask();
            case REVIEWS_LOADER_ID:
                return reviewsAsyncTask();
            default:
                throw new RuntimeException("Loader unknown");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, List list) {
        switch (loader.getId()) {
            case TRAILERS_LOADER_ID:
                mTrailerAdapter.setData(list);
                break;
            case REVIEWS_LOADER_ID:
                mReviewAdapter.setData(list);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @SuppressLint("StaticFieldLeak")
    private AsyncTaskLoader<List<Trailer>> trailersAsyncTask() {
        return new AsyncTaskLoader<List<Trailer>>(this) {
            private List<Trailer> mTrailers;

            @Override
            public List<Trailer> loadInBackground() {
                List<Trailer> trailers = new ArrayList<>();
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildVideosUrl(String.valueOf(movie.getId())));
                    trailers = MovieDBJsonUtils.getTrailersFromJsonResponse(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return trailers;
            }

            @Override
            public void deliverResult(@Nullable List<Trailer> data) {
                mTrailers = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (mTrailers != null) {
                    deliverResult(mTrailers);
                } else {
                    forceLoad();
                }
            }
        };
    }

    @SuppressLint("StaticFieldLeak")
    private AsyncTaskLoader<List<Review>> reviewsAsyncTask() {
        return new AsyncTaskLoader<List<Review>>(this) {
            private List<Review> mReviews;

            @Override
            public List<Review> loadInBackground() {
                List<Review> reviews = new ArrayList<>();
                try {
                    String jsonResponse = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildReviewsUrl(String.valueOf(movie.getId())));
                    reviews = MovieDBJsonUtils.getReviewsFromJsonResponse(jsonResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return reviews;
            }

            @Override
            public void deliverResult(@Nullable List<Review> data) {
                mReviews = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (mReviews != null) {
                    deliverResult(mReviews);
                } else {
                    forceLoad();
                }
            }
        };
    }

    @Override
    public void onClick(Review review) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(review.getUrl()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
