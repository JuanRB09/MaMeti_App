package it.grp.mameti.Splasher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_mascota.*
import kotlinx.android.synthetic.main.activity_usuario.*

var mailM = ""
var provM = ""

enum class ProviderTypeM{
    BASIC,
    GOOGLE
}

class Mascota : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        db.collection("users").document(email!!).collection("data").document("petData").get().addOnSuccessListener {
            etNombreMascota.setText(it.get("nombremascota") as String?)
            etEdad.setText(it.get("edadmascota") as String?)
            etPesoMascota.setText(it.get("pesomascota") as String?)
            etRazaMascota.setText(it.get("razamascota") as String?)
            etTalla.setText(it.get("tallamascota") as String?)
        }

        btnGuardarCambios.setOnClickListener{
            db.collection("users").document(email!!).collection("data").document("petData").set(
                hashMapOf(
                    "nombremascota" to etNombreMascota.text.toString(),
                    "edadmascota" to etEdad.text.toString(),
                    "pesomascota" to etPesoMascota.text.toString(),
                    "razamascota" to etRazaMascota.text.toString(),
                    "tallamascota" to etTalla.text.toString()
                )
            )
            Toast.makeText(applicationContext, "¡Datos Guardados!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailM = email
        provM = proveedor.toString()
    }
}