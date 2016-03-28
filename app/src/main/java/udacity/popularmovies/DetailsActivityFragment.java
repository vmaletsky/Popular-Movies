package udacity.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    private Movie mMovie;

    private String LOG_TAG = getClass().getSimpleName();

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        mMovie = getActivity().getIntent().getParcelableExtra("MOVIE");

        TextView movieTitle = (TextView) rootView.findViewById(R.id.movie_title);
        TextView overview = (TextView) rootView.findViewById(R.id.movie_overview);
        ImageView posterView = (ImageView) rootView.findViewById(R.id.poster);
        TextView releaseDateView = (TextView) rootView.findViewById(R.id.release_date);
        TextView voteAverageView = (TextView) rootView.findViewById(R.id.vote_average);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
        String url = getString(R.string.posters_base_url) + mMovie.posterPath;
        Picasso.with(getContext()).load(url).into(posterView);

        return rootView;
    }
}
