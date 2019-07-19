package com.zeeshan.coinbudget.interfaces;

import com.zeeshan.coinbudget.model.CountryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface CurrencyService {

    @Headers({"Content-Type: application/json",
            "X-RapidAPI-Host: ajayakv-rest-countries-v1.p.rapidapi.com",
            "X-RapidAPI-Key:36fb75f6f5msh3c3f8e79565e6f1p141da7jsnbea61baeeabc"})
    @GET("rest/v1/all")
    Call<List<CountryModel>> all();
}
