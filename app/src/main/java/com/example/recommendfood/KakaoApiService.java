package com.example.recommendfood;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApiService {
    @GET("v2/local/search/keyword.json")
    //@GET("v2/local/search/coord2regioncode.json")
    Call<KakaoSearchResponse> searchLocation(
            @Header("Authorization") String key,
            @Query("query") String query,
            @Query("x") String longitude,
            @Query("y") String latitude,
            @Query("radius") int radius  // 검색 반경 (단위: 미터)
    );
}