package com.example.homehealth.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.homehealth.R
import com.example.homehealth.data.HealthRepository
import com.example.homehealth.location.LocationService
import com.example.homehealth.model.Nurse
import kotlinx.coroutines.launch

/**
 * Search screen.
 *
 * - If launched with EXTRA_ZIP, runs the search immediately for that zip.
 * - Otherwise, the user can either type a zip code manually or tap
 *   "Use my location" which asks for permission, fetches the device location,
 *   reverse-geocodes to a zip, and runs the search.
 */
class NurseSearchActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ZIP = "extra_zip"
    }

    private lateinit var zipEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var useLocationButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var progressBar: ProgressBar

    private val repo = HealthRepository()
    private val locationService by lazy { LocationService(this) }
    private val adapter = NursesAdapter(emptyList())

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        val granted = grants[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                grants[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            fetchLocationAndSearch()
        } else {
            toast("Location permission denied — please enter a zip code")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nurse_search)

        zipEditText = findViewById(R.id.zipEditText)
        searchButton = findViewById(R.id.searchButton)
        useLocationButton = findViewById(R.id.useLocationButton)
        recyclerView = findViewById(R.id.nursesRecyclerView)
        emptyView = findViewById(R.id.emptyView)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchButton.setOnClickListener {
            val zip = zipEditText.text.toString().trim()
            if (zip.matches(Regex("\\d{5}"))) {
                search(zip)
            } else {
                toast("Enter a 5-digit zip code")
            }
        }

        useLocationButton.setOnClickListener { onUseLocationClicked() }

        // If we were launched with a zip code (e.g. straight from registration),
        // pre-fill and search.
        intent.getStringExtra(EXTRA_ZIP)?.let { zip ->
            zipEditText.setText(zip)
            search(zip)
        }
    }

    private fun onUseLocationClicked() {
        val hasFine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            fetchLocationAndSearch()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun fetchLocationAndSearch() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            val zip = locationService.getCurrentZipCode()
            if (zip == null) {
                progressBar.visibility = View.GONE
                toast("Couldn't determine zip from your location — please enter one")
                return@launch
            }
            zipEditText.setText(zip)
            search(zip)
        }
    }

    private fun search(zip: String) {
        progressBar.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        lifecycleScope.launch {
            try {
                val nurses = repo.findNursesByZip(zip)
                showResults(nurses)
            } catch (e: Exception) {
                toast("Search failed: ${e.message}")
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun showResults(nurses: List<Nurse>) {
        adapter.updateNurses(nurses)
        if (nurses.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}
