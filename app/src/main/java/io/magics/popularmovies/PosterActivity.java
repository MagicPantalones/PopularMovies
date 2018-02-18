package io.magics.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import io.magics.popularmovies.models.MovieForGrid;
import io.magics.popularmovies.utils.ApiQueryHelper;
import io.magics.popularmovies.utils.MovieDbJsonParser;

public class PosterActivity extends AppCompatActivity {

    private int mPageNumber;
    private String mSortMethod;
    private TextView mListTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poster);

        mPageNumber = 1;
        mSortMethod = ApiQueryHelper.TOP_RATED_QUERY;
        mListTv = findViewById(R.id.tv_result_display);

        if (isOnline()) {
            new ApiQueryAsyncTask().execute();
        }
    }

    public boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public class ApiQueryAsyncTask extends AsyncTask<String, Void, MovieForGrid[]> {



        @Override
        protected MovieForGrid[] doInBackground(String... strings) {

            try {
                String movieJsonResponse = ApiQueryHelper.doQuery(
                        ApiQueryHelper.buildApiQueryUrl(PosterActivity.this, mSortMethod, mPageNumber));
                Log.d("doInBackground", "doInBackground: " + movieJsonResponse);
                return MovieDbJsonParser.parseMovieListForGridView(movieJsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(MovieForGrid[] movieForGrids) {

            if (movieForGrids == null){
                Toast.makeText(PosterActivity.this, "movieForGrids: ", Toast.LENGTH_LONG).show();
                return;
            }

            for (MovieForGrid movie : movieForGrids){
                String posterUrl = ApiQueryHelper.buildImageUrl(movie.getPosterPath(), ApiQueryHelper.ImageSize.SIZE_DEFAULT);
                mListTv.append("Title: \n" + movie.getTitle() + "\nPoster URL: \n" + posterUrl + "\n\n");
            }
        }
    }
}
