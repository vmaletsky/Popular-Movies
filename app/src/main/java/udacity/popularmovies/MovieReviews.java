

package udacity.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieReviews {

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("results")
    @Expose
    public List<Result> results = new ArrayList<Result>();
    @SerializedName("total_pages")
    @Expose
    public Integer total_pages;
    @SerializedName("total_results")
    @Expose
    public Integer total_results;

    public static class Result implements Parcelable {

        public static final String REVIEW = "MOVIEREVIEW";

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("author")
        @Expose
        public String author;
        @SerializedName("content")
        @Expose
        public String content;
        @SerializedName("url")
        @Expose
        public String url;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeStringArray(new String[] {
                    this.id,
                    this.author,
                    this.content,
                    this.url
            });
        }

        public final static Parcelable.Creator<MovieReviews.Result> CREATOR = new Parcelable.Creator<MovieReviews.Result>() {
            @Override
            public Result createFromParcel(Parcel source) {
                return new Result(source);
            }

            @Override
            public Result[] newArray(int size) {
                return new Result[size];
            }
        };

        public Result(Parcel source) {
            String[] data = new String[4];
            source.readStringArray(data);

            this.id         = data[0];
            this.author     = data[1];
            this.content    = data[2];
            this.url        = data[3];
            this.url        = data[3];
        }



    }

}

