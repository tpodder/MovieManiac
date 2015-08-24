package com.example.android.moviemaniac.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Tanushree on 2015-07-25.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID=101;
    static final int REVIEW = 102;
    static final int REVIEW_WITH_ID=103;
    static final int TRAILER = 104;
    static final int TRAILER_WITH_ID=105;
    static final int FAVORITE = 106;
    static final int FAVORITE_WITH_ID=107;
    static final int FAVORITE_TRAILER_WITH_ID=108;
    static final int FAVORITE_REVIEW_WITH_ID=109;



    private static final SQLiteQueryBuilder sTrailerQueryBuilder;
    private static final SQLiteQueryBuilder sReviewQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteTrailerQueryBuilder;
    private static final SQLiteQueryBuilder sFavoriteReviewQueryBuilder;

    static{
        sTrailerQueryBuilder = new SQLiteQueryBuilder();


        sTrailerQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieTrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieTrailerEntry.TABLE_NAME +
                        "." + MovieContract.MovieTrailerEntry.COLUMN_MOVIE_ID);
    }

    static{
        sReviewQueryBuilder = new SQLiteQueryBuilder();


        sReviewQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.MovieReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieEntry.TABLE_NAME +
                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.MovieReviewsEntry.TABLE_NAME +
                        "."+MovieContract.MovieReviewsEntry.COLUMN_MOVIE_ID );
    }

    static{
        sFavoriteTrailerQueryBuilder = new SQLiteQueryBuilder();


        sFavoriteTrailerQueryBuilder.setTables(
                MovieContract.MovieFavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteTrailerEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieFavoriteEntry.TABLE_NAME +
                        "." + MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoriteTrailerEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteTrailerEntry.COLUMN_MOVIE_ID);
    }

    static{
        sFavoriteReviewQueryBuilder = new SQLiteQueryBuilder();


        sFavoriteReviewQueryBuilder.setTables(
                MovieContract.MovieFavoriteEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.FavoriteReviewsEntry.TABLE_NAME +
                        " ON " + MovieContract.MovieFavoriteEntry.TABLE_NAME +
                        "." + MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.FavoriteReviewsEntry.TABLE_NAME +
                        "." + MovieContract.FavoriteReviewsEntry.COLUMN_MOVIE_ID);
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(authority, MovieContract.PATH_TRAILER+ "/#", TRAILER_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE, FAVORITE);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE + "/#", FAVORITE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_REVIEW + "/#", FAVORITE_REVIEW_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_FAVORITE_TRAILER + "/#", FAVORITE_TRAILER_WITH_ID);
        return matcher;
    }




    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.MovieReviewsEntry.CONTENT_TYPE;
            case REVIEW_WITH_ID:
                return MovieContract.MovieReviewsEntry.CONTENT_TYPE;
            case TRAILER:
                return MovieContract.MovieTrailerEntry.CONTENT_TYPE;
            case TRAILER_WITH_ID:
                return MovieContract.MovieTrailerEntry.CONTENT_TYPE;
            case FAVORITE:
                return MovieContract.MovieFavoriteEntry.CONTENT_TYPE;
            case FAVORITE_WITH_ID:
                return MovieContract.MovieFavoriteEntry.CONTENT_TYPE;
            case FAVORITE_TRAILER_WITH_ID:
                return MovieContract.FavoriteTrailerEntry.CONTENT_TYPE;
            case FAVORITE_REVIEW_WITH_ID:
                return MovieContract.FavoriteReviewsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private Cursor getMovieById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_MOVIE_ID+ "=?";

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTrailerById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_MOVIE_ID+ "=?";

        return sTrailerQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieEntry.TABLE_NAME+"."+MovieContract.MovieEntry.COLUMN_MOVIE_ID+ "=?";

        return sReviewQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteMovieById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieFavoriteEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID+ "=?";

        return mOpenHelper.getReadableDatabase().query(MovieContract.MovieFavoriteEntry.TABLE_NAME,
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteTrailerById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieFavoriteEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID+ "=?";

        return sFavoriteTrailerQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getFavoriteReviewById(
            Uri uri, String[] projection, String sortOrder) {

        String id = MovieContract.MovieFavoriteEntry.getIDFromUri(uri);
        String selection =  MovieContract.MovieFavoriteEntry.TABLE_NAME+"."+MovieContract.MovieFavoriteEntry.COLUMN_MOVIE_ID+ "=?";

        return sFavoriteReviewQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movie/#"
            case MOVIE_WITH_ID: {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                    break;}
            // "review"
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "review/#
            case REVIEW_WITH_ID: {
                retCursor = getReviewById(uri, projection, sortOrder);
                break;
            }

            // "trailer"
            case TRAILER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieTrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            // "trailer/#"
            case TRAILER_WITH_ID: {
                retCursor = getTrailerById(uri, projection, sortOrder);
                break;
            }

            // "favorite/#"
            case FAVORITE_WITH_ID: {
                    retCursor = getFavoriteMovieById(uri, projection, sortOrder);
                    break;
            }

            // "favorite"
            case FAVORITE: {
                    retCursor = mOpenHelper.getReadableDatabase().query(
                            MovieContract.MovieFavoriteEntry.TABLE_NAME,
                            projection,
                            selection,
                            selectionArgs,
                            null,
                            null,
                            sortOrder);

                    break;
            }

            // "review_favorite/#"
            case FAVORITE_REVIEW_WITH_ID: {
                retCursor = getFavoriteReviewById(uri, projection, sortOrder);
                break;
            }

            // "trailer_favorite/#"
            case FAVORITE_TRAILER_WITH_ID: {
                retCursor = getFavoriteTrailerById(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.MovieReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieReviewsEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.MovieTrailerEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieTrailerEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case FAVORITE: {
                long _id = db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieFavoriteEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(
                        MovieContract.MovieReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(
                        MovieContract.MovieTrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FAVORITE:
                rowsDeleted = db.delete(
                        MovieContract.MovieFavoriteEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:

                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(
                        MovieContract.MovieReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(
                        MovieContract.MovieTrailerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case FAVORITE:
                rowsUpdated = db.update(
                        MovieContract.MovieFavoriteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            }
            case REVIEW: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            }
            case TRAILER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieTrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            }
            case FAVORITE: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieFavoriteEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);

                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }

        }

    }

    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}