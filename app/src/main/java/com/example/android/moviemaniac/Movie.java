package com.example.android.moviemaniac;


public class Movie {
        String movieName;
        public String movieLink;
        int image; // drawable reference id

        public Movie(String name, int image)
        {
                movieName = name;
                this.image = image;

        }

        public Movie(String name, String link)
        {
                movieName = name;
                movieLink = link;

        }

        public String getMovieLink(){return movieLink;}

}


