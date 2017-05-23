package com.udacity.android.movieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.android.movieapp.adapters.TrailerAdapter;
import com.udacity.android.movieapp.data.MovieContract;
import com.udacity.android.movieapp.models.Movie;
import com.udacity.android.movieapp.models.Review;
import com.udacity.android.movieapp.models.Trailer;

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
 * Activity for showing movie details.
 */
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MOVIE_OBJECT = "movie";

    @BindView(R.id.textViewName)
    TextView textViewName;
    @BindView(R.id.textViewRelease)
    TextView textViewRelease;
    @BindView(R.id.textViewOverView)
    TextView textViewOverView;
    @BindView(R.id.textViewRating)
    TextView textViewRating;
    @BindView(R.id.textViewVoteCount)
    TextView textViewVoteCount;

    @BindView(R.id.imageViewPoster)
    ImageView imageViewPoster;
    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;
    @BindView(R.id.imageViewFavorite)
    ImageView imageViewFavorite;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.reviews)
    LinearLayout linearLayout;
    @BindView(R.id.reviewsRecyclerView)
    RecyclerView reviewsRecyclerView;

    private String apiKey;
    private Movie movie;
    Trailer trailer;
    ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        setToolBar();

        Intent intent = getIntent();
        movie = (Movie) intent.getSerializableExtra(MOVIE_OBJECT);

        apiKey = getResources().getString(R.string.api_key);

        setDetails(movie);
        getDataFromIMDb(String.valueOf(movie.getId()), true);
        getDataFromIMDb(String.valueOf(movie.getId()), false);

        imageViewFavorite.setOnClickListener(this);
    }

    private void setDetails(Movie movie) {
        textViewName.setText(movie.getTitle());
        textViewRelease.setText(movie.getDate());
        textViewOverView.setText(movie.getOverview());
        textViewRating.setText(" " + movie.getRating() + "/10");
        textViewVoteCount.setText(String.valueOf(movie.getVote()));

        Picasso.with(this).load(Utility.buildImageUrl(154, movie.getImage())).
                placeholder(R.drawable.placeholder_poster).
                error(R.drawable.warning_poster).
                into(imageViewPoster);
        Picasso.with(this).load(Utility.buildImageUrl(500, movie.getImage2())).
                placeholder(R.drawable.placeholder).
                error(R.drawable.warning).
                into(imageViewBack);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int isFavorite = Utility.isFavorite(DetailsActivity.this, movie.getId());
        imageViewFavorite.setImageResource(isFavorite == 1 ?
                R.drawable.favourite_star :
                R.drawable.favourite);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (movie != null) {
            getMenuInflater().inflate(R.menu.menu_activity_detail, menu);

            MenuItem action_share = menu.findItem(R.id.action_share);

            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);

            if (trailer != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        } else {
            //noinspection deprecation
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        }
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Shared by Udacity " + getResources().getString(R.string.app_name) + ": " + movie.getTitle() + " " +
                "http://www.youtube.com/watch?v=" + trailer.getKey());
        return shareIntent;
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
            // Fixes statusBar covers toolbar issue
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
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewTitle.setText(getString(R.string.movie_details));

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
     * <p>
     * If the device has no connectivity it will display a Toast explaining that app needs
     * Internet to work properly.
     **/
    private void getDataFromIMDb(String movieID, boolean isReview) {
        if (isNetworkAvailable()) {
            // Execute task
            if (isReview) {
                new ReviewsAsyncTask(apiKey, movieID).execute();
            } else {
                new TrailerAsyncTask(apiKey, movieID).execute();
            }
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

    @Override
    public void onClick(View v) {
        if (v == imageViewFavorite) {
            // check if movie is in favorites or not
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... params) {
                    return Utility.isFavorite(DetailsActivity.this, movie.getId());
                }

                @Override
                protected void onPostExecute(Integer isFavorite) {
                    // if it is in favorites
                    if (isFavorite == 1) {
                        // delete from favorites
                        new AsyncTask<Void, Void, Integer>() {
                            @Override
                            protected Integer doInBackground(Void... params) {
                                return DetailsActivity.this.getContentResolver().delete(
                                        MovieContract.MovieEntry.CONTENT_URI,
                                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                        new String[]{Integer.toString(movie.getId())}
                                );
                            }

                            @Override
                            protected void onPostExecute(Integer rowsDeleted) {
                                imageViewFavorite.setImageResource(R.drawable.favourite);
                                Toast.makeText(DetailsActivity.this, getString(R.string.removed_from_favorites), Toast.LENGTH_SHORT).show();
                            }
                        }.execute();
                    }
                    // if it is not in favorites
                    else {
                        // add to favorites
                        new AsyncTask<Void, Void, Uri>() {
                            @Override
                            protected Uri doInBackground(Void... params) {
                                ContentValues values = new ContentValues();

                                values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                                values.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
                                values.put(MovieContract.MovieEntry.COLUMN_IMAGE, movie.getImage());
                                values.put(MovieContract.MovieEntry.COLUMN_IMAGE2, movie.getImage2());
                                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
                                values.put(MovieContract.MovieEntry.COLUMN_RATING, movie.getRating());
                                values.put(MovieContract.MovieEntry.COLUMN_VOTE, movie.getVote());
                                values.put(MovieContract.MovieEntry.COLUMN_DATE, movie.getDate());

                                return DetailsActivity.this.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                        values);
                            }

                            @Override
                            protected void onPostExecute(Uri returnUri) {
                                imageViewFavorite.setImageResource(R.drawable.favourite_star);
                                Toast.makeText(DetailsActivity.this, getString(R.string.added_from_favorites), Toast.LENGTH_SHORT).show();
                            }
                        }.execute();
                    }
                }
            }.execute();
        }
    }

    private class ReviewsAsyncTask extends AsyncTask<String, Void, ArrayList<Review>> {

        private final String LOG_TAG = ReviewsAsyncTask.class.getCanonicalName();
        private final String mApiKey;
        private String movieID;

        /**
         * {@link java.lang.reflect.Constructor}
         *
         * @param movieID movieID.
         * @param mApiKey TMDb API key.
         */
        private ReviewsAsyncTask(String mApiKey, String movieID) {
            this.mApiKey = mApiKey;
            this.movieID = movieID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewsJsonStr = null;
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
                reviewsJsonStr = builder.toString();
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
                return getReviewsDataFromJson(reviewsJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * Extracts data from the JSON object and returns an Array of movie objects.
         *
         * @param reviewsJsonStr JSON string to be traversed
         * @return ArrayList of Review objects
         * @throws JSONException throws JSONException
         */
        private ArrayList<Review> getReviewsDataFromJson(String reviewsJsonStr) throws JSONException {
            final String TAG_RESULTS = "results";

            JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
            JSONArray resultsArray = reviewsJson.getJSONArray(TAG_RESULTS);

            ArrayList<Review> reviews = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject reviewInfo = resultsArray.getJSONObject(i);
                //
                Review reviewItem = new Review(reviewInfo);
                reviews.add(reviewItem);
            }
            return reviews;
        }

        /**
         * Creates and returns an URL.
         *
         * @return URL formatted with parameters for the API
         * @throws MalformedURLException throws MalformedURLException
         */
        private URL getApiUrl() throws MalformedURLException {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + movieID + "/reviews";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri;
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, mApiKey)
                    .build();
            return new URL(builtUri.toString());
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            progressBar.setVisibility(View.GONE);
            if (reviews != null) {
                for (int i = 0; i < reviews.size(); i++) {
                    Review review = reviews.get(i);
                    View reviewItem = getLayoutInflater().inflate(R.layout.item_review, null);
                    linearLayout.addView(reviewItem);

                    TextView textViewAuthor = (TextView) reviewItem.findViewById(R.id.textViewAuthor);
                    TextView textViewReview = (TextView) reviewItem.findViewById(R.id.textViewReview);

                    textViewAuthor.setText(review.getAuthor());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        textViewReview.setText(Html.fromHtml(review.getContent(), Html.FROM_HTML_OPTION_USE_CSS_COLORS));
                    } else {
                        //noinspection deprecation
                        textViewReview.setText(Html.fromHtml(review.getContent()));
                    }
                }
            }
        }
    }

    private class TrailerAsyncTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

        private final String LOG_TAG = TrailerAsyncTask.class.getCanonicalName();
        private final String mApiKey;
        private String movieID;

        /**
         * {@link java.lang.reflect.Constructor}
         *
         * @param movieID movieID.
         * @param mApiKey TMDb API key.
         */
        private TrailerAsyncTask(String mApiKey, String movieID) {
            this.mApiKey = mApiKey;
            this.movieID = movieID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(VISIBLE);
        }

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailersJsonStr = null;
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
                trailersJsonStr = builder.toString();
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
                return getTrailersDataFromJson(trailersJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        /**
         * Extracts data from the JSON object and returns an Array of movie objects.
         *
         * @param trailersJsonStr JSON string to be traversed
         * @return ArrayList of Trailer objects
         * @throws JSONException throws JSONException
         */
        private ArrayList<Trailer> getTrailersDataFromJson(String trailersJsonStr) throws JSONException {
            final String TAG_RESULTS = "results";

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray resultsArray = trailersJson.getJSONArray(TAG_RESULTS);

            ArrayList<Trailer> trailers = new ArrayList<>();
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject trailersInfo = resultsArray.getJSONObject(i);

                trailer = new Trailer(trailersInfo);
                trailers.add(trailer);
            }
            return trailers;
        }

        /**
         * Creates and returns an URL.
         *
         * @return URL formatted with parameters for the API
         * @throws MalformedURLException throws MalformedURLException
         */
        private URL getApiUrl() throws MalformedURLException {
            final String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/" + movieID + "/videos";
            final String API_KEY_PARAM = "api_key";
            Uri builtUri;
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, mApiKey)
                    .build();
            return new URL(builtUri.toString());

        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            progressBar.setVisibility(View.GONE);
            if (trailers != null) {
                //setting recyclerView layout and adapter.
                StaggeredGridLayoutManager layoutManager1 = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
                layoutManager1.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                reviewsRecyclerView.setLayoutManager(layoutManager1);
                reviewsRecyclerView.setHasFixedSize(false);
                reviewsRecyclerView.setItemAnimator(new DefaultItemAnimator());

                reviewsRecyclerView.setAdapter(new TrailerAdapter(trailers, reviewsRecyclerView, DetailsActivity.this));

                trailer = trailers.get(0);
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
            }
        }
    }

}
