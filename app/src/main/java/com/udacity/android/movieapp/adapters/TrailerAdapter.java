package com.udacity.android.movieapp.adapters;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.android.movieapp.DetailsActivity;
import com.udacity.android.movieapp.R;
import com.udacity.android.movieapp.models.Trailer;

import java.util.ArrayList;

/**
 * Created by Muhammad on 4/27/2017
 */

public class TrailerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private RecyclerView recyclerView;
    private DetailsActivity activity;
    private ArrayList<Trailer> trailers;

    public TrailerAdapter(ArrayList<Trailer> trailers, RecyclerView recyclerView, DetailsActivity activity) {
        inflater = activity.getLayoutInflater();
        this.recyclerView = recyclerView;
        this.activity = activity;
        this.trailers = trailers;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        RecyclerView.ViewHolder holder;

        //inflate your layout and pass it to view holder
        view = inflater.inflate(R.layout.item_trailer, viewGroup, false);
        holder = new ItemHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
        layoutParams.width = (int) (recyclerView.getMeasuredWidth() * 0.75);
        layoutParams.setFullSpan(false);

        ItemHolder itemHolder = (ItemHolder) viewHolder;
        itemHolder.setDetails(trailers.get(position));
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }


    private class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewTitle;
        ImageView imageViewThumbnail;
        Trailer trailer;

        private ItemHolder(View itemView) {
            super(itemView);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewName);
            imageViewThumbnail = (ImageView) itemView.findViewById(R.id.imageViewThumbnail);

            itemView.setOnClickListener(this);
        }

        private void setDetails(Trailer trailer) {
            this.trailer = trailer;
            textViewTitle.setText(trailer.getName());

            String yt_thumbnail_url = "http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg";
            Picasso.with(activity).load(yt_thumbnail_url).
                    placeholder(R.drawable.placeholder).
                    error(R.drawable.warning).
                    into(imageViewThumbnail);
        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                activity.startActivity(intent);
            }
        }
    }
}