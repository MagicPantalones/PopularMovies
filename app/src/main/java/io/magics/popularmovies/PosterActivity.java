package io.magics.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import io.magics.popularmovies.models.MovieForGrid;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.MovieJsonParser;

import static io.magics.popularmovies.utils.ApiQueryHelper.*;
import static io.magics.popularmovies.utils.MovieJsonParser.*;

public class PosterActivity extends AppCompatActivity
        implements PosterAdapter.PosterClickHandler{

    private int mPageNumber;
    private SortingMethod mSortMethod;
    private RecyclerView mGridRecyclerView;
    private PosterAdapter mPosterAdapter;
    private ProgressBar mMovieLoader;
    private ImageView mErrorImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        mSortMethod = SortingMethod.POPULAR;
        mMovieLoader = findViewById(R.id.pb_loading);
        mErrorImage = findViewById(R.id.iv_error);
        mGridRecyclerView = findViewById(R.id.rv_grid_recycler);

        initRecycler();
    }

    public void initRecycler(){

        hideGridStartLoad();

        mPageNumber = 1;
        final Boolean orientation = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, orientation ? 2 : 1);

        gridLayoutManager.setOrientation(orientation ? GridLayoutManager.VERTICAL : GridLayoutManager.HORIZONTAL);
        mGridRecyclerView.setLayoutManager(gridLayoutManager);
        mGridRecyclerView.setHasFixedSize(true);

        mPosterAdapter = new PosterAdapter(this);

        mGridRecyclerView.setAdapter(mPosterAdapter);

        mPosterAdapter.setEndListener(new PosterAdapter.ReachedEndHandler() {
            @Override
            public void endReached(int position) {
                mPageNumber += 1;
                Toast.makeText(PosterActivity.this, "Page number " + mPageNumber, Toast.LENGTH_SHORT).show();
                mMovieLoader.setVisibility(View.VISIBLE);
                connectAndFetchData();
            }
        });
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

    public void hideGridStartLoad(){
        mGridRecyclerView.setVisibility(View.INVISIBLE);
        mMovieLoader.setVisibility(View.VISIBLE);
    }

    public void showErrorImage(){
        mErrorImage.setVisibility(View.VISIBLE);
        mGridRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(String movieId, View v) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        Bundle extras = new Bundle();
        extras.putString("movieId", movieId);
        extras.putInt("width", v.getMeasuredWidth());
        extras.putInt("height", v.getMeasuredHeight());
        intent.putExtras(extras);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sorting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.mi_popular:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    mSortMethod = SortingMethod.POPULAR;
                    initRecycler();
                }
                else item.setChecked(true);
                return true;
            case R.id.mi_top_rated:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    mSortMethod = SortingMethod.TOP_RATED;
                    initRecycler();
                }
                else item.setChecked(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class ApiQueryAsyncTask extends AsyncTask<String, Void, List<MovieForGrid>> {
        private final String TAG = ApiQueryAsyncTask.class.getSimpleName();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMovieLoader.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieForGrid> doInBackground(String... strings) {

            try {
                String movieJsonResponse = ApiQueryHelper.doQuery(
                        ApiQueryHelper.buildApiQueryUrl(PosterActivity.this, mSortMethod, mPageNumber));
                return MovieJsonParser.parseForGridView(movieJsonResponse, PosterActivity.this);

            } catch (Exception e) {
                Log.e(TAG, "doInBackground: ", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MovieForGrid> moviesForGrid) {
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
