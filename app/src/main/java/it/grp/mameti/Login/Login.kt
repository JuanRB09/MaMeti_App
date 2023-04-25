package it.grp.mameti.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import it.grp.mameti.CreateProfiles.CreateHumanProfile
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_login.*

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //EVENTOS DE GOOGLE ANALYTICS
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen",bundle)

        acceso()
    }

    //AUTENTICACION CON FIREBASE (CORREO)
    private fun acceso() {
        title = "Autenticación"

        btLogin.setOnClickListener{
            if(etUsuario.text.isNotEmpty() && etContrasena.text.isNotBlank()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etUsuario.text.toString(),
                    etContrasena.text.toString()).addOnCompleteListener(){
                    if (it.isSuccessful){
                        startActivity(Intent(this, CreateHumanProfile::class.java))
                        finish()
                    }else{
                        alerta_noAuth()
                    }
                }
            }else{
                alerta_noData()
            }
        }

    }
    //METODOS COMPLEMENTO: MUESTRA DE ALERTAS
    private fun alerta_noAuth(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("No se ha podido autenticar al usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun alerta_noData(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Complete ambos campos.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //NAVEGAR A LAS DIFERENTES PANTALLAS
    fun forgotPassword(view: View) {
        startActivity(Intent(this, ForgotPassword::class.java))
    }
    fun createAccount(view: View) {
        startActivity(Intent(this, CreateHumanProfile::class.java))
    }

}

    //CODIGO DE EJEMPLO PARA PASAR COMO PARAMETRO EL CORREO Y EL PROVEEDOR DEL MISMO
//    private fun principal(usuario: String, proveedor: ProviderType){
//        val homeIntent = Intent(this,CreateHumanProfile::class.java).apply {
//            putExtra("usuario", usuario)
//            putExtra("proveedor", proveedor.name)
//        }
//        startActivity(homeIntent)
//
//        //EN EL IF DE acceso()
//        //principal(it.result?.user?.email ?:"", ProviderType.BASIC)
//
//    }
//    //EN LA ACTIVIDAD A DONDE SERAN REDIRIGIDOS LOS DATOS
//    enum class ProviderType{
//        BASIC
//    }