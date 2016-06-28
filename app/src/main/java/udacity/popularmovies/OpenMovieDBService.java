package udacity.popularmovies;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by volod on 6/28/2016.
 */
public interface OpenMovieDBService {
    @GET("movie/{sort_by}")
    Call<List<Movie>> getMovies(@Path("sort_by") String sortBy);

    @GET("movie/{id}/videos")
    Call<List<MovieTrailer>> getTrailers(@Path("id") String movieId);

}
