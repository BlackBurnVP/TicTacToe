package com.vitalii.tictactoylocal

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    private var mDatabase:FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mRef = mDatabase.reference
    private lateinit var sp:SharedPreferences
    private lateinit var ed:SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        ed = sp.edit()


        btnLogin.setOnClickListener(loginEvent)
        btnRegister.setOnClickListener(registerEvent)
    }

    private val loginEvent = View.OnClickListener {
        loginToFirebase(edEmail.text.toString(),edPassword.text.toString())
    }
    private val registerEvent = View.OnClickListener {
        registerToFirebase(edEmail.text.toString(),edPassword.text.toString())
    }

    private fun loginToFirebase(email:String, password:String){
        mAuth!!.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    val currentUser = mAuth!!.currentUser
                    Toast.makeText(this,"Logged in as $email",Toast.LENGTH_SHORT).show()
                    mRef.child("Users").child(splitString(currentUser!!.email!!)).child("Request").setValue(currentUser.uid)
                    ed.putString("email",splitString(currentUser.email!!)).commit()
                    ed.putString("uid",currentUser.uid).commit()
                    toMain()
                }else{
                    Toast.makeText(this,"Invalid login data",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerToFirebase(email:String, password:String){
        mAuth!!.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    val currentUser = mAuth!!.currentUser
                    Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show()
                    mRef.child("Users").child(splitString(currentUser!!.email!!)).child("Request").setValue(currentUser.uid)
                    ed.putString("email",splitString(currentUser.email!!)).commit()
                    ed.putString("uid",currentUser.uid).commit()
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

    fun splitString(str:String):String{
        val split = str.split("@")
        return split[0]
    }
}
