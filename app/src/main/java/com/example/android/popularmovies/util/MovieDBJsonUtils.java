package com.example.android.popularmovies.util;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Review;
import com.example.android.popularmovies.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adibella on 23/02/2018.
 */

public class MovieDBJsonUtils {
    private final static String TAG = MovieDBJsonUtils.class.getSimpleName();
    private final static String KEY_RESULTS = "results";
    private final static String KEY_ID = "id";
    private final static String KEY_ORIGINAL_TITLE = "original_title";
    private final static String KEY_POSTER_PATH = "poster_path";
    private final static String KEY_OVERVIEW = "overview";
    private final static String KEY_VOTE_AVERAGE = "vote_average";
    private final static String KEY_RELEASE_DATE = "release_date";
    private final static String KEY_STATUS_CODE = "status_code";
    private final static String KEY_TRAILER_KEY = "key";
    private final static String KEY_TRAILER_NAME = "name";
    private final static String KEY_TRAILER_TYPE = "type";
    private final static String VALUE_TRAILER_TYPE = "Trailer";
    private final static String KEY_REVIEW_AUTHOR = "author";
    private final static String KEY_REVIEW_CONTENT = "content";
    private final static String KEY_REVIEW_URL = "url";
    private final static int ERROR_CODE_INVALID_API_KEY = 7;
    private final static int ERROR_CODE_RESOURCE_NOT_FOUND = 34;

    public static Movie[] getMoviesDataFromJson(Context context, String moviesJsonString)
    throws JSONException {
        Log.v(TAG, "Json data: " + moviesJsonString);

        Movie[] parsedMoviesData = null;

        JSONObject moviesDataJson = new JSONObject(moviesJsonString);

        if (moviesDataJson.has(KEY_STATUS_CODE)) {
            int errorCode = moviesDataJson.getInt(KEY_STATUS_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case ERROR_CODE_INVALID_API_KEY:
                    return null;
                case ERROR_CODE_RESOURCE_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray resultsArray = moviesDataJson.getJSONArray(KEY_RESULTS);

        parsedMoviesData = new Movie[resultsArray.length()];

        for (int i = 0; i < resultsArray.length(); ++i) {
            JSONObject movieJson = resultsArray.getJSONObject(i);
            int id = movieJson.getInt(KEY_ID);
            String originalTitle = movieJson.getString(KEY_ORIGINAL_TITLE);
            String poster_path = movieJson.getString(KEY_POSTER_PATH);
            String overview = movieJson.getString(KEY_OVERVIEW);
            Double voteAverage = movieJson.getDouble(KEY_VOTE_AVERAGE);
            String releaseDate = movieJson.getString(KEY_RELEASE_DATE);

            parsedMoviesData[i] = new Movie(id,
                    originalTitle,
                    NetworkUtils.buildPosterUrl(poster_path),
                    poster_path,
                    overview,
                    voteAverage,
                    releaseDate);
            Log.v(TAG, poster_path);
        }

        return parsedMoviesData;
    }

    public static List<Trailer> getTrailersFromJsonResponse(String jsonResponse) {
        List<Trailer> trailers = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray trailersArray = jsonObject.getJSONArray(KEY_RESULTS);
            if (trailersArray != null) {
                for (int i = 0; i < trailersArray.length(); i++) {
                    JSONObject trailerJSON = trailersArray.getJSONObject(i);
                    String type = trailerJSON.optString(KEY_TRAILER_TYPE);
                    if (type.equals(VALUE_TRAILER_TYPE)) {
                        String id = trailerJSON.optString(KEY_ID);
                        String key = trailerJSON.optString(KEY_TRAILER_KEY);
                        String name = trailerJSON.optString(KEY_TRAILER_NAME);
                        trailers.add(new Trailer(id, key, name));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public static List<Review> getReviewsFromJsonResponse(String jsonResponse) {
        List<Review> reviews = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray reviewsArray = jsonObject.getJSONArray(KEY_RESULTS);
            if (reviewsArray != null) {
                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject reviewJSON = reviewsArray.getJSONObject(i);
                    String id = reviewJSON.optString(KEY_ID);
                    String author = reviewJSON.optString(KEY_REVIEW_AUTHOR);
                    String content = reviewJSON.optString(KEY_REVIEW_CONTENT);
                    String url = reviewJSON.optString(KEY_REVIEW_URL);
                    reviews.add(new Review(id, author, content, url));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }
}
