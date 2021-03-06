package udacity.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class DetailsActivity extends AppCompatActivity {
    private String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        DetailsFragment fragment = new DetailsFragment();

        Bundle arguments = new Bundle();
        Movie m = getIntent().getParcelableExtra(Movie.MOVIE);

        if (savedInstanceState != null) {
            m = savedInstanceState.getParcelable(Movie.MOVIE);
        }

        setContentView(R.layout.activity_details);
        arguments.putParcelable(Movie.MOVIE, m);

        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction()
                .add(R.id.movie_details_container, fragment)
                .commit();
    }

}
