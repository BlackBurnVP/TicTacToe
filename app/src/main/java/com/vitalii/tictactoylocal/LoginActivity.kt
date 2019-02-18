package com.vitalii.tictactoylocal

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth



class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener(loginEvent)
    }

    private val loginEvent = View.OnClickListener {
        loginToFirebase(edEmail.text.toString(),edPassword.text.toString())
    }

    private fun loginToFirebase(email:String, password:String){
        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show()
                    toMain()
                }else{
                    Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        if (currentUser!=null){
            toMain()
        }
    }

    private fun toMain(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }
}
