package com.example.speedyweather

import android.os.Bundle
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.speedyweather.databinding.ActivityMainBinding
import com.example.speedyweather.openWeatherJSONAPIClasses.SpeedyWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fetchWeatherData("Moradabad")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }


    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(ApiInterface::class.java)
        val response =
            retrofit.getWeatherData(cityName, "97e589bf0d7b2fca405a35d7d23b1857", "metric")
        response.enqueue(object : Callback<SpeedyWeather> {
            override fun onResponse(call: Call<SpeedyWeather>, response: Response<SpeedyWeather>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toInt()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val conditions = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemperature = responseBody.main.temp_max
                    val minTemperature = responseBody.main.temp_min


                    binding.txtTemperature.text = "$temperature"
                    binding.txtWeather.text = "$conditions"
                    binding.txtMinTemp.text = "Min : $minTemperature"
                    binding.txtMaxTemp.text = "Max : $maxTemperature"
                    binding.txtHumidity.text = "$humidity%"
                    binding.txtWindSpeed.text = "$windSpeed m/s"
                    binding.txtSunrise.text = "${time(sunRise)}"
                    binding.txtSunset.text = "${time(sunSet)}"
                    binding.txtSea.text = "$seaLevel hPa"
                    binding.txtConditions.text = "$conditions"

                    binding.txtDay.text = dayName(System.currentTimeMillis())
                    binding.txtDate.text = date()
                    binding.txtCityName.text = "$cityName"

                    changeWeatherBackground(conditions)

                }
            }

            override fun onFailure(call: Call<SpeedyWeather>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeWeatherBackground(conditions: String) {
        when(conditions){
            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.img_sunny_back)
                binding.imgWeather.setImageResource(R.drawable.ic_weather_sunny)
            }
            "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                binding.root.setBackgroundResource(R.drawable.img_cloudy_back)
                binding.imgWeather.setImageResource(R.drawable.ic_weather_cloudy)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.img_rainy_back)
                binding.imgWeather.setImageResource(R.drawable.ic_weather_rainy)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.img_snowy_back)
                binding.imgWeather.setImageResource(R.drawable.ic_weather_snowy)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.img_sunny_back)
                binding.imgWeather.setImageResource(R.drawable.ic_weather_sunny)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH : mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}