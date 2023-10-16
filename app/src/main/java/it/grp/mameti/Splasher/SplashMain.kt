package it.grp.mameti.Splasher

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_splash_main.*

var mail = ""
var prov = ""

enum class ProviderType{
    BASIC,
    GOOGLE
}

class SplashMain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_main)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        //GUARDADO DE DATOS DE INICIO DE SESION
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("proveedor", prov)
        prefs.apply()

        //NAVEGACION POR VENTANAS
        guiaMonitoreo()
        guiaPerfilHumano(mail, ProviderTypeU.BASIC)
        guiaPerfilMascota(mail, ProviderTypeM.BASIC)
    }

    private fun setup(email:String, proveedor:String){
        title = "Inicio"
        mail = email
        prov = proveedor.toString()

        //BOTON PARA CERRAR SESION
        this.btnLogoutUs.setOnClickListener{
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    //NAVEGAR POR LAS VENTANAS
    private fun guiaMonitoreo(){
        btnMonitor.setOnClickListener {
            startActivity(Intent(this, Monitoreo::class.java))
        }
    }
    //DE LA MISMA MANERA EN QUE SE RECIBEN LOS DATOS EN ESTA CLASE, ESTOS DEBEN SER PASADOS A LOS
    //PERFILES DE HUMANO Y MASCOTA
    private fun guiaPerfilHumano(email: String, proveedor: ProviderTypeU){
        btnHumano.setOnClickListener {
            startActivity(Intent(this, Usuario::class.java).apply {
                putExtra("email", email)
                putExtra("proveedor", proveedor.name)
            })
        }
    }
    private fun guiaPerfilMascota(email: String, proveedor: ProviderTypeM){
        btnMascota.setOnClickListener{
            startActivity(Intent(this, Mascota::class.java).apply{
                putExtra("email", email)
                putExtra("proveedor", proveedor.name)
            })
        }
    }

}