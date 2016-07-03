package udacity.popularmovies;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by volod on 7/2/2016.
 */
public class ReviewActivity extends AppCompatActivity {
    private String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ReviewFragment fragment = new ReviewFragment();

        Bundle arguments = new Bundle();
        Movie m = getIntent().getParcelableExtra(Movie.MOVIE);
        setContentView(R.layout.activity_reviews);
        arguments.putParcelable(Movie.MOVIE, m);

        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .add(R.id.reviews_container, fragment)
                .commit();
    }
}
