package udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);
        DetailsFragment fragment = new DetailsFragment();

        Bundle arguments = new Bundle();
        arguments.putParcelable(Movie.MOVIE, getIntent().getData());

        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .add(R.id.movie_details_container, fragment)
                .commit();
    }

}
