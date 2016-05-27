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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsFragment extends Fragment {

    private Movie mMovie;

    private String LOG_TAG = getClass().getSimpleName();
    @Bind(R.id.movie_title)         protected TextView movieTitle;
    @Bind(R.id.movie_overview)      protected TextView overview;
    @Bind(R.id.poster)              protected ImageView posterView;
    @Bind(R.id.release_date)        protected TextView releaseDateView;
    @Bind(R.id.vote_average)        protected TextView voteAverageView;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable("MOVIE");
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
        }
        return rootView;
    }
}
