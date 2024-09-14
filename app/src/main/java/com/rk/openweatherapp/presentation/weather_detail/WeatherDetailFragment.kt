package com.rk.openweatherapp.presentation.weather_detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.rk.openweatherapp.databinding.FragmentWeatherDetailBinding
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.presentation.BindingFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * create an instance of this fragment.
 */

@AndroidEntryPoint
class WeatherDetailFragment : BindingFragment<FragmentWeatherDetailBinding>() {

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentWeatherDetailBinding::inflate

    private val args: WeatherDetailFragmentArgs by navArgs()
    private val viewModel: WeatherDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getWeatherBy(args.latArg, args.lonArg, args.dateArg)
        subscribeToStates()
    }

    private fun subscribeToStates() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state.isLoading) {
                    true -> binding.progressBar.visibility = View.VISIBLE
                    false -> binding.progressBar.visibility = View.GONE
                }

                when (state.weather != null) {
                    true -> populateViews(state.weather)
                    false -> TODO()
                }

                when (state.error.isNotEmpty()) {
                    true -> Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
                    false -> TODO()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun populateViews(weather: Weather) {
        with(binding) {
            title.text = weather.title
            description.text = weather.description
            temperatureValue.text = when {
                weather.day > 25 -> "Hot"
                weather.day < 10 -> "Cold"
                else -> "Normal"
            }
            dayValue.text = weather.day.toString() + "°C"
            nightValue.text = weather.night.toString() + "°C"
        }
    }
}