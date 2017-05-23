package com.udacity.android.movieapp.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Muhammad on 4/27/2017
 * * Creates a trailer object.
 */
public class Trailer {

    private String id;
    private String key;
    private String name;
    private String site;
    private String type;


    public Trailer(JSONObject trailer) throws JSONException {
        this.id = trailer.getString("id");
        this.key = trailer.getString("key");
        this.name = trailer.getString("name");
        this.site = trailer.getString("site");
        this.type = trailer.getString("type");
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public String getType() {
        return type;
    }
}