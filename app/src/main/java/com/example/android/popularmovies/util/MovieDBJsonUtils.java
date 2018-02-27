package com.example.android.popularmovies.util;

import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by adibella on 23/02/2018.
 */

public class MovieDBJsonUtils {
    private final static String TAG = MovieDBJsonUtils.class.getSimpleName();
    public static Movie[] getMoviesDataFromJson(Context context, String moviesJsonString)
    throws JSONException {
        final String KEY_RESULTS = "results";
        final String KEY_ORIGINAL_TITLE = "original_title";
        final String KEY_POSTER_PATH = "poster_path";
        final String KEY_OVERVIEW = "overview";
        final String KEY_VOTE_AVERAGE = "vote_average";
        final String KEY_RELEASE_DATE = "release_date";
        final String KEY_STATUS_CODE = "status_code";
        final int ERROR_CODE_INVALID_API_KEY = 7;
        final int ERROR_CODE_RESOURCE_NOT_FOUND = 34;

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
            String originalTitle = movieJson.getString(KEY_ORIGINAL_TITLE);
            String poster_path = movieJson.getString(KEY_POSTER_PATH);
            String overview = movieJson.getString(KEY_OVERVIEW);
            Double voteAverage = movieJson.getDouble(KEY_VOTE_AVERAGE);
            String releaseDate = movieJson.getString(KEY_RELEASE_DATE);

            parsedMoviesData[i] = new Movie(originalTitle,
                    NetworkUtils.buildPosterUrl(poster_path),
                    overview,
                    voteAverage,
                    releaseDate);
        }

        return parsedMoviesData;
    }
}
