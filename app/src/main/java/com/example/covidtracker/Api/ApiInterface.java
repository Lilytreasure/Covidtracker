package com.example.covidtracker.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {
    //The base url may change keep updating to avoid running into errors
    String BASE_URL="https://disease.sh/v3/covid-19/";

    @GET("countries")
    Call<List<ModelClass>> getcountrydata();



}
