package com.example.speedyweather.openWeatherJSONAPIClasses

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)