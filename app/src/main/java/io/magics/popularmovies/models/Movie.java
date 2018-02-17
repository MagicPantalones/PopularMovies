package io.magics.popularmovies.models;

import java.util.List;

/**
 * A model class for the JSON parsing
 * Created by Erik on 17.02.2018.
 */

public class Movie {

    private String posterPath;
    private String originalTitle;
    private List<String> genre;
    private String overview;
    private String voteAverage;
    private String voteCount;

    public Movie(){}

    public Movie(String posterPath, String originalTitle, List<String> genre, String overview, String voteAverage, String voteCount){
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.genre = genre;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
    }

    public String getPosterPath(){ return posterPath; }

    public void setPosterPath(String posterPath){ this.posterPath = posterPath; }

    public String getOriginalTitle(){ return originalTitle; }

    public void setOriginalTitle(String originalTitle){ this.originalTitle = originalTitle; }

    public List<String> getGenre() { return genre; }

    public void setGenre(List<String> genre) { this.genre = genre; }

    public String getOverview(){ return overview; }

    public void setOverview(String overview){ this.overview = overview; }

    public String getVoteAverage(){ return voteAverage; }

    public void setVoteAverage(String voteAverage){ this.voteAverage = voteAverage; }

    public String getVoteCount(){ return voteCount; }

    public void setVoteCount(String voteCount){ this.voteCount = voteCount; }

}
