package com.rk.openweatherapp.presentation.weather_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rk.openweatherapp.databinding.FragmentWeatherListBinding
import com.rk.openweatherapp.domain.model.Weather
import com.rk.openweatherapp.presentation.BindingFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WeatherListFragment : BindingFragment<FragmentWeatherListBinding>() {

    override val bindingInflater: (LayoutInflater) -> FragmentWeatherListBinding
        get() = FragmentWeatherListBinding::inflate

    private val viewModel: WeatherListViewModel by viewModels()
    private val adapter: WeatherAdapter by lazy { WeatherAdapter(::onWeatherClick) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isFineLocationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val isCoarseLocationGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
            if (isFineLocationGranted || isCoarseLocationGranted) {
                fetchWeatherForCurrentLocation()
            } else {
                // Handle the case where permission is denied (optional: show a message to user)
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_LONG)
                    .show()

            }
        }

    // Instead of using onRequestPermissionsResult, use this
    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set up RecyclerView with the adapter
        binding.recyclerView.adapter = adapter

        // Initialize FusedLocationProviderClient for fetching current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // Check if location permissions are granted, otherwise request them
        if (checkLocationPermission()) {
            fetchWeatherForCurrentLocation()
        } else {
            requestLocationPermission()
        }

        // Observe the state flow from ViewModel and update UI
        subscribeToStates()
        // Fetch weather for the last searched city
        viewModel.fetchLastCity()
        // Set up search functionality
        setupSearchView()
    }

    // Check if location permissions are granted
    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    // Fetch weather using the user's current location
    private fun fetchWeatherForCurrentLocation() {
        if (checkLocationPermission()) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    // Call ViewModel to fetch weather using lat/lon
                    viewModel.getWeather(lat, lon)
                } else {
                    // Handle case where location is null (optional: show a message to user)
                    Toast.makeText(requireContext(), "Unable to fetch location", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }


    private fun setupSearchView() {
        // Use binding.searchView, not findViewById
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.fetchWeatherByCity(query)
                    viewModel.saveLastCity(query) // Save the last searched city
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun subscribeToStates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is WeatherListState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyStateView.visibility = View.GONE
                        }

                        is WeatherListState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if (state.weatherList.isNotEmpty()) {
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.emptyStateView.visibility = View.GONE
                                adapter.submitList(state.weatherList) // Submit the weather list to the adapter
                            } else {
                                binding.recyclerView.visibility = View.GONE
                                binding.emptyStateView.visibility = View.VISIBLE
                            }
                        }

                        is WeatherListState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyStateView.visibility = View.VISIBLE
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun onWeatherClick(weather: Weather) {
        // Navigate to WeatherDetailFragment when a weather item is clicked
        val action = WeatherListFragmentDirections
            .actionWeatherListFragmentToWeatherDetailFragment(
                weather.lat,
                weather.lon,
                weather.dt
            )
        view?.findNavController()?.navigate(action)
    }
}
