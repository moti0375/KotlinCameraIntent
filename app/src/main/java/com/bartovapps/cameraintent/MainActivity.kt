package com.bartovapps.cameraintent

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val TAG = "MainActivity"
    lateinit var imageFilePath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cameraButton.setOnClickListener({
            try{
                val imageFile = createOutputFile()
                val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val authorities = packageName + ".fileprovider"
                val imageUri = FileProvider.getUriForFile(this, authorities, imageFile)

                Log.i(TAG, imageUri.toString())
                callCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                if(callCameraIntent.resolveActivity(packageManager) != null){
                    startActivityForResult(callCameraIntent, CAMERA_REQUEST_CODE)
                }
            }catch (e: Exception){
                Toast.makeText(this, "Could not create the file", Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                Log.i(TAG, "onActivityResult, requestCode " )
                if(resultCode == Activity.RESULT_OK){
                        Log.i(TAG, "Result OK, data != null")
                        photoImageView.setImageBitmap(setScaledBitmap())
                }
            }
            else -> {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createOutputFile() : File {
        val timestamp = SimpleDateFormat("yyyyMMddmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timestamp + "_"
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if(!storageDirectory.exists()){
            storageDirectory.mkdirs()
        }

        val imageFile = createTempFile(imageFileName, ".jpg", storageDirectory)

        imageFilePath = imageFile.absolutePath
        return imageFile
    }

    fun setScaledBitmap() : Bitmap {
        val imageViewWidth = photoImageView.width
        val imageViewHeight = photoImageView.height

        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFile(imageFilePath, bitmapOptions)
        val bitmapWidth = bitmapOptions.outWidth
        val bitmapHeight = bitmapOptions.outHeight

        val scaleFactor = Math.min(bitmapWidth/imageViewWidth, bitmapHeight/imageViewHeight)

        bitmapOptions.inSampleSize = scaleFactor

        bitmapOptions.inJustDecodeBounds = false


        return BitmapFactory.decodeFile(imageFilePath, bitmapOptions)

    }
}
