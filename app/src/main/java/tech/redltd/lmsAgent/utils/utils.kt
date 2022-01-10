package tech.redltd.lmsAgent.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dmax.dialog.SpotsDialog
import es.dmoral.toasty.Toasty
import tech.redltd.lmsAgent.R
import tech.redltd.lmsAgent.activities.loginActivity.LoginActivity
import java.io.IOException

fun Context.loadingDialog():AlertDialog{
    return SpotsDialog.Builder()
        .setMessage("Loading....")
        .setContext(this).build()
}

fun Context.showAlert(title: String?,message: String?) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK") {
                dialogInterface: DialogInterface, i: Int -> dialogInterface.dismiss()
        }.create()
        .show()
}

fun AppCompatActivity.setHomeToolbar(title:String){
    try {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp)
    }catch (ex:Exception){
        println(" Toolbar Exception ${ex.message}")
        ex.printStackTrace()
    }
}

 fun Context.getCapturedImage(selectedPhotoUri: Uri): Bitmap? {
    var bitmap: Bitmap? = null
    if (Build.VERSION.SDK_INT >= 29) {
        val source: ImageDecoder.Source =  ImageDecoder.createSource( this.contentResolver, selectedPhotoUri)
        try {
            bitmap = ImageDecoder.decodeBitmap(source)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    } else {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedPhotoUri
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return bitmap
}

fun Context.errorToast(text:String){
    Toasty.error(this,text).show()
}

fun Context.successToast(text: String){
    Toasty.success(this,text).show()
}

fun AppCompatActivity.networkError(code:Int){
    when(code){
        401->{
            try {
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }catch (ex:Exception){

            }

        }
      500->{
          showAlert("Server Error","Internal Server Error")
      }
    }
}