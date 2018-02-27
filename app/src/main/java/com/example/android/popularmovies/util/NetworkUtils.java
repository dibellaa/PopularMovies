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
    public final static String MOVIEDB_BASE_URL =
            "https://api.themoviedb.org/3/movie";
    public final static String MOVIEDB_POSTER_BASE_URL =
            "http://image.tmdb.org/t/p/w185";
    final static String API_KEY_PARAM = "api_key";
    final static String API_KEY_VALUE = "insert_api_key_here";
    private static Toast mToast;

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
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static Uri buildPosterUrl(String path) {
        Log.v(TAG, "String path: " + path);
        Uri posterUri = Uri.parse(MOVIEDB_POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(path).build();
        URL url = null;
        try {
            url = new URL(posterUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Poster URI " + url);
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

