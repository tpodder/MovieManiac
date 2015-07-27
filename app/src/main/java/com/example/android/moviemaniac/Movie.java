package com.example.android.moviemaniac;
/*
    Movie Class whose data members describe a movie
 */

public class Movie {
        String movieName;
        String movieLink;
        String movieReleaseDate;
        String movieRating;
        String movieOverview;
        String movieTrailerLinks;
        String movieReviews;

    public Movie(String name, String link, String rDate, String rating, String overview,
                 String trailerLinks, String reviews)
        {
                movieName = name;
                movieLink = link;
                movieReleaseDate =rDate;
                movieRating = rating+"/10";
                movieOverview = overview;
                movieTrailerLinks = trailerLinks;
                movieReviews = reviews;

        }
}


