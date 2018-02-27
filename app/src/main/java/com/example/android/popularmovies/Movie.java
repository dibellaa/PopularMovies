package com.example.android.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.example.android.popularmovies.util.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by adibella on 26/02/2018.
 */

public class Movie implements Parcelable{
    private String originalTitle;
    private Uri pathPoster;
    private String overview;
    private Double voteAverage;
    private String releaseDate;

    public Movie() {

    }

    public Movie(String originalTitle, Uri pathPoster, String overview, Double voteAverage, String releaseDate) {
        this.originalTitle = originalTitle;
        this.pathPoster = pathPoster;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        pathPoster = in.readParcelable(Uri.class.getClassLoader());
        overview = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
        releaseDate = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Uri getPathPoster() {
        return pathPoster;
    }

    public void setPathPoster(Uri pathPoster) {
        this.pathPoster = pathPoster;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "originalTitle='" + originalTitle + '\'' +
                ", pathPoster=" + pathPoster +
                ", overview='" + overview + '\'' +
                ", voteAverage=" + voteAverage +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeParcelable(pathPoster, i);
        parcel.writeString(overview);
        if (voteAverage == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(voteAverage);
        }
        parcel.writeString(releaseDate);
    }

    public void displayPoster(final Context context, ImageView imageView) {
        Uri imageToDisplay = this.pathPoster;
        Picasso.with(context)
                .load(imageToDisplay)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.image_error)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        NetworkUtils.checkConnection(context);
                    }
                });
    }
}
