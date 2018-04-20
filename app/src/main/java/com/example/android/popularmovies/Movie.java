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
    private int id;
    private String originalTitle;
    private Uri pathPoster;
    private String stringPathPoster;
    private String overview;
    private Double voteAverage;
    private String releaseDate;

    public Movie() {

    }

    public Movie(int id, String originalTitle, Uri pathPoster, String stringPathPoster, String overview, Double voteAverage, String releaseDate) {
        this.id = id;
        this.originalTitle = originalTitle;
        this.pathPoster = pathPoster;
        this.stringPathPoster = stringPathPoster;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        originalTitle = in.readString();
        pathPoster = in.readParcelable(Uri.class.getClassLoader());
        stringPathPoster = in.readString();
        overview = in.readString();
        if (in.readByte() == 0) {
            voteAverage = null;
        } else {
            voteAverage = in.readDouble();
        }
        releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeParcelable(pathPoster, flags);
        dest.writeString(stringPathPoster);
        dest.writeString(overview);
        if (voteAverage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(voteAverage);
        }
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStringPathPoster() {
        return stringPathPoster;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                "originalTitle='" + originalTitle + '\'' +
                ", pathPoster=" + pathPoster +
                ", stringPathPoster=" + stringPathPoster +
                ", overview='" + overview + '\'' +
                ", voteAverage=" + voteAverage +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
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
