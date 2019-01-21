package com.vitalii.tictactoylocal

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var Player1 = ArrayList<Int>()
    var Player2 = ArrayList<Int>()
    var ActivePlayer = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun onClick(view: View){

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
        if (ActivePlayer == 1) {
            btnSelected.text = "X"
            btnSelected.setBackgroundColor(Color.GREEN)
            Player1.add(cellId)
            ActivePlayer = 2
        };else{
            btnSelected.text = "O"
            btnSelected.setBackgroundColor(Color.BLUE)
            Player2.add(cellId)
            ActivePlayer = 1
        }
        btnSelected.isEnabled = false
        findWinner()
    }

    private fun findWinner(){
        var winner = -1
        if(Player1.contains(1) && Player1.contains(2) && Player1.contains(3)){
            winner = 1
        }

        if(Player1.contains(4) && Player1.contains(5) && Player1.contains(6)){
            winner = 1
        }

        if(Player1.contains(7) && Player1.contains(8) && Player1.contains(9)){
            winner = 1
        }

        if(Player1.contains(1) && Player1.contains(4) && Player1.contains(7)){
            winner = 1
        }

        if(Player1.contains(2) && Player1.contains(5) && Player1.contains(8)){
            winner = 1
        }

        if(Player1.contains(3) && Player1.contains(6) && Player1.contains(9)){
            winner = 1
        }
        /*
        * PLAYER TWO
        * */

        if(Player2.contains(1) && Player2.contains(2) && Player2.contains(3)){
            winner = 2
        }

        if(Player2.contains(4) && Player2.contains(5) && Player2.contains(6)){
            winner = 2
        }

        if(Player2.contains(7) && Player2.contains(8) && Player2.contains(9)){
            winner = 2
        }

        if(Player2.contains(1) && Player2.contains(4) && Player2.contains(7)){
            winner = 2
        }

        if(Player2.contains(2) && Player2.contains(5) && Player2.contains(8)){
            winner = 2
        }

        if(Player2.contains(3) && Player2.contains(6) && Player2.contains(9)){
            winner = 2
        }

        if(winner != -1){
            if(winner==1){
                Toast.makeText(this,"WINNER IS PLAYER 1",Toast.LENGTH_SHORT).show()
            };else{
                Toast.makeText(this,"WINNER IS PLAYER 2",Toast.LENGTH_SHORT).show()
            }
        }
    }
}