package udacity.popularmovies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class MovieTrailers {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("results")
    @Expose
    public List<Result> results = new ArrayList<Result>();

    public static class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("iso_639_1")
        @Expose
        public String iso_639_1;
        @SerializedName("iso_3166_1")
        @Expose
        public String iso_3166_1;
        @SerializedName("key")
        @Expose
        public String key;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("site")
        @Expose
        public String site;
        @SerializedName("size")
        @Expose
        public Integer size;
        @SerializedName("type")
        @Expose
        public String type;

    }
}



