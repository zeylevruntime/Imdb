package com.tes.imdb;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


public interface ApiServ {
    @GET("?apikey=491484bf")
    Call<MovieList> getMovies(
            @Query("s") String s,
            @Query("page") int page,
            @Query("type") String type,
            @Query("y") String year
    );
    @GET("?apikey=491484bf")
    Call<MovieDetail> getDetail(
            @Query("i") String s
    );


}
