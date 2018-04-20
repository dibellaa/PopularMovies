package com.example.android.popularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by adibella on 23/02/2018.
 */

public class NetworkUtils {
    private final static String TAG = NetworkUtils.class.getSimpleName();
    private final static String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    private final static String MOVIEDB_POSTER_BASE_URL =
            "http://image.tmdb.org/t/p/w185";
    private final static String MOVIEDB_VIDEOS_PATH = "videos";
    private final static String API_KEY_PARAM = "api_key";
    private final static String API_KEY_VALUE = "insert_api_key_here";
    private static final String MOVIEDB_REVIEWS_PATH = "reviews";
    private static Toast mToast;
    private final static String YOUTUBE_THUMB_URL =
            "https://img.youtube.com/vi";
    private final static String YOUTUBE_THUMB_IMAGE_PATH = "0.jpg";
    private final static String YOUTUBE_APP_URL =
            "vnd.youtube:";
    private final static String YOUTUBE_BROWSER_URL =
            "http://www.youtube.com/watch";
    private final static String YOUTUBE_VIDEO_KEY = "v";


    public static URL buildUrl(String sortOrder) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(sortOrder)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static Uri buildPosterUrl(String path) {
//        Log.v(TAG, "String path: " + path);
        Uri posterUri = Uri.parse(MOVIEDB_POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(path).build();
        URL url = null;
        try {
            url = new URL(posterUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "Poster URI " + url);
        return Uri.parse(url.toString());
    }

    public static URL buildVideosUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIEDB_VIDEOS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildReviewsUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIEDB_REVIEWS_PATH)
                .appendQueryParameter(API_KEY_PARAM, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static Uri buildYouTubeThumbUrl(String path) {
        Uri thumbUri = Uri.parse(YOUTUBE_THUMB_URL).buildUpon()
                .appendPath(path)
                .appendPath(YOUTUBE_THUMB_IMAGE_PATH)
                .build();
        URL url = null;
        try {
            url = new URL(thumbUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Uri.parse(url.toString());
    }

    public static Uri buildYouTubeAppUrl(String path) {
        return Uri.parse(YOUTUBE_APP_URL).buildUpon()
                .appendPath(path)
                .build();
    }

    public static Uri buildYouTubeBrowserUrl(String key) {
        Uri youTubeBrowserUri = Uri.parse(YOUTUBE_BROWSER_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_VIDEO_KEY, key)
                .build();
        URL url = null;
        try {
            url = new URL(youTubeBrowserUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return Uri.parse(url.toString());
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getNetworkInfo(context);
        return networkInfo != null && networkInfo.isConnected();
    }

    public static boolean checkConnection(Context context) {
        Boolean isConnected = isConnected(context);
        String msg = context.getString(R.string.no_connection_msg);
        if(!isConnected){
            if(mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            mToast.show();
        }
        return isConnected;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

