package udacity.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Volodymyr on 3/5/2016.
 */
public class Movie implements Parcelable {
    public static final String MOVIE = "MOVIE";
    public String id;

    public String posterPath;

    public String title;

    public String overview;

    public String releaseDate;

    public double voteAverage;

    public int runtime;

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
        String[] data = new String[5];
        in.readStringArray(data);

        this.id = data[0];
        this.title = data[1];
        this.overview = data[2];
        this.posterPath = data[3];
        this.releaseDate = data[4];
        this.voteAverage = in.readDouble();
        this.runtime = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.id,
                this.title,
                this.overview,
                this.posterPath,
                this.releaseDate
        });
        dest.writeDouble(this.voteAverage);
        dest.writeInt(this.runtime);
    }
}
