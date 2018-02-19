package io.magics.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;

import io.magics.popularmovies.models.MovieForGrid;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.MovieDbJsonParser;

public class PosterActivity extends AppCompatActivity
        implements PosterAdapter.PosterClickHandler{

    private int mPageNumber;
    private String mSortMethod;
    private RecyclerView mGridRecyclerView;
    private PosterAdapter mPosterAdapter;
    private ProgressBar mMovieLoader;
    private ImageView mErrorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        mPageNumber = 1;
        mSortMethod = ApiQueryHelper.TOP_RATED_QUERY;
        mMovieLoader = findViewById(R.id.pb_loading);
        mErrorImage = findViewById(R.id.iv_error);
        mGridRecyclerView = findViewById(R.id.rv_grid_recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mGridRecyclerView.setLayoutManager(gridLayoutManager);
        mGridRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);
        mGridRecyclerView.setAdapter(mPosterAdapter);

        connectAndFetchData();
    }

    public void connectAndFetchData(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        showGrid();

        if (ni != null && ni.isConnectedOrConnecting()){
            new ApiQueryAsyncTask().execute();
        }

    }

    public void showGrid(){
        mErrorImage.setVisibility(View.GONE);
        mGridRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showErrorImage(){
        mErrorImage.setVisibility(View.VISIBLE);
        mGridRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(String movieId) {
        Toast.makeText(this, movieId, Toast.LENGTH_LONG).show();
    }

    public class ApiQueryAsyncTask extends AsyncTask<String, Void, MovieForGrid[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMovieLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected MovieForGrid[] doInBackground(String... strings) {

            try {
                String movieJsonResponse = ApiQueryHelper.doQuery(
                        ApiQueryHelper.buildApiQueryUrl(PosterActivity.this, mSortMethod, mPageNumber));
                return MovieDbJsonParser.parseMovieListForGridView(movieJsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieForGrid[] moviesForGrid) {
            mMovieLoader.setVisibility(View.INVISIBLE);
            if (moviesForGrid == null){
                showErrorImage();
                return;
            }
            showGrid();
            mPosterAdapter.setMovieData(moviesForGrid);
        }
    }
}
