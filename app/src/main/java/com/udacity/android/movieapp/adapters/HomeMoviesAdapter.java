package com.udacity.android.movieapp.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.movieapp.DetailsActivity;
import com.udacity.android.movieapp.MainActivity;
import com.udacity.android.movieapp.R;
import com.udacity.android.movieapp.Utility;
import com.udacity.android.movieapp.models.Movie;

import java.util.ArrayList;

/**
 * Created by Muhammad on 4/7/2017
 */

public class HomeMoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private MainActivity activity;
    private ArrayList<Movie> movies;

    public HomeMoviesAdapter(ArrayList<Movie> movies, RecyclerView recyclerView, MainActivity activity) {
        inflater = activity.getLayoutInflater();
        this.recyclerView = recyclerView;
        this.activity = activity;
        this.movies = movies;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;

        //inflate your layout and pass it to view holder
        view = inflater.inflate(R.layout.item_movie, viewGroup, false);
        holder = new ItemHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.width = (int) (recyclerView.getMeasuredWidth() * 0.5);
        layoutParams.setFullSpan(false);

        ItemHolder itemHolder = (ItemHolder) viewHolder;
        itemHolder.setDetails(movies.get(position));
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }


    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle;
        ImageView imageViewPoster;
        Movie movie;

        private ItemHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewName);
            imageViewPoster = (ImageView) itemView.findViewById(R.id.imageViewPoster);

            itemView.setOnClickListener(this);
        }

        private void setDetails(Movie movie) {
            this.movie = movie;
            textViewTitle.setText(movie.getTitle());

            Picasso.with(activity).load(Utility.buildImageUrl(185, movie.getImage())).
                    placeholder(R.drawable.placeholder).
                    error(R.drawable.warning).
                    into(imageViewPoster);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                Intent intent = new Intent(activity, DetailsActivity.class);
                intent.putExtra(DetailsActivity.MOVIE_OBJECT, movie);
                activity.startActivity(intent);
            }
        }
    }
}