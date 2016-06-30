package udacity.popularmovies;

/**
 * Created by volod on 6/20/2016.
 */


import java.util.List;
import java.util.ArrayList;


public class MovieTrailers {

    public Integer id;

    public List<Result> results = new ArrayList<Result>();

    public static class Result {


        public String id;

        public String iso6391;

        public String iso31661;

        public String key;

        public String name;

        public String site;

        public Integer size;

        public String type;

    }
}
