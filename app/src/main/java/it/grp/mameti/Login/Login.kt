package it.grp.mameti.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import it.grp.mameti.CreateProfiles.CreateHumanProfile
import it.grp.mameti.R

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //val btLogin: Button = findViewById(R.id.btLogin) //EJEMPLO DE DECLARACION DE UN COMPONENTE
    }

    //NAVEGAR A LAS DIFERENTES PANTALLAS
    fun forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPassword::class.java))
    }
    fun createAccount(view: View) {
        startActivity(Intent(this, CreateHumanProfile::class.java))
    }

}