package com.example.learningfirebase
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.*
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    private var currAccount:String?=null
    lateinit var callbackManager:CallbackManager
    private lateinit var googleSignInClient:GoogleSignInClient
    //firebase authentication object which lets us know whether user is logged in or not //
    companion object{
        var REQUEST_CODE=1
    }
    override fun onStart() {
        //on Start is actually responsible for loading the page in a sense//
        super.onStart()
        val user=auth.currentUser
        if(user!=null)
        {
            val next_pg=Intent(this,DashboardActivity::class.java)
            startActivity(next_pg)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Initializing facebook sdk//
        auth= FirebaseAuth.getInstance()
        btnNgoLogin.setOnClickListener {
          val intent=Intent(this@MainActivity,NgoLogin::class.java)
            startActivity(intent)
        }
        btnNgoRegister.setOnClickListener {
            val intent=Intent(this@MainActivity,NgoRegistration2::class.java)
            startActivity(intent)
        }
        //make an authentication object of your sign ins and sign outs//
        callbackManager = CallbackManager.Factory.create()
        FacebookSdk.sdkInitialize(getApplicationContext());
            login_button.setReadPermissions("email", "public_profile")
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("Facebook Login Result", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }
            override fun onCancel() {
                Log.d("FB ON Cancel", "facebook:onCancel")
            }
            override fun onError(error: FacebookException) {
                Log.d("FB error", "facebook:onError", error)
            }
        })
        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
        requestIdToken(getString(R.string.token_id)).requestEmail().build()
        googleSignInClient=GoogleSignIn.getClient(this,gso)
        signUp.setOnClickListener {
            signIn()
        }
    }
    private fun signIn(){
        val intent=googleSignInClient.signInIntent
        // get the google account selection intent //
        startActivityForResult(intent, REQUEST_CODE)
    }
   private fun firebaseAuthWithGoogle(idToken:String)
   {
       val cred=GoogleAuthProvider.getCredential(idToken,null)
           auth.signInWithCredential(cred)
                   .addOnCompleteListener(this) { task ->
                       if (task.isSuccessful) {
                           Log.i("SignInActivity", "SignInWithCredential:Success")
                           val intent = Intent(this, DashboardActivity::class.java)
                           currAccount="google"
                           intent.putExtra(Constants.account,currAccount)
                           startActivity(intent)
                         //Constants.account is a static variable so can be accessed anywhere in the class//
                       } else {
                           Toast.makeText(this@MainActivity, "Couldn't register with firebase", Toast.LENGTH_SHORT).show()
                           Log.i("SignInActivity", "signInWithCredential:Failed")
                       }
                   }
   }
    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("FB Access Token", "handleFacebookAccessToken:$token")
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Facebook Authentication", "signInWithCredential:success")
                    val new_intent=Intent(this,DashboardActivity::class.java)
                    currAccount="facebook"
                    new_intent.putExtra(Constants.account,currAccount)
                    startActivity(new_intent)
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Facebook Authentication","signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        applicationContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REQUEST_CODE)
        {
            val task=GoogleSignIn.getSignedInAccountFromIntent(data)
            //This will just get the email account you have selected//
            val exception=task.exception
            if(task.isSuccessful)
            {
                //user has selected an email id //
                try{
                    val account=task.getResult(ApiException::class.java)
                    Log.i("SignInActivity","firebaseAuthWithGoogle:"+account!!.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                    //Your app must also verify its credentials in order to store the data of this account//
                }
                catch(e:ApiException)
                {
                    Log.i("SignInAcitivty","Google Sign In Failed"+e.statusCode)
                }
            }
            else
            {
                Log.i("Sign in Activity",exception.toString())
            }
        }
        else
        {
            callbackManager.onActivityResult(requestCode,resultCode,data)
        }
    }
}