package udacity.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import udacity.popularmovies.data.MovieContract;
import udacity.popularmovies.data.MoviesDBHelper;


public class PostersFragment extends Fragment {
    public interface Callback {
        void onItemSelected(Movie m);
    }

    private String LOG_TAG = getClass().getSimpleName();
    @Bind(R.id.posters_view)    protected RecyclerView mPostersView;
    @Bind(R.id.recycler_fast_scroller)  protected RecyclerFastScroller mRecyclerFastScroller;

    private MoviesAdapter mMoviesAdapter;



    private Context mContext;

    private Retrofit mRetrofit;

    public PostersFragment() {
        // Required empty public constructor
    }
    
    public static PostersFragment newInstance() {

        Bundle args = new Bundle();

        PostersFragment fragment = new PostersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
            if (args.getBoolean(getString(R.string.show_favs_param)) == true) {
                showFavoriteMovies();
                return;
            }
        }
        if (isNetworkAvailable()) {
            fetchMovies("1");
        } else {
            showFavoriteMovies();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showFavoriteMovies() {
        MoviesDBHelper dbHelper = new MoviesDBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] cols = {
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH
        };
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME,
                cols,
                null,
                null,
                null,
                null,
                null
        );


        for( c.moveToFirst(); !c.isAfterLast(); c.moveToNext() ) {
            int movieId      = c.getInt(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            String movieTitle   = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
            String posterPath   = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
            double voteAverage  = c.getDouble(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
            String overview     = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
            String releaseDate  = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));

            Movie m = new Movie();

            m.id            = movieId;
            m.title         = movieTitle;
            m.overview      = overview;
            m.posterPath    = posterPath;
            m.releaseDate   = releaseDate;
            m.voteAverage   = voteAverage;

            mMoviesAdapter.add(m);
        }

        db.close();

    }

    private String LOG_TAG_DB = "DB REQUEST";
    private void fetchMovies(String page) {
        FetchMovieData movieData = new FetchMovieData();
        Bundle bundle = getArguments();
        String sortBy = getString(R.string.by_popularity_param);
        if (bundle != null) {
            sortBy = bundle.getString(getString(R.string.pref_sort_by_key), getString(R.string.by_popularity_param));
        }
        movieData.execute(sortBy, page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);
        mContext = rootView.getContext();
        ButterKnife.bind(this, rootView);
        mPostersView.setHasFixedSize(true);
        mPostersView.setItemViewCacheSize(10);

        final GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        mPostersView.setLayoutManager(mLayoutManager);
        mMoviesAdapter = new MoviesAdapter(mContext);
        mPostersView.setAdapter(mMoviesAdapter);


        mRecyclerFastScroller.attachRecyclerView(mPostersView);

        mPostersView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                fetchMovies(String.valueOf(current_page+1));
                Log.v(LOG_TAG, "Loading page " + (current_page+1));
            }
        });
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return rootView;
    }

    class FetchMovieData extends AsyncTask<String, Void, MoviePage> {
        String TAG = this.getClass().getSimpleName();

        @Override
        public MoviePage doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }


            try {
                OpenMovieDBService movieService = mRetrofit.create(OpenMovieDBService.class);
                Call<MoviePage> call = movieService.getMovies(params[0], BuildConfig.TMDB_API_KEY, params[1]);

                MoviePage moviePage = call.execute().body();
                return moviePage;

            } catch (IOException e) {
                Log.e(TAG, "Receiving movies list error : " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(MoviePage movies) {
            if (movies != null) {
                mMoviesAdapter.addAll(movies.results);
                mMoviesAdapter.notifyDataSetChanged();
                super.onPostExecute(movies);
            }

        }
    }
}
