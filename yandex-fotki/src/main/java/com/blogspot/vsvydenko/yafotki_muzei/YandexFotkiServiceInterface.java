package com.blogspot.vsvydenko.yafotki_muzei;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by vsvydenko on 26.02.14.
 */
public interface YandexFotkiServiceInterface {

    @Headers("Accept: application/json")
    @GET("/top/updated;{date}/")
    PhotosResponse getTopPhotos(@Path("date") String date);

    @Headers("Accept: application/json")
    @GET("/podhistory/poddate;{date}/?limit=1")
    PhotosResponse getPODPhoto(@Path("date") String date);

    static class PhotosResponse {
        List<Photo> entries;
    }

    static class Photo {
        String  id;
        String  author;
        String  title;
        Img     img;
        Links   links;
    }

    static class Img {
        ImageData XXXL;
    }

    static class ImageData {
        String href;
    }

    static class Links {
        String alternate;
    }
}
