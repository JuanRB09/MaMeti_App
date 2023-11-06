package it.grp.mameti.Splasher

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_usuario.*

var mailU = ""
var provU = ""

enum class ProviderTypeU{
    BASIC,
    GOOGLE
}

class Usuario : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        db.collection("users").document(email!!).collection("data").document("userData").get().addOnSuccessListener {
            etNombres.setText(it.get("nombre") as String?)
            etApellidoPaterno.setText(it.get("apellidop") as String?)
            etApellidoMaterno.setText(it.get("apellidom") as String?)
            etCorreo.setText(it.get("mail") as String?)
            etTelefono.setText(it.get("telefono") as String?)
            etNumExt.setText(it.get("numext") as String?)
            etNumInt.setText(it.get("numint") as String?)
            etCalle.setText(it.get("calle") as String?)
            etColonia.setText(it.get("colonia") as String?)
            etCiudad.setText(it.get("ciudad") as String?)
            etEstado.setText(it.get("estado") as String?)
        }

        //GUARDANDO LOS DATOS DEL USUARIO EN LA DB
        btnGuardar.setOnClickListener {
            //DOCUMENTO UNICO DE USUARIO
            db.collection("users").document(email!!).collection("data")
                .document("userData").set(
                    hashMapOf(
                        "nombre" to etNombres.text.toString(),
                        "apellidop" to etApellidoPaterno.text.toString(),
                        "apellidom" to etApellidoMaterno.text.toString(),
                        "mail" to etCorreo.text.toString(),
                        "telefono" to etTelefono.text.toString(),
                        "numext" to etNumExt.text.toString(),
                        "numint" to etNumInt.text.toString(),
                        "calle" to etCalle.text.toString(),
                        "colonia" to etColonia.text.toString(),
                        "ciudad" to etCiudad.text.toString(),
                        "estado" to etEstado.text.toString()
                    )
            )
            //DOCUMENTO GLOBAL DE MAIL
            db.collection("users").document(email!!).collection("data")
                .document("mailData").set(
                    hashMapOf(
                        "nombre" to (etNombres.text.toString()+" "+
                                etApellidoPaterno.text.toString()+" "+
                                etApellidoMaterno.text.toString()),
                        "mail" to etCorreo.text.toString(),
                        "telefono" to etTelefono.text.toString(),
                        "direccion" to (etNumInt.text.toString() +", "+
                                etNumExt.text.toString() +", "+
                                etCalle.text.toString() +", "+
                                etColonia.text.toString() +", "+
                                etCiudad.text.toString() +", "+
                                etEstado.text.toString())
                    ), SetOptions.merge()
                )
            Toast.makeText(applicationContext, "¡Datos Guardados!", Toast.LENGTH_SHORT).show()
        }

        //CARGANDO LOS DATOS DEL USUARIO
        btnRefresh.setOnClickListener {
            db.collection("users").document(email!!).collection("data")
                .document("userData").get().addOnSuccessListener {
                etNombres.setText(it.get("nombre") as String?)
                etApellidoPaterno.setText(it.get("apellidop") as String?)
                etApellidoMaterno.setText(it.get("apellidom") as String?)
                etCorreo.setText(it.get("mail") as String?)
                etTelefono.setText(it.get("telefono") as String?)
                etNumExt.setText(it.get("numext") as String?)
                etNumInt.setText(it.get("numint") as String?)
                etCalle.setText(it.get("calle") as String?)
                etColonia.setText(it.get("colonia") as String?)
                etCiudad.setText(it.get("ciudad") as String?)
                etEstado.setText(it.get("estado") as String?)
            }
            Toast.makeText(applicationContext, "¡Datos Cargados!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailU = email
        provU = proveedor.toString()
    }

}