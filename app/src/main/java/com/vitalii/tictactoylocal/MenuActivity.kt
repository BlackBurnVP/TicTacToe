package com.vitalii.tictactoylocal

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_menu.*

class MenuActivity : AppCompatActivity() {

    private lateinit var mSinglePlayer:Button
    private lateinit var mMultiPlayer:Button
    private lateinit var mSignOut:Button
    private lateinit var sp: SharedPreferences
    private lateinit var ed: SharedPreferences.Editor
    private var mAuth: FirebaseAuth? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        ed = sp.edit()

        mSinglePlayer = findViewById(R.id.btnSinglePlay)
        mSinglePlayer.setOnClickListener{
            val intent = Intent(this, SinglePlayer::class.java)
            startActivity(intent)
        }
        mMultiPlayer = findViewById(R.id.btnMultiplayer)
        mMultiPlayer.setOnClickListener {
            val intent = Intent(this,Multiplayer::class.java)
            startActivity(intent)
        }
        mSignOut = findViewById(R.id.btnSignOut)
        mSignOut.setOnClickListener {
            mAuth!!.signOut()
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        val user = sp.getString("email","")
        txtWelcome.text = "$user, Welcome to Tic Tac Toy Game"

    }
}
