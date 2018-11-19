package com.example.rajiv.dictionary;

import com.example.rajiv.dictionary.definition.ResponseData;
import com.example.rajiv.dictionary.pronunciation.Data;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface OxfordApiService{
    @Headers({
            "Accept: application/json",
            "app_id: 409c212b",
            "app_key: 0f2af2aaa46429be00fae0156457a7a6"
    })

    @GET("inflections/en/{word}")
    Call<com.example.rajiv.dictionary.lemma.Lemmatron> getLemma(@Path("word") String word);

    @Headers({
            "Accept: application/json",
            "app_id: 409c212b",
            "app_key: 0f2af2aaa46429be00fae0156457a7a6"
    })


    @GET("entries/en/{word}")
    Call<ResponseData> getFullWordData(@Path("word") String word);

    @Headers({
            "Accept: application/json",
            "app_id: 409c212b",
            "app_key: 0f2af2aaa46429be00fae0156457a7a6"
    })


    @GET("entries/en/{word}/pronunciations")
    Call<Data> getPronunciation(@Path("word") String word);
}
