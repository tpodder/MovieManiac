package com.example.android.moviemaniac;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviemaniac.data.MovieContract;

/**
 * Created by Tanushree on 2015-07-27.
 * Adapter for list view of reviews
 */
public class ReviewAdapter extends CursorAdapter {


    Context c;
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Activity context,Cursor cursor, int flags) {

        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item, parent, false);
        return view;
    }

    public void bindView(View view, Context context, Cursor cursor) {

        TextView reviewView = (TextView) view.findViewById(R.id.reviews);
        reviewView.setText(cursor.getString(cursor.getColumnIndex
                (MovieContract.MovieReviewsEntry.COLUMN_CONTENT)));

        TextView authorView = (TextView) view.findViewById(R.id.author);
        authorView.setText(cursor.getString(cursor.getColumnIndex
                (MovieContract.MovieReviewsEntry.COLUMN_AUTHOR)));

        DatabaseUtils.dumpCursor(cursor);
    }
}
