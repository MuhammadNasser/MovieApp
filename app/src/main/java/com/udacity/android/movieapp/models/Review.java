package com.udacity.android.movieapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Muhammad on 4/27/2017
 * * Creates a review object.
 */
public class Review {

    private String id;
    private String author;
    private String content;

    public Review(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.author = trailer.getString("author");
        this.content = trailer.getString("content");
    }

    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
