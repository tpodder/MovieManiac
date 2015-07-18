package com.example.android.moviemaniac;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends ArrayAdapter<Movie> {

    List<Movie> movies = new ArrayList<>();
    //String[] urls = new String[]{};
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    Context c;

    public MovieAdapter(Activity context, List<Movie> movies) {

        super(context, 0, movies);
        this.movies= movies;
        c= context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

            Movie movie = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_layout, parent, false);
            }

            ImageView iconView = (ImageView) convertView.findViewById(R.id.list_item_icon);
            iconView.setImageResource(movie.image);

            TextView movieNameView = (TextView) convertView.findViewById(R.id.list_item_movieName);
            movieNameView.setText(movie.movieName);

            /*final String MOVIE_BASE_URL = " http://image.tmdb.org/t/p/";
            final String SIZE = "w92";
            final String POSTER_PATH = "poster_path";*/
            // ?????????????????????
            /*Uri uri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(SIZE)
                    .appendPath(value)
                    .build();*/
             //Uri uri = Uri.parse(VALUE);

            final String VALUE = movie.movieLink;

            Picasso.with(c).setLoggingEnabled(true);
            Picasso.with(c).load(movie.movieLink).error(R.drawable.image_url).into(iconView);


            return convertView;
    }

    }
