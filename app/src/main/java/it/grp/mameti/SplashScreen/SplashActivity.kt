package it.grp.mameti.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import it.grp.mameti.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(1500)
        startActivity(Intent(this, MainActivity::class.java)) //MANDA LLAMAR EL MAIN AL TERMINAR EL SPLASH
        finish()
    }
}