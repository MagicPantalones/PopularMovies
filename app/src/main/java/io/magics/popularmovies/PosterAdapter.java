package io.magics.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;


import io.magics.popularmovies.models.MovieForGrid;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.GlideApp;

/**
 * Adapter for my recycler
 * Created by Erik on 18.02.2018.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder>{

    private static final String TAG = PosterAdapter.class.getSimpleName();
    private MovieForGrid[] mMovieData;
    private Context mContext;
    private int mViewWidth;
    private int mViewHeight;
    private final PosterClickHandler mClickHandler;

    public interface PosterClickHandler{
        void onClick(String movieId);
    }

    public PosterAdapter(PosterClickHandler posterClickHandler) {
        this.mClickHandler = posterClickHandler;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mViewHeight = parent.getMeasuredHeight() / 2;
        mViewWidth = parent.getMeasuredWidth() / 2;
        View v = LayoutInflater.from(mContext).inflate(R.layout.poster_view_holder, parent, false);
        return new PosterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        MovieForGrid mfg = mMovieData[position];
        ImageView iv = holder.mIv;
        String posterUrl = ApiQueryHelper.buildImageUrl(mfg.getPosterPath(), ApiQueryHelper.ImageSize.SIZE_DEFAULT);


        iv.setContentDescription(mfg.getTitle());

        GlideApp.with(holder.itemView)
                .load(posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.logo_app)
                .override(mViewWidth, mViewHeight)
                .centerCrop()
                .into(iv);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.length;
    }

    public void setMovieData(MovieForGrid[] moviesForGrid){

        mMovieData = moviesForGrid;
        notifyDataSetChanged();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mIv;

        public PosterViewHolder(View itemView) {
            super(itemView);
            mIv = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mMovieData[getAdapterPosition()].getMovieId());
        }
    }


}
