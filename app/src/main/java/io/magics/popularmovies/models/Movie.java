package io.magics.popularmovies.models;

/**
 * A model class for the JSON parsing
 * Created by Erik on 17.02.2018.
 */

public class Movie {

    private String posterPath;
    private String title;
    private String overview;
    private String voteAverage;
    private String voteCount;
    private String releaseDate;

    public Movie(){}

    public Movie(String posterPath, String title, String overview, String voteAverage, String voteCount, String releaseDate){
        this.posterPath = posterPath;
        this.title = title;
        this.overview = overview;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.releaseDate = releaseDate;
    }

    public String getPosterPath(){ return posterPath; }

    public void setPosterPath(String posterPath){ this.posterPath = posterPath; }

    public String getTitle(){ return title; }

    public void setTitle(String title){ this.title = title; }

    public String getReleaseDate() { return releaseDate; }

    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public String getOverview(){ return overview; }

    public void setOverview(String overview){ this.overview = overview; }

    public String getVoteAverage(){ return voteAverage; }

    public void setVoteAverage(String voteAverage){ this.voteAverage = voteAverage; }

    public String getVoteCount(){ return voteCount; }

    public void setVoteCount(String voteCount){ this.voteCount = voteCount; }

}
