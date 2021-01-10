package com.eddp.busapp

import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.eddp.busapp.data.Post
import com.eddp.busapp.data.Station
import com.eddp.busapp.data.StationAPI
import com.eddp.busapp.data.StationResponse
import com.eddp.busapp.interfaces.AsyncDataObservable
import com.eddp.busapp.interfaces.AsyncDataObserver
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity(), AsyncDataObservable {
    private var _observers: MutableList<AsyncDataObserver?> = ArrayList()
    private var _fragments: List<Fragment> = listOf(Home(), StationsViewPager(), UserPics())

    private var _posts: List<Post>? = null
    private var _stations: List<Station>? = null

    // Getters
    fun getPosts() : List<Post>? = this._posts
    fun getStations() : List<Station>? = this._stations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getStationsFromAPI()
    }

    private fun getStationsFromAPI() {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("http://barcelonaapi.marcpous.com")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val service = retrofit.create(StationAPI::class.java)
        val call = service.stationsList()

        call.enqueue(object : Callback<StationResponse> {
            override fun onResponse(
                    call: Call<StationResponse>,
                    response: Response<StationResponse>
            ) {
                val statusCode: Int = response.code()
                val resp: StationResponse? = response.body()

                if (!response.isSuccessful) {
                    Toast.makeText(
                            this@MainActivity,
                            "Error ${statusCode}: " + this@MainActivity.getString(R.string.get_stations_error),
                            Toast.LENGTH_SHORT
                    ).show()
                } else {
                    this@MainActivity._stations = resp?.data?.stations
                    notifyGet()
                }
            }

            override fun onFailure(call: Call<StationResponse>?, err: Throwable) {
                Toast.makeText(
                        this@MainActivity,
                        this@MainActivity.getString(R.string.get_stations_error),
                        Toast.LENGTH_SHORT
                ).show()
                Log.e("ERROR", err.message, err)
            }
        })
    }

    // Observable
    override fun registerReceiver(dataObserver: AsyncDataObserver) {
        if (!this._observers.contains(dataObserver)) {
            this._observers.add(dataObserver)
        }
    }

    override fun unregisterReceiver(dataObserver: AsyncDataObserver) {
        if (!this._observers.contains(dataObserver)) {
            this._observers.remove(dataObserver)
        }
    }

    override fun notifyGet() {
        for (observer in this._observers) {
            observer?.onDataGet()
        }
    }

    companion object {
        // Permissions
        fun allPermissionGranted(context: Context, requiredPermissions: List<String>) : Boolean {
            return requiredPermissions.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun getMissingPermissions(
            context: Context,
            requiredPermissions: List<String>
        ) : MutableList<String>? {
            val missingPermissions: MutableList<String> = ArrayList()

            for (permission in requiredPermissions) {
                when {
                    ContextCompat.checkSelfPermission(context, permission)
                            != PackageManager.PERMISSION_GRANTED -> {
                        missingPermissions.add(permission)
                    }
                }
            }

            return if (missingPermissions.size > 0)
                missingPermissions else null
        }

        fun getMissingPermissionDialog(
            context: Context,
            title: String,
            message: String,
            positiveButtonText: String,
            onPositiveButtonClick: DialogInterface.OnClickListener) : AlertDialog.Builder {

            return AlertDialog
                .Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButtonText, onPositiveButtonClick)
        }
    }
}