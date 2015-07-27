package com.example.android.moviemaniac;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviemaniac.data.MovieContract;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {

    //Declaring variables for array list of movies, the context and a logtag for debugging
   // List<Movie> movies = new ArrayList<>();
    Context c;
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();

   //adapter constructor
    public MovieAdapter(Activity context,Cursor cursor, int flags) {

        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_layout, parent, false);
        bindView(view,context,cursor);
         return view;
        }

    public void bindView(View view, Context context, Cursor cursor) {
        //Declaring views for Movie Posters(Image View) and MovieName(Text View)
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);

        TextView movieNameView = (TextView) view.findViewById(R.id.list_item_movieName);
        movieNameView.setText(cursor.getString(cursor.getColumnIndex
                (MovieContract.MovieEntry.COLUMN_MOVIE_TITLE)));

        //Loading and the Movie Posters into the image view using Picasso and logging the poster URLs
        Picasso.with(context).setLoggingEnabled(true);
        Picasso.with(context).load(cursor.getString(cursor.getColumnIndex
                (MovieContract.MovieEntry.COLUMN_POSTER_LINK)))
                .placeholder(R.drawable.image_url).into(iconView);
    }

    }
