package com.example.learningfirebase
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dashboard.*
class DashboardActivity : AppCompatActivity() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.token_id)).requestEmail().build()
        //To ensure in the Dashboard Activity that we signout with the right account , we need to use pass a variable //
        // with the intent which ensures that we sign out of the right account //
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        //I am getting the Constants.account variable value so that when the user presses//
        // the signout button , we know that which account we want to sign out from//
        auth = FirebaseAuth.getInstance()
        var user=auth.currentUser
        if(user!=null)
        {
            var name=user?.displayName
            tvName.text=name
            var email=user?.email
            tvEmail.text=email
            var dpUrl=user?.photoUrl
            Glide.with(this).load(dpUrl).into(ImgView)
        }
        signout.setOnClickListener {
            if(Constants.account==null)
            {
                auth.signOut()
                LoginManager.getInstance().logOut()
                mGoogleSignInClient.signOut().addOnCompleteListener {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    LoginManager.getInstance().logOut()
                    val nalpha = Intent(this, MainActivity::class.java)
                    startActivity(nalpha)
                }
            }
            else if(Constants.account=="google")
            mGoogleSignInClient.signOut().addOnCompleteListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
             else if(Constants.account=="facebook")
            {
                LoginManager.getInstance().logOut()
                val nalpha = Intent(this, MainActivity::class.java)
                startActivity(nalpha)
            }
        }
    }
}