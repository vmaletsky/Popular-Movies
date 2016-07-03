package udacity.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.data.MoviesDBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private Movie mMovie;

    private MovieTrailers mTrailersList;

    private MovieReviews mReviewsList;

    private boolean mTwoPane;

    private String LOG_TAG = getClass().getSimpleName();

    private LayoutInflater mInflater;

    Retrofit mRetrofit;

    private String LOG_TAG_DB = "DB REQUEST";
    @Bind(R.id.movie_title)         protected TextView movieTitle;
    @Bind(R.id.movie_overview)      protected TextView overview;
    @Bind(R.id.poster)              protected ImageView posterView;
    @Bind(R.id.release_date)        protected TextView releaseDateView;
    @Bind(R.id.vote_average)        protected TextView voteAverageView;
    @Bind(R.id.button_fav)          protected Button mButtonFavorite;
    @Bind(R.id.reviews_caption)     protected TextView mReviewsCaption;

    public DetailsFragment() {
    }

    private Boolean isFavourite(Movie m) {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {MovieContract.MovieEntry.COLUMN_MOVIE_ID};
        String[] selectionArgs = {String.valueOf( m.id )};
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
        values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,    m.voteAverage);

        db.insert(MovieContract.MovieEntry.TABLE_NAME,
                null,
                values
        );

        db.close();
    }

    private void unmarkFavourite(Movie m) {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity().getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(MovieContract.MovieEntry.TABLE_NAME,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] {String.valueOf( m.id ) });

        db.close();
    }

    private void fetchTrailers() {
        FetchMovieTrailers fetchMovieTrailers = new FetchMovieTrailers();
        fetchMovieTrailers.execute(String.valueOf( mMovie.id) );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        Bundle args = getArguments();

        if (getActivity().findViewById(R.id.fragment_posters) == null) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
        }

        mInflater = getActivity().getLayoutInflater();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if (args == null) {
            return null;
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
            mButtonFavorite.setText(getString(R.string.mark_fav));
            mButtonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    markFavourite(mMovie);
                    mButtonFavorite.setText(getString(R.string.unmark_fav));
                }
            });
        } else {
            mButtonFavorite.setText(getString(R.string.unmark_fav));
            mButtonFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unmarkFavourite(mMovie);
                    mButtonFavorite.setText(getString(R.string.mark_fav));
                }
            });
        }

        mReviewsCaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle args = new Bundle();
                    args.putParcelable(Movie.MOVIE, mMovie);
                    ReviewFragment fragment = new ReviewFragment();
                    fragment.setArguments(args);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.movie_details_container, fragment, ReviewFragment.REVIEWFRAGMENT_TAG)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Intent intent = new Intent(getActivity(), ReviewActivity.class);
                    intent.putExtra(Movie.MOVIE, mMovie);
                    startActivity(intent);
                }
            }
        });
        fetchTrailers();

        String url = getString(R.string.posters_base_url) + mMovie.posterPath;
        Picasso.with(getActivity()).load(url).into(posterView);
        return rootView;
    }

    public class FetchMovieTrailers extends AsyncTask<String, Void, MovieTrailers> {

        public final String LOG_TAG = getClass().getSimpleName();

        @Override
        protected MovieTrailers doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }

            try {

                OpenMovieDBService movieService = mRetrofit.create(OpenMovieDBService.class);
                Call<MovieTrailers> call = movieService.getTrailers(params[0], BuildConfig.TMDB_API_KEY);
                MovieTrailers m = call.execute().body();
                return m;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Receiving movies trailers error : " + e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(MovieTrailers movieTrailers) {
            super.onPostExecute(movieTrailers);

            if (movieTrailers != null) {
                mTrailersList = movieTrailers;
                LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.trailers_container);
                for (final MovieTrailers.Result trailer: movieTrailers.results) {
                    View trailerItem = mInflater.inflate(R.layout.trailer_item, null);
                    TextView trailerName = (TextView) trailerItem.findViewById(R.id.trailer_name);
                    trailerName.setText(trailer.name);
                    trailerItem.setLayoutParams(
                            new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            )
                    );
                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Uri videoURI = Uri.parse("http://www.youtube.com/watch")
                                    .buildUpon()
                                    .appendQueryParameter("v", trailer.key)
                                    .build();
                            Intent intent = new Intent(Intent.ACTION_VIEW, videoURI);
                            startActivity(intent);
                        }
                    });
                    layout.addView(trailerItem);
                }

            }

        }
    }



}
