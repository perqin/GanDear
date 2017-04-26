package com.perqin.gandear.data;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public interface ResourceService {
    String BASE_URL = "https://gandear.perqin.com/";

    @GET("data.json")
    Call<String> getDataJsonFile();
}
