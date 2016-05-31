package udacity.popularmovies;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        PostersFragment.Callback {
    @Override
    public void onItemSelected(Movie m) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(Movie.MOVIE, m);
            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(Movie.MOVIE, m);
            startActivity(intent);
        }
    }

    protected boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new DetailsFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
