package com.udacity.android.movieapp;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.movieapp.adapters.HomeMoviesAdapter;
import com.udacity.android.movieapp.data.MovieContract;
import com.udacity.android.movieapp.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

/**
 * Created by Muhammad on 4/7/2017
 * Main activity of the application.
 */
public class MainActivity extends AppCompatActivity {

    public static final String POPULARITY = "popular";
    public static final String RATING = "top_rated";
    private static final String FAVORITE = "favorite";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private String mSortBy = POPULARITY;
    private String apiKey;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_IMAGE,
            MovieContract.MovieEntry.COLUMN_IMAGE2,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_VOTE,
            MovieContract.MovieEntry.COLUMN_DATE
    };

    public static final int COL_MOVIE_ID = 1;
    public static final int COL_TITLE = 2;
    public static final int COL_IMAGE = 3;
    public static final int COL_IMAGE2 = 4;
    public static final int COL_OVERVIEW = 5;
    public static final int COL_RATING = 6;
    public static final int COL_VOTE = 7;
    public static final int COL_DATE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set status bar Transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            int color;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = getResources().getColor(R.color.redTransparent, null);
            } else {
                // noinspection deprecation
                color = getResources().getColor(R.color.redTransparent);
            }
            getWindow().setStatusBarColor(color);
        }
        setContentView(R.layout.activity_main);
        setToolBar();
        ButterKnife.bind(this);

        apiKey = getResources().getString(R.string.api_key);
        getMoviesFromIMDb(mSortBy);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem action_sort_by_popularity = menu.findItem(R.id.action_sort_by_popularity);
        MenuItem action_sort_by_rating = menu.findItem(R.id.action_sort_by_rating);
        MenuItem action_sort_by_favorite = menu.findItem(R.id.action_sort_by_favorite);

        if (mSortBy.contains(POPULARITY)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_popularity.setChecked(true);
            }
        } else if (mSortBy.contains(RATING)) {
            if (!action_sort_by_rating.isChecked()) {
                action_sort_by_rating.setChecked(true);
            }
        } else if (mSortBy.contentEquals(FAVORITE)) {
            if (!action_sort_by_popularity.isChecked()) {
                action_sort_by_favorite.setChecked(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_sort_by_popularity:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = POPULARITY;
                getMoviesFromIMDb(mSortBy);
                return true;
            case R.id.action_sort_by_rating:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = RATING;
                getMoviesFromIMDb(mSortBy);
                return true;
            case R.id.action_sort_by_favorite:
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }
                mSortBy = FAVORITE;
                new FavoritesMoviesTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void setToolBar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar);
        assert toolBar != null;
        toolBar.setBackgroundResource(R.color.colorPrimary);
        View actionBarView = getLayoutInflater().inflate(R.layout.toolbar_customview, toolBar, false);
        actionBarView.setBackgroundResource(R.color.colorPrimary);

        setSupportActionBar(toolBar);

        if (Build.VERSION.SDK_INT >= 21) {
            // Set the status bar to dark-semi-transparentish
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // Set paddingTop of toolbar to height of status bar.
            // Fixes statusbar covers toolbar issue
            int statusBarHeight = getStatusBarHeight();
            toolBar.setPadding(0, statusBarHeight, 0, 0);
        }
        TextView textViewTitle = (TextView) actionBarView.findViewById(R.id.textViewTitle);

        // Set up the drawer.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(actionBarView);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        textViewTitle.setText(getString(R.string.popular_movies));
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = (int) getResources().getDimension(resourceId);
        }
        return result;
    }

    /**
     * If device has Internet the magic happens when app launches. The app will start the process
     * of collecting data from the API and present it to the user.
     * If the device has no connectivity it will display a Toast explaining that app needs
     * Internet to work properly.
     **/
    private void getMoviesFromIMDb(String mSortBy) {
        if (isNetworkAvailable()) {
            // Execute task
            MovieAsyncTask movieAsyncTask = new MovieAsyncTask(apiKey, mSortBy);
            movieAsyncTask.execute();
        } else {
            Toast.makeText(this, getString(R.string.internet_connection), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Checks if there is Internet accessible.
     *
     * @return True if there is Internet. False if not.
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MovieAsyncTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        private final String LOG_TAG = MovieAsyncTask.class.getCanonicalName();
        private final String mApiKey;
        private String sortBy;

        /**
         * {@link java.lang.reflect.Constructor}
         *
         * @param sortBy  sorting Movies.
         * @param mApiKey TMDb API key.
         */
        private MovieAsyncTask(String mApiKey, String sortBy) {
            this.mApiKey = mApiKey;
            this.sortBy = sortBy;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                URL url = getApiUrl();

                // Start connecting to get JSON
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder builder = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Adds '\n' at last line if not already there.
                    // This supposedly makes it easier to debug.
                    builder.append(line).append("");
                }
                if (builder.length() == 0) {
                    return null;
                }
                moviesJsonStr = builder.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "error", e);
                return null;
            } finally {
                // Tidy up: release url connection and buffered reader
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "close", e);
                    }
                }
            }
            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * Extracts data from the JSON object and returns an Array of movie objects.
         *
         * @param moviesJsonStr JSON string to be traversed
         * @return ArrayList of Movie objects
         * @throws JSONException throws JSONException
         */
        private ArrayList<Movie> getMoviesDataFromJson(String moviesJsonStr) throws JSONException {
            final String TAG_RESULTS = "results";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(TAG_RESULTS);

            ArrayList<Movie> movies = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject movieInfo = resultsArray.getJSONObject(i);
                //
                Movie movieItem = new Movie(movieInfo);
                movies.add(movieItem);
            }
            return movies;
        }

        /**
         * Creates and returns an URL.
         *
         * @return URL formatted with parameters for the API
         * @throws MalformedURLException throws MalformedURLException
         */
        private URL getApiUrl() throws MalformedURLException {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + sortBy;
            final String API_KEY_PARAM = "api_key";
            Uri builtUri;
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, mApiKey)
                    .build();
            return new URL(builtUri.toString());

        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            progressBar.setVisibility(View.GONE);
            if (movies != null) {
                //setting recyclerView layout and adapter.
                StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                layoutManager1.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mRecyclerView.setLayoutManager(layoutManager1);
                mRecyclerView.setHasFixedSize(false);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                mRecyclerView.setAdapter(new HomeMoviesAdapter(movies, mRecyclerView, MainActivity.this));
            }
        }
    }

    private class FavoritesMoviesTask extends AsyncTask<Void, Void, ArrayList<Movie>> {

        private Context mContext;

        /**
         * {@link java.lang.reflect.Constructor}
         *
         * @param context Activity Context.
         */
        private FavoritesMoviesTask(Context context) {
            mContext = context;
        }

        /**
         * Extracts data from the Cursor database and returns an Array of movie objects.
         *
         * @param cursor cursor of database
         * @return ArrayList of Movie objects
         */
        private ArrayList<Movie> getFavoriteMoviesDataFromCursor(Cursor cursor) {
            ArrayList<Movie> movies = new ArrayList<>();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie(cursor);
                    movies.add(movie);
                } while (cursor.moveToNext());
                cursor.close();
            }
            return movies;
        }

        @Override
        protected ArrayList<Movie> doInBackground(Void... params) {
            Cursor cursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
            return getFavoriteMoviesDataFromCursor(cursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            if (movies != null) {
                //setting recyclerView layout and adapter.
                StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                layoutManager1.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                mRecyclerView.setLayoutManager(layoutManager1);
                mRecyclerView.setHasFixedSize(false);
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());

                mRecyclerView.setAdapter(new HomeMoviesAdapter(movies, mRecyclerView, MainActivity.this));
            }
        }
    }


}
