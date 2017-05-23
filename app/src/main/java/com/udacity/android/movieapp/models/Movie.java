package com.udacity.android.movieapp.models;

import android.database.Cursor;

import com.udacity.android.movieapp.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Muhammad on 4/7/2017
 * * Creates a movie object.
 * The Serializable is generated to transfer object by Intents.
 */

public class Movie implements Serializable {

    private int id;
    private String title; // original_title
    private String image; // poster_path
    private String image2; //movie_details_image
    private String overview; // over_view_Movie
    private int rating; // vote_average
    private int vote; // vote_count
    private String date; // release_date

    /**
     * Constructor for a movie object it's fill data in Strings from JSONObject.
     */
    public Movie(JSONObject movie) throws JSONException {
        this.id = movie.optInt("id");
        this.title = movie.optString("original_title");
        this.image = movie.optString("poster_path");
        this.image2 = movie.optString("backdrop_path");
        this.overview = movie.optString("overview");
        this.rating = movie.optInt("vote_average");
        this.vote = movie.optInt("vote_count");
        this.date = movie.optString("release_date");
    }

    /**
     * Constructor for a movie object it's fill data in Strings from Cursor.
     */
    public Movie(Cursor cursor) {
        this.id = cursor.getInt(MainActivity.COL_MOVIE_ID);
        this.title = cursor.getString(MainActivity.COL_TITLE);
        this.image = cursor.getString(MainActivity.COL_IMAGE);
        this.image2 = cursor.getString(MainActivity.COL_IMAGE2);
        this.overview = cursor.getString(MainActivity.COL_OVERVIEW);
        this.rating = cursor.getInt(MainActivity.COL_RATING);
        this.vote = cursor.getInt(MainActivity.COL_VOTE);
        this.date = cursor.getString(MainActivity.COL_DATE);
    }

    /**
     * Gets the data that's added in Strings.
     *
     * @return Strings data
     */
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getImage2() {
        return image2;
    }

    public String getOverview() {
        return overview;
    }

    public int getRating() {
        return rating;
    }

    public int getVote() {
        return vote;
    }

    public String getDate() {
        return date;
    }
}
