package com.example.learningfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_ngo_home_page.*
import kotlinx.android.synthetic.main.activity_ngoregistration_2.*

class NgoHomePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ngo_home_page)
        /*new_intent.putExtra(Constants.name,etNgoName.text.toString())
        new_intent.putExtra(Constants.contact,etContact.text.toString())
        new_intent.putExtra(Constants.desc,etDescription.text.toString())
        new_intent.putExtra(Constants.email,etEmail.text.toString())
        new_intent.putExtra(Constants.imgLink,imgUri)*/
        var name=intent.getStringExtra(Constants.name)
        var desc=intent.getStringExtra(Constants.desc)
        var email=intent.getStringExtra(Constants.email)
        var contact=intent.getStringExtra(Constants.contact)
        var imgLink=intent.getStringExtra(Constants.imgLink)
        tvName.setText(name.toString())
        tvDesc.setText(desc.toString())
        tvEmail.setText(email.toString())
        tvPhone.setText(contact.toString())
        Glide.with(this).load(imgLink).into(ImgView)
        btnSignOut.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            finish()
            startActivity(intent)
        }
    }
}