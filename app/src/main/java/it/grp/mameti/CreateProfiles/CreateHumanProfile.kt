package it.grp.mameti.CreateProfiles

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_create_human_profile.*

class CreateHumanProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_human_profile)
        supportActionBar?.hide()

        noEmpty()
    }

    //VERIFICAR DATOS VACIOS
    private fun noEmpty(){
        title = "Verificacion"
        btnRegistro.setOnClickListener(){
            if (etCorreo.text.isNotEmpty() &&
                etAuthPass1.text.isNotEmpty() &&
                etAuthPass2.text.isNotEmpty()) {
                equalsPass()
            }else{
                alerta_noEmpty()
            }
        }
    }

    //VERIFICA QUE LAS CONTRASÑEAS SEAN IGUALES
    private fun equalsPass(){
        if (etAuthPass1.text.toString() == etAuthPass2.text.toString()) {
            alertProfile()
        }else{
            alerta_noEqualsPass()
        }
    }

    private fun alertProfile(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Atención!")
        builder.setMessage("Recuerda tu e-Mail y Contraseña, ya que serán tus datos de inicio de sesión.")

        builder.apply {
            setPositiveButton(R.string.ok,
                DialogInterface.OnClickListener { dialog, btnAceptar ->
                    //SI NO ESTAN VACIOS INICIA EL REGISTRO EN FIREBASE
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        etCorreo.text.toString(),
                        etAuthPass2.text.toString()
                    ).addOnCompleteListener() {
                        if (it.isSuccessful) {
                            Toast.makeText(this@CreateHumanProfile,"¡Perfil creado con exito!", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            alerta_noAuth()
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
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("No se ha podido autenticar al usuario (contraseña muy corta).")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun alerta_noEmpty(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Rellena los campos vacios.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun alerta_noEqualsPass(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Verifica las contraseñas.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}