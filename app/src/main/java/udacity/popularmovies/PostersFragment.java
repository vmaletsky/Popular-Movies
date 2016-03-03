package udacity.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, ex.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView mPostersView;

    public PostersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchMovies();
    }

    public static PostersFragment newInstance() {
        return new PostersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void fetchMovies() {
        FetchMovieData movieData = new FetchMovieData();
        movieData.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_posters, container, false);
        mPostersView = (RecyclerView) rootView.findViewById(R.id.postersView);
        mPostersView.setLayoutManager(new GridLayoutManager(rootView.getContext(), 3));
        mPostersView.setHasFixedSize(true);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i<20; i++) {
            list.add("Item " + i);
        }
        mPostersView.setAdapter(new PostersAdapter(rootView.getContext(), list));
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    class FetchMovieData extends AsyncTask {
        String TAG = this.getClass().getSimpleName();


        public String[] getMoviesDataFromJson(String json) throws JSONException {
            final String PAGE = "page";
            final String RESULTS = "results";
            final String POSTER_PATH = "poster_path";
            final String ID = "id";
            final String ORIGINAL_TITLE = "original_title";
            final String TITLE = "title";

            JSONObject moviesJson = new JSONObject(json);
            JSONArray resultsArray = moviesJson.getJSONArray(RESULTS);

            return null;
        }

        public String[] doInBackground(Object... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            final String baseUrl =
                    "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + BuildConfig.TMDB_API_KEY;
            Uri builtUri = Uri.parse(baseUrl);

            try {
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
                Log.v(TAG, "Received movies list : " + moviesJsonStr);
            } catch (IOException e) {
                Log.e(TAG, "Shit happened " + e);
            }
            return null;
        }
    }
}
