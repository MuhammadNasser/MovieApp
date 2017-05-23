// Generated code from Butter Knife. Do not modify!
package com.udacity.android.movieapp;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DetailsActivity_ViewBinding implements Unbinder {
  private DetailsActivity target;

  @UiThread
  public DetailsActivity_ViewBinding(DetailsActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DetailsActivity_ViewBinding(DetailsActivity target, View source) {
    this.target = target;

    target.textViewName = Utils.findRequiredViewAsType(source, R.id.textViewName, "field 'textViewName'", TextView.class);
    target.textViewRelease = Utils.findRequiredViewAsType(source, R.id.textViewRelease, "field 'textViewRelease'", TextView.class);
    target.textViewOverView = Utils.findRequiredViewAsType(source, R.id.textViewOverView, "field 'textViewOverView'", TextView.class);
    target.textViewRating = Utils.findRequiredViewAsType(source, R.id.textViewRating, "field 'textViewRating'", TextView.class);
    target.textViewVoteCount = Utils.findRequiredViewAsType(source, R.id.textViewVoteCount, "field 'textViewVoteCount'", TextView.class);
    target.imageViewPoster = Utils.findRequiredViewAsType(source, R.id.imageViewPoster, "field 'imageViewPoster'", ImageView.class);
    target.imageViewBack = Utils.findRequiredViewAsType(source, R.id.imageViewBack, "field 'imageViewBack'", ImageView.class);
    target.imageViewFavorite = Utils.findRequiredViewAsType(source, R.id.imageViewFavorite, "field 'imageViewFavorite'", ImageView.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.progressBar, "field 'progressBar'", ProgressBar.class);
    target.linearLayout = Utils.findRequiredViewAsType(source, R.id.reviews, "field 'linearLayout'", LinearLayout.class);
    target.reviewsRecyclerView = Utils.findRequiredViewAsType(source, R.id.reviewsRecyclerView, "field 'reviewsRecyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DetailsActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.textViewName = null;
    target.textViewRelease = null;
    target.textViewOverView = null;
    target.textViewRating = null;
    target.textViewVoteCount = null;
    target.imageViewPoster = null;
    target.imageViewBack = null;
    target.imageViewFavorite = null;
    target.progressBar = null;
    target.linearLayout = null;
    target.reviewsRecyclerView = null;
  }
}
