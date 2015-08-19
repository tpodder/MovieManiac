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

/**
 * Created by Tanushree on 2015-07-27.
 * Adapter for the list view to display trailers
 */
public class TrailerAdapter extends CursorAdapter {

    Context c;
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    public TrailerAdapter(Activity context,Cursor cursor, int flags) {

        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item, parent, false);
        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        TextView textView2 = (TextView) view.findViewById(R.id.name);
        textView2.setText(cursor.getString(cursor.getColumnIndex
                (MovieContract.MovieTrailerEntry.COLUMN_NAME))+":");

        ImageView imageView = (ImageView) view.findViewById(R.id.videoImg);
        imageView.setImageResource(R.drawable.youtube);
    }
}
