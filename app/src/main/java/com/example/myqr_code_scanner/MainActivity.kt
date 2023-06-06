package com.example.myqr_code_scanner

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.myqr_code_scanner.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity() {

    val option=BarcodeScannerOptions.Builder(

    ).setBarcodeFormats(
        Barcode.FORMAT_QR_CODE,
        Barcode.FORMAT_AZTEC
    )
        .build()
    lateinit var binding:ActivityMainBinding
    private val REQUEST_IMAGE_CAPTURE=1
    private var imageBitmap:Bitmap?=null
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        databaseReference=FirebaseDatabase.getInstance().getReference("data")
       //Writedata("data")

        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.apply {
            Capture.setOnClickListener {

                takeImage()
                textview.text=""
            }
            detect.setOnClickListener {

                detectImage()

            }
        }


    }
//
//    private fun Writedata(s: String) {
//        val key=databaseReference.push().key
//        databaseReference.child(key!!).setValue("")

   // }

    private fun detectImage() {
       if (imageBitmap!=null){
           val image=InputImage.fromBitmap(imageBitmap!!,0)

           val scanner=BarcodeScanning.getClient(option)

           scanner.process(image)
               .addOnSuccessListener { barcodes->
                   if (barcodes.toString()=="[]"){
                       Toast.makeText(this,"Nothing to show to scan",Toast.LENGTH_SHORT).show()

                   }
                   for (barcode in barcodes){
                       val valurType=barcode.valueType
                       when(valurType){
                           Barcode.TYPE_WIFI->{

                               val ssid=barcode.wifi!!.ssid
                               val password=barcode.wifi!!.password
                               val type=barcode.wifi!!.encryptionType

                               binding.textview.text=ssid+"\n"+password+type


                           }

                           Barcode.TYPE_URL->{

                               val tittle=barcode.url!!.title
                               val url=barcode.url!!.url
                               binding.textview.text=tittle+"\n"+url
                           }
                       }
                   }
               }


       }

        else{
            Toast.makeText(this,"Please select a photo ",Toast.LENGTH_SHORT).show()

       }
    }

    private fun takeImage() {


        val intent= Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(intent,REQUEST_IMAGE_CAPTURE)
        }catch (e:java.lang.Exception)
        {

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==REQUEST_IMAGE_CAPTURE && resultCode== RESULT_OK){
            val extras:Bundle?=data?.extras

            imageBitmap=extras?.get("data")as Bitmap
            if(imageBitmap!=null){
                binding.imageview.setImageBitmap(imageBitmap)
            }
        }
    }
}