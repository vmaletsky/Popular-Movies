package udacity.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by volod on 7/2/2016.
 */
public class ReviewFragment extends Fragment {
    private MovieReviews mMovieReviews;
    private ReviewsListAdapter mListAdapter;

    @Bind(R.id.reviews_list)    protected ListView mReviewsListView;

    private Movie mMovie;

    private LayoutInflater mInflater;

    public static final String REVIEWFRAGMENT_TAG = "RFTAG";
    Retrofit mRetrofit;
    private final String LOG_TAG = getClass().getSimpleName();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.review_fragment, container, false);
        ButterKnife.bind(this, rootView);
        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://api.themoviedb.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mInflater = inflater;
        mMovieReviews = new MovieReviews();

        Bundle args = getArguments();

        mMovie = args.getParcelable(Movie.MOVIE);
        FetchMovieReview fetchMovieReview = new FetchMovieReview();
        fetchMovieReview.execute(String.valueOf( mMovie.id ));

        return rootView;
    }


    private class ReviewsListAdapter extends ArrayAdapter<MovieReviews.Result> {
        public ReviewsListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return mMovieReviews.results.size();
        }

        @Override
        public MovieReviews.Result getItem(int position) {
            return mMovieReviews.results.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.review_item, null);
                holder = new ViewHolder();
                holder.author = (TextView) convertView.findViewById(R.id.review_author);
                holder.content = (TextView) convertView.findViewById(R.id.review_content);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Log.v(LOG_TAG, "Getting view...");
            holder.author.setText(getItem(position).author);
            holder.content.setText(getItem(position).content);

            return convertView;
        }

        private class ViewHolder {
            TextView author;

            TextView content;
        }
    }

    public class FetchMovieReview extends AsyncTask<String, Void, MovieReviews> {


        @Override
        protected MovieReviews doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }

            try {

                OpenMovieDBService movieService = mRetrofit.create(OpenMovieDBService.class);
                Call<MovieReviews> call = movieService.getReviews(params[0], BuildConfig.TMDB_API_KEY);
                mMovieReviews = call.execute().body();
                Log.v(LOG_TAG, "Reviews list length = " + mMovieReviews.results.size());
                return mMovieReviews;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Receiving movies trailers error : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(final MovieReviews movieReviews) {
            super.onPostExecute(movieReviews);

            if (movieReviews != null) {
                mMovieReviews = movieReviews;
                mListAdapter = new ReviewsListAdapter(getActivity(), R.layout.review_item);
                mListAdapter.addAll(mMovieReviews.results);
                mListAdapter.notifyDataSetChanged();
                mReviewsListView.setAdapter(mListAdapter);
            }
        }
    }
}
