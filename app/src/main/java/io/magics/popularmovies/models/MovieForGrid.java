package io.magics.popularmovies.models;

/**
 * Model Class for the grid view
 * Created by Erik on 17.02.2018.
 */

public class MovieForGrid {

    private String posterPath;
    private String originalTitle;

    public MovieForGrid(String posterPath, String originalTitle){
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
    }

    public String getPosterPath() { return posterPath; }

    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }

    public String getOriginalTitle() { return originalTitle; }

    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
}
