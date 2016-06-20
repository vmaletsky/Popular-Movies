package udacity.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.data.MoviesDBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private Movie mMovie;

    private ArrayList<MovieTrailer> mTrailersList;

    private String LOG_TAG = getClass().getSimpleName();

    private String LOG_TAG_DB = "DB REQUEST";
    @Bind(R.id.movie_title)         protected TextView movieTitle;
    @Bind(R.id.movie_overview)      protected TextView overview;
    @Bind(R.id.poster)              protected ImageView posterView;
    @Bind(R.id.release_date)        protected TextView releaseDateView;
    @Bind(R.id.vote_average)        protected TextView voteAverageView;
    @Bind(R.id.button_fav)          protected Button mButtonFavorite;
    @Bind(R.id.trailers_list)       protected ListView mTrailersListView;

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
        String url = getString(R.string.posters_base_url) + mMovie.posterPath;
        Picasso.with(getActivity()).load(url).into(posterView);
        return rootView;
    }

    public class TrailersListAdapter implements ListAdapter {
        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {

        }

        @Override
        public int getCount() {
            return mTrailersList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTrailersList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MovieTrailer trailer = mTrailersList.get(position);
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.trailer_item, parent);
            TextView nameView = (TextView) rowView.findViewById(R.id.trailer_name);
            nameView.setText(trailer.name);
            return null;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }

    public class FetchMovieTrailers extends AsyncTask<String, Void, MovieTrailer[]> {

        public final String LOG_TAG = getClass().getSimpleName();
        @Override
        protected MovieTrailer[] doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String moviesJsonStr = null;


            try {
                final String API_KEY_PARAM="api_key";
                final String baseUrl =
                        getString(R.string.movie_fetch_base_url) + params[0] + "/videos";
                Uri builtUri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.TMDB_API_KEY)
                        .build();
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Receiving movies trailers error : " + e);
            }

            try {
                return getTrailersFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Something went wrong with JSON : " + e);
            }
            return null;
        }

        public MovieTrailer[] getTrailersFromJson(String json) throws JSONException {
            final String RESULTS = "results";
            final String ID = "id";
            final String NAME = "name";
            final String KEY = "key";

            JSONObject videosJson = new JSONObject(json);
            JSONArray videosArray = videosJson.getJSONArray(RESULTS);
            MovieTrailer[] videos = new MovieTrailer[videosArray.length()];
            for (int i=0; i<videosArray.length(); i++) {
                JSONObject videoObject = videosArray.getJSONObject(i);

                String key      = videoObject.getString(KEY);
                String name     = videoObject.getString(NAME);
                String id       = videoObject.getString(ID);

                MovieTrailer v = new MovieTrailer();
                v.id = id;
                v.key = key;
                v.name = name;

                videos[i] = v;
            }
            return videos;
        }

        @Override
        protected void onPostExecute(MovieTrailer[] movieTrailers) {
            super.onPostExecute(movieTrailers);

            if (movieTrailers != null) {
                mTrailersList = new ArrayList<>(Arrays.asList(movieTrailers));
                ListAdapter adapter = new TrailersListAdapter();
                mTrailersListView.setAdapter(adapter);
            }

        }
    }

    public class FetchMovieReview extends AsyncTask {
        @Override
        protected MovieReview[] doInBackground(Object[] params) {
            return null;
        }
    }
}
