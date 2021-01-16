package com.example.learningfirebase
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_ngoregistration_2.*
import kotlinx.android.synthetic.main.activity_ngoregistration_2.imgView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
class NgoRegistration2 : AppCompatActivity() {
    //We have certain variables whose value we need to upload//
    private var REQ_CODE = 1
    private var storageRef=Firebase.storage.reference
    private var picUri: Uri? = null
    private var imgUri: String? = null
    // imgUri contains the public downloadable link of your image which is uploaded on firebase//
    private var ref = Firebase.firestore.collection("Ngo")
    //With each correspoding ngo , we will have a set of fields attributed with
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngoregistration_2)
        imgView.setOnClickListener {
            //as soon as image is selected , we should get the download link and get the image uri//
             Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, REQ_CODE)
            }
        }
        //Now let us assume that we have all possible data required by us for uploading to firestore//
        btnSubmit.setOnClickListener {
            if (etDescription.text.toString().isEmpty()||etCity.text.toString().isEmpty() || etContact.text.toString().isEmpty()
                || etNgoName.text.toString().isEmpty()|| etEmail.text.toString().isEmpty() ||etNgoAddress.text.toString().isEmpty()
            )
            {
                Toast.makeText(
                    this@NgoRegistration2,
                    "Please enter a proper description of your NGO before submitting the form",
                    Toast.LENGTH_LONG
                ).show()
            }
            else if (picUri== null) {
                //meaning img not selected by user as of now //
                Toast.makeText(
                    this@NgoRegistration2,
                    "Problem setting up image",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //We know for sure that the pic uri is not null here which simply means that the user has//
                // selected a photo//
                    //We got the download link for the image , now just upload the details//
                var name=etNgoName.text.toString()
                var address=etNgoAddress.text.toString()
                var email=etEmail.text.toString()
                var contact=etContact.text.toString()
                var city= etCity.text.toString()
                var abt: String = etDescription.text.toString()
                var ngo = Ngo(name, city, address, email, contact,abt,imgUri!!)
                 // because we already know that image has been uplaoded to cloud //
                    upload(ngo)
            }
        }
    }
    private fun uploadPicture(name: String)= CoroutineScope(Dispatchers.IO).launch {
        try{
            picUri?.let {
                //that means we are trying to upload this file to the storage//
                // if this uri is null ,then , no img is selected by user//
                var loc=storageRef.child("images/$name")
                // Location of Image//
                loc.putFile(it).await()
                // code after this line will execute only if file is inserted properly//
                Log.i("ImgStored", "YES")
                    var download=loc.downloadUrl
                //get the public download url of the file //
                download.addOnSuccessListener {
                    imgUri=it.toString()
                }
                download.addOnFailureListener {
                    Log.i("ImgDwnLnk", "Failed")
                }
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(this@NgoRegistration2, e.message, Toast.LENGTH_LONG).show()
        }
    }
    private fun upload(result: Ngo) = CoroutineScope(Dispatchers.IO).launch {
        //This function is obviously abt uplaoding ngo data into firebase //
        try {
            //uploading the data of the person //
            ref.add(result).await()
            withContext(Dispatchers.Main)
            {
                Toast.makeText(
                    this@NgoRegistration2,
                    "Saved the details of the NGO and successfully registered" ,
                    Toast.LENGTH_SHORT).show()
                   goToNgoPage()
            }
        }
        catch (e: Exception)
        {
            withContext(Dispatchers.Main)
            {
                Toast.makeText(
                    this@NgoRegistration2,
                    "Can't save details of NGO",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun goToNgoPage() {
        imgView.setImageDrawable(ContextCompat.getDrawable(this@NgoRegistration2,R.drawable.ic_baseline_image_search_24))
        etDescription.setText("")
        etCity.setText("")
        etContact.setText("")
        etNgoAddress.setText("")
        etNgoName.setText("")
        etEmail.setText("")
        val intent=Intent(this@NgoRegistration2,MainActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_CODE) {
            //data.data represents the uri of the img that we have selected //
            data?.data?.let {
                picUri = it// this is the uri of the image that we have seleceted//
                //picUri is just kept to ensure that we know whether the user has even //
                // selected an image or not //
                imgView.setImageURI(it)
                uploadPicture(UUID.randomUUID().toString())
            }
        }
    }
}