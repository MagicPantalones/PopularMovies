package io.magics.popularmovies;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;

import java.util.List;

import io.magics.popularmovies.models.MovieForGrid;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.GlideApp;

/**
 * Adapter for my recycler
 * Created by Erik on 18.02.2018.
 */

public class PosterAdapter extends RecyclerView.Adapter<PosterAdapter.PosterViewHolder> {

    private static final String TAG = PosterAdapter.class.getSimpleName();
    private List<MovieForGrid> mMovieData;
    private int mViewWidth;
    private int mViewHeight;
    private final PosterClickHandler mClickHandler;
    private ReachedEndHandler mReachedEndHandler;
    private ApiQueryHelper.ImageSize mImageSize;

    public interface PosterClickHandler{
        void onClick(String movieId, View view);
    }

    //Help from https://medium.com/@ayhamorfali/android-detect-when-the-recyclerview-reaches-the-bottom-43f810430e1e
    public interface ReachedEndHandler{
        void endReached(int position);
    }

    public PosterAdapter(PosterClickHandler posterClickHandler) {
        this.mClickHandler = posterClickHandler;
    }

    public void setEndListener(ReachedEndHandler reachedEndHandler){
        this.mReachedEndHandler = reachedEndHandler;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        Boolean orientation = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        mImageSize = ApiQueryHelper.getOptimalImgSize(context);

        //Sets the ViewHolder sizes based on the devise's orientation.
        mViewHeight = orientation ? parent.getMeasuredHeight() / 2 : parent.getMeasuredHeight();
        mViewWidth = orientation ? parent.getMeasuredWidth() / 2 : parent.getMeasuredWidth() / 3;

        View v = LayoutInflater.from(context).inflate(R.layout.poster_view_holder, parent, false);

        return new PosterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        MovieForGrid mfg = mMovieData.get(position);
        ImageView iv = holder.mIv;
        String posterUrl = ApiQueryHelper.buildImageUrl(mfg.getPosterPath(), mImageSize);

        if (position == mMovieData.size() - 1){
            mReachedEndHandler.endReached(position);
            Log.d(TAG, "IMAGESIZE " + mImageSize);
        }

        iv.setContentDescription(mfg.getTitle());
        iv.setMinimumHeight(mViewHeight);
        iv.setMinimumWidth(mViewWidth);


        GlideApp.with(holder.itemView)
                .load(posterUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bg_loading_realydarkgrey)
                .downsample(DownsampleStrategy.NONE)
                .centerCrop()
                .into(iv);
    }

    @Override
    public int getItemCount() {
        if (mMovieData == null) return 0;
        return mMovieData.size();
    }

    public void setMovieData(List<MovieForGrid> moviesForGrid){
        if (moviesForGrid == null) {
            return;
        }else if (mMovieData == null){
            mMovieData = moviesForGrid;
        } else {
            mMovieData.addAll(moviesForGrid);
        }
        notifyDataSetChanged();
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView mIv;

        public PosterViewHolder(View itemView) {
            super(itemView);
            mIv = itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickHandler.onClick(mMovieData.get(getAdapterPosition()).getMovieId(), v);
        }

    }


}
