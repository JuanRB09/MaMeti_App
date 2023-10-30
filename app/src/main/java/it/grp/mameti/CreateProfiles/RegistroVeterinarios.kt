package it.grp.mameti.CreateProfiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_registro_veterinarios.*

var mailRC = ""
var provRC = ""

enum class ProviderTypeU{
    BASIC,
    GOOGLE
}

class RegistroVeterinarios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_veterinarios)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        //GUARDAR LOS DATOS DEL CONTACTO
        btnGuardarContacto.setOnClickListener {
            db.collection("users").document(email!!).collection("data").document("contactData").collection("contacts").document(etNombreVet.text.toString()).set(
                hashMapOf(
                    "contacto1" to etContactoPrincipal.text.toString(),
                    "contacto2" to etContactoSecundario.text.toString(),
                    "nombrecontacto" to etNombreVet.text.toString(),
                    "direccioncontacto" to etDireccionVet.text.toString()
                )
            )
            Toast.makeText(this,"¡Contacto creado con exito!", Toast.LENGTH_SHORT).show()
        }

        //ELIMINAR EL CONTACTO
        btnEliminarContacto.setOnClickListener {
            val documentReference = db.collection("users")
                .document(email!!)
                .collection("data")
                .document("contactData")
                .collection("contacts")
                .document(etContactoElim.text.toString())

            documentReference.delete()
                .addOnSuccessListener {
                   Toast.makeText(this,"¡Contacto eliminado con exito!", Toast.LENGTH_SHORT).show()
                   etContactoElim.setText("")
                }
                .addOnFailureListener { _ ->
                   Toast.makeText(this,"¡El contacto no existe!", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailRC = email
        provRC = proveedor.toString()
    }

}