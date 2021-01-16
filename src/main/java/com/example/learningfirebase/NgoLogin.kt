package com.example.learningfirebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_ngo_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NgoLogin : AppCompatActivity() {
    private var ref= Firebase.firestore.collection("Ngo")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_login)
        var name=etNgoName.text.toString()
        var contact=etPhone.text.toString()
        var email=etEmail.text.toString()
        login(name,contact,email)

    }
    private fun login(name:String,contact:String,email:String)= CoroutineScope(Dispatchers.IO).launch {
            try{
                val querySnapshot=ref.whereEqualTo("name",name).whereEqualTo("contact",contact)
                .whereEqualTo("email",email).get().await()
                 if(querySnapshot.documents.isEmpty())
                 {
                     Toast.makeText(this@NgoLogin,"No such user exists,",Toast.LENGTH_LONG).show()
                 }
            }
            catch(e:Exception){
                withContext(Dispatchers.Main)
                {
                    Toast.makeText(this@NgoLogin,"Couldn't retrieve the data", Toast.LENGTH_SHORT).show()
                }
            }
        }
}