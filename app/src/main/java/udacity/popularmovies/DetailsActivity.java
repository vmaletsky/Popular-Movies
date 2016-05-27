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
        if (savedInstanceState == null) {
            DetailsFragment fragment = new DetailsFragment();

            Bundle arguments = new Bundle();
            Movie m = getIntent().getParcelableExtra("MOVIE");
            Log.v(LOG_TAG, "MOVIE PASSED TO DETAILS = " + m.title);
            arguments.putParcelable("MOVIE", getIntent().getParcelableExtra("MOVIE"));
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();
        }
    }

}
