package io.magics.popularmovies.models;

/**
 * Model Class for the grid view
 * Created by Erik on 17.02.2018.
 */

public class MovieForGrid {

    private String posterPath;
    private String movieId;
    private String title;

    public MovieForGrid(String posterPath, String title, String movieId){
        this.posterPath = posterPath;
        this.title = title;
        this.movieId = movieId;
    }

    public String getPosterPath() { return posterPath; }

    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getMovieId() { return movieId; }

    public void setMovieId(String movieId) { this.movieId = movieId; }

}
