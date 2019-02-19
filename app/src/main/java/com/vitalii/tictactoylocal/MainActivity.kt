package com.vitalii.tictactoylocal

import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private var activePlayer = 1
    private var emptyCell = ArrayList<Int>()
    private var winner =-1
    private var mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mRef = mDatabase.reference
    private var userEmail:String? = ""
    private val login = LoginActivity()

    private lateinit var sp: SharedPreferences
    private lateinit var ed: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        ed = sp.edit()

        userEmail = sp.getString("email","")

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        btnInvite.setOnClickListener(invite)
        btnAccept.setOnClickListener(accept)

        incomingRequests()
    }

    protected fun onClick(view: View){

        val btnSelected = view as Button
        var cellID = 0
        when(btnSelected.id){
            R.id.btn1 -> cellID=1
            R.id.btn2 -> cellID=2
            R.id.btn3 -> cellID=3
            R.id.btn4 -> cellID=4
            R.id.btn5 -> cellID=5
            R.id.btn6 -> cellID=6
            R.id.btn7 -> cellID=7
            R.id.btn8 -> cellID=8
            R.id.btn9 -> cellID=9
        }

        Toast.makeText(this,"Clicked $cellID",Toast.LENGTH_SHORT).show()
        playGame(cellID,btnSelected)
    }

    private fun playGame(cellId:Int,btnSelected:Button) {
            if (activePlayer == 1) {
                btnSelected.text = "X"
                btnSelected.setBackgroundColor(Color.GREEN)
                player1.add(cellId)
                findWinner()
                activePlayer = 2
                if(winner == -1){
                    artificialPlayer()
                }
            }; else if (activePlayer == 2) {
                btnSelected.text = "O"
                btnSelected.setBackgroundColor(Color.BLUE)
                player2.add(cellId)
                activePlayer = 1
            }
            btnSelected.isEnabled = false
            //findWinner()
    }

    private fun findWinner(){
        if(player1.contains(1) && player1.contains(2) && player1.contains(3)){
            winner = 1
        }

        if(player1.contains(4) && player1.contains(5) && player1.contains(6)){
            winner = 1
        }

        if(player1.contains(7) && player1.contains(8) && player1.contains(9)){
            winner = 1
        }

        if(player1.contains(1) && player1.contains(4) && player1.contains(7)){
            winner = 1
        }

        if(player1.contains(2) && player1.contains(5) && player1.contains(8)){
            winner = 1
        }

        if(player1.contains(3) && player1.contains(6) && player1.contains(9)){
            winner = 1
        }
        /*
        * PLAYER TWO
        * */

        if(player2.contains(1) && player2.contains(2) && player2.contains(3)){
            winner = 2
        }

        if(player2.contains(4) && player2.contains(5) && player2.contains(6)){
            winner = 2
        }

        if(player2.contains(7) && player2.contains(8) && player2.contains(9)){
            winner = 2
        }

        if(player2.contains(1) && player2.contains(4) && player2.contains(7)){
            winner = 2
        }

        if(player2.contains(2) && player2.contains(5) && player2.contains(8)){
            winner = 2
        }

        if(player2.contains(3) && player2.contains(6) && player2.contains(9)){
            winner = 2
        }

        if(winner != -1){
            announceWinner()
//            if(winner==1){
//                Toast.makeText(this,"WINNER IS PLAYER 1",Toast.LENGTH_SHORT).show()
//                activePlayer = 0
//            };else{
//                Toast.makeText(this,"WINNER IS PLAYER 2",Toast.LENGTH_SHORT).show()
//            }
        }
    }

    private fun artificialPlayer(){
        for (cellId in 1..9){
            if(!(player1.contains(cellId) || player2.contains(cellId))){
                emptyCell.add(cellId)
            }
        }
        val r = Random()
        val randIndex = r.nextInt(emptyCell.size-0)+0
        val cellId = emptyCell[randIndex]

        val btnSelected:Button
        when(cellId){
            1-> btnSelected = btn1
            2-> btnSelected = btn2
            3-> btnSelected = btn3
            4-> btnSelected = btn4
            5-> btnSelected = btn5
            6-> btnSelected = btn6
            7-> btnSelected = btn7
            8-> btnSelected = btn8
            9-> btnSelected = btn9
            else -> btnSelected = btn1
        }
        findWinner()
        if(winner == -1){
            playGame(cellId,btnSelected)
        }
    }

    private fun announceWinner(){
            Toast.makeText(this,"WINNER IS PLAYER $winner",Toast.LENGTH_SHORT).show()
    }

    private val invite = View.OnClickListener {
        val email = edOtherEmail.text.toString()
        println(userEmail)
        println(email)
        if (splitString(email)!=userEmail){
            mRef.child("Users").child(splitString(email)).child("Request").push().setValue(userEmail)
        }else{
            Toast.makeText(this,"You can't invite yourself",Toast.LENGTH_SHORT).show()
        }
    }
    private val accept = View.OnClickListener {
        val email = edOtherEmail.text.toString()
        mRef.child("Users").child(splitString(email)).child("Request").push().setValue(userEmail)
    }

    private fun incomingRequests(){
        mRef.child("Users").child(splitString(userEmail!!)).child("Request")
            .addValueEventListener(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children){
                        val td = dataSnapshot.value as HashMap<String, Any>
                        val values = td[snapshot.key!!].toString()
                        edOtherEmail.setText(values)
                        mRef.child("Users").child(splitString(userEmail!!)).child("Request").setValue(true)
                        break
                    }
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun splitString(str:String):String{
        val split = str.split("@")
        return split[0]
    }
}
