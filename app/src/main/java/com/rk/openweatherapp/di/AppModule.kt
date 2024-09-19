package com.rk.openweatherapp.di

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import com.rk.openweatherapp.common.Constants
import com.rk.openweatherapp.data.remote.OpenWeatherApi
import com.rk.openweatherapp.data.repository.WeatherRepositoryImpl
import com.rk.openweatherapp.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOpenWeatherApi(): OpenWeatherApi {
        // Create an instance of HttpLoggingInterceptor
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)  // Set logging level to BODY for detailed logs

        // Build the OkHttpClient and add the logging interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)  // Attach the logging interceptor
            .build()

        // Create and return the Retrofit instance using the client
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)  // Use the custom client with logging
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherApi::class.java)
    }

//    @Provides
//    @Singleton
//    fun provideOpenWeatherApi(): OpenWeatherApi {
//        return Retrofit.Builder()
//            .baseUrl(Constants.BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(OpenWeatherApi::class.java)
//    }

    @Provides
    @Singleton
    fun provideWeatherRepository(api: OpenWeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGeocoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("OpenWeatherApiPrefs", Context.MODE_PRIVATE)
    }

}