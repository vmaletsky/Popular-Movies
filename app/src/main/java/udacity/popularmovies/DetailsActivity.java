package udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DetailsActivity extends AppCompatActivity {
    private String LOG_TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);
        DetailsFragment fragment = new DetailsFragment();

        Bundle arguments = new Bundle();
        Movie m = getIntent().getParcelableExtra(Movie.MOVIE);
        arguments.putParcelable(Movie.MOVIE, m);

        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .add(R.id.movie_details_container, fragment)
                .commit();
    }

}
