package udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by volod on 6/16/2016.
 */
public class MovieContract {
    public MovieContract() {
    }

    public static final String CONTENT_AUTHORITY = "udacity.popularmovies.app";

    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static abstract class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_MOVIE_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_VOTE_AVERAGE = "vote_average";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_RUNTIME = "runtime";

        public static final String COLUMN_OVERVIEW = "overview";

    }
}
