package udacity.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_posters, new PostersFragment())
                    .commit();
        }
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
        PostersFragment fragment = new PostersFragment();
        if (id == R.id.by_popularity) {
            Bundle args = new Bundle();
            args.putString(getString(R.string.pref_sort_by_key), getString(R.string.by_popularity_param));
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_posters, fragment)
                    .commit();
            return true;
        } else if (id == R.id.by_rating) {
            Bundle args = new Bundle();
            args.putString(getString(R.string.pref_sort_by_key), getString(R.string.by_rating_param));
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_posters, fragment)
                    .commit();
            return true;
        } else if (id == R.id.show_favs) {
            Bundle args = new Bundle();
            args.putBoolean(getString(R.string.show_favs_param), true);
            fragment.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_posters, fragment)
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
