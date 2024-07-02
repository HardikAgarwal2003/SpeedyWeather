package com.example.speedyweather

import com.example.speedyweather.openWeatherJSONAPIClasses.SpeedyWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ):Call<SpeedyWeather>

}