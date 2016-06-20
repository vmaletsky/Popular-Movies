package udacity.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.BinderThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.data.MovieProvider;
import udacity.popularmovies.data.MoviesDBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private Movie mMovie;

    private String LOG_TAG = getClass().getSimpleName();

    private String LOG_TAG_DB = "DB REQUEST";
    @Bind(R.id.movie_title)         protected TextView movieTitle;
    @Bind(R.id.movie_overview)      protected TextView overview;
    @Bind(R.id.poster)              protected ImageView posterView;
    @Bind(R.id.release_date)        protected TextView releaseDateView;
    @Bind(R.id.vote_average)        protected TextView voteAverageView;
    @Bind(R.id.button_fav)          protected Button btnFavourite;

    public DetailsFragment() {
    }

    private Boolean isFavourite(Movie m) {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String[] selectionArgs = { m.id };
        Cursor c  = db.query(MovieContract.MovieEntry.TABLE_NAME,
                projection,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                selectionArgs,
                null,
                null,
                null
        );
        return (c.getCount() > 0);
    }

    private void markFavourite(Movie m) {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE,           m.title);
        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID,        m.id);
        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW,        m.overview);
        values.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH,     m.posterPath);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,    m.releaseDate);
        values.put(MovieContract.MovieEntry.COLUMN_RUNTIME,         m.runtime);
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,    m.voteAverage);

        Log.v(LOG_TAG_DB, "ADDING MOVIE id = " + m.id + " title = " + m.title);
        db.insert(MovieContract.MovieEntry.TABLE_NAME,
                null,
                values
        );

        db.close();
    }

    private void unmarkFavourite(Movie m) {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Log.v(LOG_TAG_DB, "DELETING MOVIE id = " + m.id + " title : " + m.title);
        db.delete(MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { m.id });

        db.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        Bundle args = getArguments();
        if (args == null) {
            return rootView;
        }
        mMovie = args.getParcelable(Movie.MOVIE);
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate;
        Calendar c = Calendar.getInstance();
        try {
            releaseDate = sdf.parse(mMovie.releaseDate);
            c.setTime(releaseDate);

        } catch (ParseException e) {
            Log.e(LOG_TAG, "ParseException: Wrong release date : " + e);
        }
        movieTitle.setText(mMovie.title);
        overview.setText(mMovie.overview);
        String year = String.valueOf(c.get(Calendar.YEAR));
        releaseDateView.setText(year);
        voteAverageView.setText(mMovie.voteAverage + "/10");
        if (!isFavourite(mMovie)) {
            btnFavourite.setText(getString(R.string.mark_fav));
            btnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markFavourite(mMovie);
                    btnFavourite.setText(getString(R.string.unmark_fav));
                }
            });
        } else {
            btnFavourite.setText(getString(R.string.unmark_fav));
            btnFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unmarkFavourite(mMovie);
                    btnFavourite.setText(getString(R.string.mark_fav));
                }
            });
        }
        String url = getString(R.string.posters_base_url) + mMovie.posterPath;
        Picasso.with(getActivity()).load(url).into(posterView);
        return rootView;
    }
}
