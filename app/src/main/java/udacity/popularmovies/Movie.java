package udacity.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Volodymyr on 3/5/2016.
 */


import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Movie implements Parcelable {
    public static final String MOVIE = "MOVIE";
    @SerializedName("poster_path")
    @Expose
    public String posterPath;
    @SerializedName("adult")
    @Expose
    public boolean adult;
    @SerializedName("overview")
    @Expose
    public String overview;
    @SerializedName("release_date")
    @Expose
    public String releaseDate;
    @SerializedName("genre_ids")
    @Expose
    public List<Integer> genreIds = new ArrayList<Integer>();
    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("original_title")
    @Expose
    public String originalTitle;
    @SerializedName("original_language")
    @Expose
    public String originalLanguage;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("backdrop_path")
    @Expose
    public String backdropPath;
    @SerializedName("popularity")
    @Expose
    public double popularity;
    @SerializedName("vote_count")
    @Expose
    public int voteCount;
    @SerializedName("video")
    @Expose
    public boolean video;
    @SerializedName("vote_average")
    @Expose
    public double voteAverage;


    public final static Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {

    }

    public Movie(Parcel in) {
        String[] data = new String[4];
        this.id = in.readInt();
        in.readStringArray(data);

        this.title = data[0];
        this.overview = data[1];
        this.posterPath = data[2];
        this.releaseDate = data[3];
        this.voteAverage = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeStringArray(new String[]{
                this.title,
                this.overview,
                this.posterPath,
                this.releaseDate
        });
        dest.writeDouble(this.voteAverage);
    }
}
