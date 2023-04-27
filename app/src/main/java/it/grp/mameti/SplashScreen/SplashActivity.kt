package it.grp.mameti.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.grp.mameti.Login.Login

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        Thread.sleep(1500)
        startActivity(Intent(this, Login::class.java)) //MANDA LLAMAR EL MAIN AL TERMINAR EL SPLASH
        finish()
    }
}