package com.example.android.moviemaniac.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviemaniac.data.MovieContract.MovieEntry;

/**
 * Created by Tanushree on 2015-07-25.
 */
public class MovieDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_LINK + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_RATING+ " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW+ " TEXT NOT NULL, " +
                MovieEntry.COLUMN_TRAILER_LINKS+ " TEXT NOT NULL, " +
                MovieEntry.COLUMN_REVIEWS+ " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE IF NOT EXISTS " +
                MovieContract.MovieReviewsEntry.TABLE_NAME + " (" +
                MovieContract.MovieReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.MovieReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE IF NOT EXISTS " +
                MovieContract.MovieTrailerEntry.TABLE_NAME + " (" +
                MovieContract.MovieTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MovieContract.MovieTrailerEntry.COLUMN_NAME + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE IF NOT EXISTS " + MovieContract.MovieFavoriteEntry.TABLE_NAME + " (" +
                MovieContract.MovieFavoriteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_POSTER_LINK + " TEXT NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                MovieContract.MovieFavoriteEntry.COLUMN_RATING+ " TEXT NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_OVERVIEW+ " TEXT NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_TRAILER_LINKS+ " TEXT NOT NULL, " +
                MovieContract.MovieFavoriteEntry.COLUMN_REVIEWS+ " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " +
                MovieContract.FavoriteReviewsEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavoriteReviewsEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoriteReviewsEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.FavoriteReviewsEntry.COLUMN_CONTENT + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE_FAVORITE);

        final String SQL_CREATE_TRAILER_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS " +
                MovieContract.FavoriteTrailerEntry.TABLE_NAME + " (" +
                MovieContract.FavoriteTrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.FavoriteTrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.FavoriteTrailerEntry.COLUMN_KEY + " TEXT NOT NULL, " +
                MovieContract.FavoriteTrailerEntry.COLUMN_NAME + " TEXT NOT NULL " + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE_FAVORITE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTrailerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieFavoriteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.FavoriteTrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
