package tech.redltd.lmsAgent.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Base64
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.InputStream


class AppUtils(private val context: Context) {



    fun saveDataIntoPreference(key: String?,value: String?) {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getDataFromPreference(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "")
    }

      fun checkInternet(): Boolean {
        var isInternetOK = false
        try {
            val connectivityManager =
                this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.isAvailable && networkInfo.isConnected) {
                isInternetOK = true
            }
        } catch (ex: Exception) {
        }
        return isInternetOK
    }

    fun goSettingsActivity() {
        if (!checkInternet()) {
            AlertDialog.Builder(context)
                .setMessage("Sorry! Internet is not enable.")
                .setTitle("Internet Disable")
                .setCancelable(false)
                .setPositiveButton(
                    "Settings"
                ) { dialogInterface, i ->
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    ContextCompat.startActivity(context, intent, null)
                }
                .create()
                .show()
        }
    }

    fun getEncoded64ImageStringFromBitmap(bitmap: Bitmap,quality:Int): String? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.JPEG, quality, stream)
        val byteFormat: ByteArray = stream.toByteArray()
        // Get the Base64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    fun getAllDivisions(): Array<Division> {
        var divisions : Array<Division> = arrayOf()
        try {
            val  inputStream: InputStream = context.assets.open("divisions.json")
            val json = inputStream.bufferedReader().use{it.readText()}
            val gson = Gson()
            divisions  = gson.fromJson(json,Array<Division>::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return divisions
    }

    fun getAllDistricts(): Array<District> {
        var districts : Array<District> = arrayOf()
        try {
            val  inputStream: InputStream = context.assets.open("districts.json")
            val json = inputStream.bufferedReader().use{it.readText()}
            val gson = Gson()
            districts  = gson.fromJson(json,Array<District>::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return districts
    }

    fun getAllThana(): Array<Thana> {
        var thans : Array<Thana> = arrayOf()
        try {
            val  inputStream: InputStream = context.assets.open("thana.json")
            val json = inputStream.bufferedReader().use{it.readText()}
            val gson = Gson()
            thans  = gson.fromJson(json,Array<Thana>::class.java)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return thans
    }
}