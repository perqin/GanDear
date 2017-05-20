package com.perqin.gandear.data;

import com.perqin.gandear.data.models.Version;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Author   : perqin
 * Date     : 17-4-26
 */

public interface ResourceService {
    String BASE_URL = "https://gandear.perqin.com/";

    @GET("data.json")
    Observable<String> getDataJsonFile();

    @GET("version.json")
    Observable<Version> getVersionJsonFile();
}
