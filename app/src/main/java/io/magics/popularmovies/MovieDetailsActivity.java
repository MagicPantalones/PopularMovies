package io.magics.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;

import io.magics.popularmovies.models.Movie;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.GlideApp;
import io.magics.popularmovies.utils.MovieJsonParser;

public class MovieDetailsActivity extends AppCompatActivity {

    String mMovieId;
    int mImageWidth;
    int mImageHeight;
    ApiQueryHelper.ImageSize mImageSize;

    ImageView mPosterIv;
    TextView mTitleTv;
    TextView mReleaseDateTv;
    TextView mVoteTv;
    TextView mPlotTv;
    ProgressBar mLoaderPb;
    View mDetailsFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        mDetailsFrag = findViewById(R.id.details_activity_frag);
        mTitleTv = findViewById(R.id.tv_movie_title);
        mPosterIv = findViewById(R.id.iv_poster_details);
        mReleaseDateTv = findViewById(R.id.tv_release_date_text);
        mVoteTv = findViewById(R.id.tv_vote_average_text);
        mPlotTv = findViewById(R.id.tv_plot_text);
        mLoaderPb = findViewById(R.id.pb_details_loading);
        mImageSize = ApiQueryHelper.getOptimalImgSize(this);

        Intent intent = getIntent();
        Bundle bundle;
        if (intent != null){
            bundle = intent.getExtras();
            mMovieId = bundle.getString("movieId");
            mImageWidth = bundle.getInt("width");
            mImageHeight = bundle.getInt("height");
        }

        connectAndFetchData();
    }

    public void connectAndFetchData(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        showLoader();

        if (ni != null && ni.isConnectedOrConnecting()){
            new MovieDetailsFetcher().execute();
        }

    }

    public void showLoader(){
        mDetailsFrag.setVisibility(View.INVISIBLE);
        mLoaderPb.setVisibility(View.VISIBLE);
    }

    public void hideLoader(){
        mDetailsFrag.setVisibility(View.VISIBLE);
        mLoaderPb.setVisibility(View.INVISIBLE);
    }

    public class MovieDetailsFetcher extends AsyncTask<String, Void, Movie>{

        @Override
        protected Movie doInBackground(String... strings) {
            URL movieUrl = ApiQueryHelper.buildMovieUrl(MovieDetailsActivity.this, mMovieId);
            try {
                return MovieJsonParser.parseMovieDetails(ApiQueryHelper.doQuery(movieUrl), MovieDetailsActivity.this);
            } catch (Exception e) {
                Log.e("Details Activity", "JSON parsing failed", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Movie movie) {
            String posterUrl = ApiQueryHelper.buildImageUrl(movie.getPosterPath(), mImageSize);

            mReleaseDateTv.setText(movie.getReleaseDate());
            mVoteTv.setText(
                    getString(R.string.details_vote_average_text, movie.getVoteAverage(), movie.getVoteCount()));
            mPlotTv.setText(movie.getOverview());
            mTitleTv.setText(movie.getTitle());

            /* Image wont resize if this activity is rotated after it's created.
             * Will implement remeasure in part 2.
             */
            GlideApp.with(MovieDetailsActivity.this)
                    .load(posterUrl)
                    .placeholder(R.drawable.bg_loading_realydarkgrey)
                    .centerCrop()
                    .override(mImageWidth, mImageHeight)
                    .dontAnimate()
                    .into(mPosterIv);

            hideLoader();
        }
    }

}
