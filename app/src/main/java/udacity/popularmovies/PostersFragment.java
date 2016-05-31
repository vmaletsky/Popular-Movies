package udacity.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PostersFragment extends Fragment {
    public interface Callback {
        public void onItemSelected(Movie m);
    }

    private String LOG_TAG = getClass().getSimpleName();

    @Bind(R.id.posters_view)    protected RecyclerView mPostersView;

    private MoviesAdapter mMoviesAdapter;

    private Context mContext;

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

        fetchMovies();
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

    private void fetchMovies() {
        FetchMovieData movieData = new FetchMovieData();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_rating_value));
        movieData.execute(sortBy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);
        ButterKnife.bind(this, rootView);
        mPostersView.setHasFixedSize(true);
        mPostersView.setItemViewCacheSize(10);
        mPostersView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
        mContext = rootView.getContext();
        mMoviesAdapter = new MoviesAdapter(mContext);
        mPostersView.setAdapter(mMoviesAdapter);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            mPostersView.setLayoutManager(new GridLayoutManager(mContext, 2));
        else
            mPostersView.setLayoutManager(new GridLayoutManager(mContext, 4));
        return rootView;
    }

    class FetchMovieData extends AsyncTask<String, Void, Movie[]> {
        String TAG = this.getClass().getSimpleName();


        public Movie[] getMoviesDataFromJson(String json) throws JSONException {
            final String PAGE = "page";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String ID = "id";
            final String OVERVIEW = "overview";
            final String TITLE = "title";
            final String RELEASE_DATE = "release_date";
            final String VOTE_AVERAGE = "vote_average";

            JSONObject moviesJson = new JSONObject(json);
            JSONArray moviesArray = moviesJson.getJSONArray(RESULTS);
            Movie[] movies = new Movie[moviesArray.length()];
            for (int i=0; i<moviesArray.length(); i++) {
                JSONObject movieObject = moviesArray.getJSONObject(i);
                String id = movieObject.getString(ID);
                String posterPath = movieObject.getString(POSTER_PATH);
                String title = movieObject.getString(TITLE);
                String overview = movieObject.getString(OVERVIEW);
                String releaseDate = movieObject.getString(RELEASE_DATE);
                double voteAverage = movieObject.getDouble(VOTE_AVERAGE);

                Movie m = new Movie();
                m.id = id;
                m.posterPath = posterPath;
                m.title = title;
                m.overview = overview;
                m.releaseDate = releaseDate;
                m.voteAverage = voteAverage;
                m.runtime = 0;
                movies[i] = m;
            }
            return movies;
        }

        @Override
        public Movie[] doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }
            HttpURLConnection urlConnection;
            BufferedReader reader;
            String moviesJsonStr = null;


            try {
                final String API_KEY_PARAM="api_key";
                final String baseUrl =
                        getString(R.string.movie_fetch_base_url) + params[0];
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
                Log.e(TAG, "Receiving movies list error : " + e);
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(TAG, "Something went wrong with JSON : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                ArrayList<Movie> moviesList = new ArrayList<>(Arrays.asList(movies));
                mMoviesAdapter.addAll(moviesList);
                mMoviesAdapter.notifyDataSetChanged();
                super.onPostExecute(movies);
            }

        }
    }
}
