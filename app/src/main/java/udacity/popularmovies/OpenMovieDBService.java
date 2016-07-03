package udacity.popularmovies;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by volod on 6/28/2016.
 */
public interface OpenMovieDBService {

    final String API_KEY_PARAM="api_key";

    @GET("/3/movie/{sort_by}")
    Call<MoviePage> getMovies(@Path("sort_by") String sortBy, @Query(API_KEY_PARAM) String apiKey, @Query("page") String page);

    @GET("/3/movie/{id}/videos")
    Call<MovieTrailers> getTrailers(@Path("id") String movieId, @Query(API_KEY_PARAM) String apiKey);

    @GET("/3/movie/{id}/reviews")
    Call<MovieReviews> getReviews(@Path("id") String movieId, @Query(API_KEY_PARAM) String apiKey);



}
