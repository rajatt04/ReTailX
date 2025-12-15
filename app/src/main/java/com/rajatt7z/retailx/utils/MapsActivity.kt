package com.rajatt7z.retailx.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.rajatt7z.retailx.R
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.util.Locale

class MapsActivity : AppCompatActivity() {

    private lateinit var map: MapView
    private var selectedAddress: String = ""
    private lateinit var tvCurrentAddress: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show()
                // Default to India
                val india = GeoPoint(20.5937, 78.9629)
                map.controller.setCenter(india)
                map.controller.setZoom(5.0)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load OSMDroid configuration
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName

        setContentView(R.layout.activity_maps)

        tvCurrentAddress = findViewById(R.id.tvCurrentAddress)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmLocation)
        val etSearch = findViewById<android.widget.EditText>(R.id.etSearch)
        val btnSearch = findViewById<android.widget.ImageButton>(R.id.btnSearch)
        val fabMyLocation = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabMyLocation)
        
        map = findViewById(R.id.map)

        setupMap()

        btnConfirm.setOnClickListener {
            if (selectedAddress.isNotEmpty()) {
                val resultIntent = Intent()
                resultIntent.putExtra("address", selectedAddress)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please select a location", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Search functionality
        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchLocation(query)
                // Hide keyboard
                 val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                 imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
            }
        }
        
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    searchLocation(query)
                    val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
                }
                true
            } else {
                false
            }
        }
        
        // My Location Button
        fabMyLocation.setOnClickListener {
            checkPermissionAndEnableLocation()
        }

        checkPermissionAndEnableLocation()
    }
    
    private fun searchLocation(query: String) {
        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(query, 1)
                
                runOnUiThread {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val geoPoint = GeoPoint(address.latitude, address.longitude)
                        map.controller.animateTo(geoPoint)
                        map.controller.setZoom(15.0)
                        
                        // Update address text
                        val sb = StringBuilder()
                        for (i in 0..address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        selectedAddress = sb.toString().trim()
                        tvCurrentAddress.text = selectedAddress
                        
                    } else {
                        Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Search failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }


    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(5.0)
        
        // Add Map Listener to detect movement
        map.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                // Determine center and geocode
                // To avoid spamming geocoder, we could use a timer or just do it. 
                // Native geocoder is synchronous, better run in background or use a delayed query.
                // For simplicity in this demo, we'll do it on idle-ish, but OSMDroid doesn't have "onIdle".
                // We'll update only when the user stops interacting? 
                // Actually, let's just update the address when the user clicks verify? 
                // No, the UX expects realtime-ish updates.
                // Let's settle for updating when scroll happens but fast.
                // Better: Update only when CONFIRM is clicked? 
                // The prompt says "Move pin to select location".
                // Let's update address on scroll end? No easy event.
                // Let's just update "Selected Location" text to coordinates first, and geocode only on confirm?
                // OR: Run geocoding on a separate thread with debounce. 
                // Simplified approach: Update address when "Confirm" is clicked? 
                // No, user wants to see what they picked.
                // I will add a delayed runnable to fetch address 1 second after last scroll.
                return true
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return true
            }
        })
        
        // Better implementation for "Idle":
        // Since we don't have a reliable idle listener, let's trigger geocoding when the user clicks "Confirm" 
        // OR simply add a floating "Update Address" button?
        // Let's go with the debounce approach on the main thread for simplicity via handler.
    }
    
    // Using a simple timer for "idle" detection
    private var lastMapUpdate = 0L
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val updateAddressRunnable = Runnable {
        val center = map.mapCenter
        if (center is GeoPoint) {
            getAddressFromLocation(center)
        }
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        // Intercept touch to detect "idle"
        if (ev?.action == android.view.MotionEvent.ACTION_UP) {
            handler.removeCallbacks(updateAddressRunnable)
            handler.postDelayed(updateAddressRunnable, 1000) // 1 second delay
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun checkPermissionAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        // OSMDroid has a MyLocationOverlay, but we can just move camera once.
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentGeoPoint = GeoPoint(location.latitude, location.longitude)
                    map.controller.setCenter(currentGeoPoint)
                    map.controller.setZoom(18.0)
                    getAddressFromLocation(currentGeoPoint)
                } else {
                     val india = GeoPoint(20.5937, 78.9629)
                     map.controller.setCenter(india)
                     map.controller.setZoom(5.0)
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun getAddressFromLocation(geoPoint: GeoPoint) {
        tvCurrentAddress.text = "Fetching address..."
        Thread {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                // Basic android geocoder usually works without API key on devices with Play Services.
                val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
                
                runOnUiThread {
                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val sb = StringBuilder()
                        for (i in 0..address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i)).append("\n")
                        }
                        selectedAddress = sb.toString().trim()
                        tvCurrentAddress.text = selectedAddress
                    } else {
                        tvCurrentAddress.text = "Unknown Location"
                        selectedAddress = ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvCurrentAddress.text = "Cannot load address"
                }
            }
        }.start()
    }
    
    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
