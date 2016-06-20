package udacity.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by volod on 6/16/2016.
 */
public class MovieProvider extends ContentProvider {

    private MoviesDBHelper mMoviesDBHelper;

    @Override
    public boolean onCreate() {
        mMoviesDBHelper = new MoviesDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return mMoviesDBHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mMoviesDBHelper.getWritableDatabase().insert(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                values);

        return MovieContract.CONTENT_URI;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return mMoviesDBHelper.getWritableDatabase().delete(
                MovieContract.MovieEntry.TABLE_NAME,
                selection,
                selectionArgs
        );
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return mMoviesDBHelper.getWritableDatabase().update(
                MovieContract.MovieEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
    }
}
