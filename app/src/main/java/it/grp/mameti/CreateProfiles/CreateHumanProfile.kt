package it.grp.mameti.CreateProfiles

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_create_human_profile.*

class CreateHumanProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_human_profile)
    }

    private fun alertProfile(){
        val builder = AlertDialog.Builder(this, R.style.SplashTheme)
        builder.setTitle("¡Atención!")
        builder.setMessage("Recuerda tu e-Mail y Contraseña, ya que serán tus datos de inicio" +
                "de sesión.")
        builder.apply {
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, btnAceptar ->
                    btnRegistro.setOnClickListener{
                        if(etCorreo.text.isNotEmpty() && etAuthPass2.text.isNotEmpty()){
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(etCorreo.text.toString(),
                                etAuthPass2.text.toString()).addOnCompleteListener(){
                                if (it.isSuccessful){
                                    finish()
                                }else{
                                    alerta_noAuth()
                                }
                            }
                        }
                    }
                })
            setNegativeButton(R.string.cancel,
                DialogInterface.OnClickListener { dialog, btnCancelar ->
                    dialog.dismiss()
                })

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun alerta_noAuth(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡Error!")
        builder.setMessage("No se ha podido autenticar al usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}