package com.vitalii.tictactoylocal

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_multiplayer.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.lang.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Multiplayer : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private var mAuth: FirebaseAuth? = null
    private var player1 = ArrayList<Int>()
    private var player2 = ArrayList<Int>()
    private var activePlayer = 1
    private var usedCell = ArrayList<Button>()
    private var winner =-1
    private var mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var mRef = mDatabase.reference
    private var myEmail:String? = ""
    private val login = LoginActivity()
    private var myTurn:Boolean = false

    private lateinit var sp: SharedPreferences
    private lateinit var ed: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer)

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        ed = sp.edit()

        myEmail = sp.getString("email","")

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()

        btnInvite.setOnClickListener(invite)

        incomingRequests()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when(item!!.itemId){
            R.id.toMenu -> {
                val intent = Intent(this,MenuActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onClick(view: View){

        if(winner== -1){
            if(myTurn) {
                val btnSelected = view as Button
                var cellID = 0
                when (btnSelected.id) {
                    R.id.btn1 -> cellID = 1
                    R.id.btn2 -> cellID = 2
                    R.id.btn3 -> cellID = 3
                    R.id.btn4 -> cellID = 4
                    R.id.btn5 -> cellID = 5
                    R.id.btn6 -> cellID = 6
                    R.id.btn7 -> cellID = 7
                    R.id.btn8 -> cellID = 8
                    R.id.btn9 -> cellID = 9
                }
                mRef.child("OnlinePlayer").child(sessionID!!).child(cellID.toString()).setValue(myEmail)
                myTurn = false
            }
        }else{
            announceWinner()
        }
    }

    private fun playGame(cellId:Int,btnSelected:Button) {
            if (activePlayer == 1) {
                btnSelected.text = "X"
                btnSelected.setBackgroundColor(Color.GREEN)
                player1.add(cellId)
                activePlayer = 2

            }else{
                btnSelected.text = "O"
                btnSelected.setBackgroundColor(Color.BLUE)
                player2.add(cellId)
                activePlayer = 1
            }
            btnSelected.isEnabled = false
            usedCell.add(btnSelected)
            findWinner()
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
        }
    }

    private fun autoPlay(cellId:Int){

        val btnSelected:Button = when(cellId){
            1-> btn1
            2-> btn2
            3-> btn3
            4-> btn4
            5-> btn5
            6-> btn6
            7-> btn7
            8-> btn8
            9-> btn9
            else -> btn1
        }
        playGame(cellId,btnSelected)
    }

    private var sessionID:String? = ""
    private var playerSymbol:String? = null

    private fun onlinePlayer(sessionID:String){
        this.sessionID = sessionID

        mRef.child("OnlinePlayer").child(sessionID)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                        player1.clear()
                        player2.clear()
                    try {
                        if (dataSnapshot.value!= null) {
                            val td = HashMap<String,Any>()
                            for (snapshot in dataSnapshot.children){
                                val value = snapshot.value
                                td[snapshot.key!!] = value!!
                            }
                            for (key in td.keys) {
                                val value = td[key].toString()

                                if (value == myEmail) {
                                    activePlayer = if (playerSymbol == "X") 1 else 2
                                } else {
                                    activePlayer = if (playerSymbol == "X") 2 else 1
                                    myTurn = true
                                }
                                autoPlay(key.toInt())
                            }
                        }
                    }catch (ex:Exception){ }
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
    }

    private fun announceWinner(){
        Toast.makeText(this,"WINNER IS PLAYER $winner",Toast.LENGTH_SHORT).show()
        mRef.child("OnlinePlayer").child(sessionID!!).removeValue()
    }

    private val invite = View.OnClickListener {
        val email = edOtherEmail.text.toString()
        if (login.splitString(email)!=myEmail){
            sessionID = myEmail+login.splitString(email)
            mRef.child("Users").child(login.splitString(email)).child("Request").push().setValue(myEmail)
            mRef.child("OnlinePlayer").child(sessionID!!).setValue("Waiting for $email")
            Toast.makeText(this,"Waiting for $email answer",Toast.LENGTH_SHORT).show()
            onlinePlayer(sessionID!!)
            playerSymbol = "X"
            myTurn = true
        }else{
            Toast.makeText(this,"You can't invite yourself",Toast.LENGTH_SHORT).show()
        }
    }


    private fun accept(){
        val email = edOtherEmail.text.toString()
        sessionID = login.splitString(email)+myEmail
        //mRef.child("OnlinePlayer").child(sessionID!!).removeValue()
        mRef.child("Users").child(login.splitString(email)).child("Request").push().setValue(myEmail)
        mRef.child("OnlinePlayer").child(sessionID!!).setValue("Accepted")
        onlinePlayer(sessionID!!)
        playerSymbol = "O"
    }

    private fun decline(){
        val email = edOtherEmail.text.toString()
        sessionID = login.splitString(email)+myEmail
        mRef.child("OnlinePlayer").child(sessionID!!).setValue("Declined")
        edOtherEmail.text.clear()
    }

    private var number = 0

    private fun incomingRequests(){
        mRef.child("Users").child(login.splitString(myEmail!!)).child("Request")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children){
                        val tp = dataSnapshot.value as HashMap<String, Any>
                        val value = tp[snapshot.key!!].toString()
                        edOtherEmail.setText(value)
//                        val notifyMe = Notifications()
//                        notifyMe.Notify(this@Multiplayer,values,number)
//                        number++
                        mRef.child("Users").child(login.splitString(myEmail!!)).child("Request").setValue(true)
                        break
                    }
                }
                override fun onCancelled(p0: DatabaseError) {}
            })
        mRef.child("OnlinePlayer").child(sessionID!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for(snapshot in dataSnapshot.children){
                        val td = HashMap<String,Any>()
                        val value = snapshot.value
                        td[snapshot.key!!] = value!!

                        when(td[snapshot.key!!.toString()]){
                            "Accepted" ->{
                                refresh()
                                Toast.makeText(this@Multiplayer,"Accepted",Toast.LENGTH_SHORT).show()
                                txtConnected.text = "Connected with ${edOtherEmail.text}"
                            }
                            "Waiting for $myEmail" ->{
                                alert()
                            }
                            "Declined" ->{
                                Toast.makeText(this@Multiplayer,"Declined",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {}
            })

    }

    override fun onDestroy() {
        super.onDestroy()
        mRef.child("OnlinePlayer").child(sessionID!!).removeValue()
    }

    @SuppressLint("NewApi")
    private fun refresh(){
        winner = -1
        for (btn in usedCell){
            btn.isEnabled = true
            btn.background = resources.getDrawable(R.drawable.background_btn)
            btn.text = ""
        }
    }

    private fun alert(){
        val email = edOtherEmail.text.toString()
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Player $email has invited you to the game")
            .setTitle("TicTacToy Invite")
        builder.setPositiveButton("Accept", DialogInterface.OnClickListener { dialog, id ->
            accept()
        })
        builder.setNegativeButton("Decline", DialogInterface.OnClickListener{dialog, id ->
            decline()
        })
        val dialog = builder.create()
        dialog.show()
    }
}
