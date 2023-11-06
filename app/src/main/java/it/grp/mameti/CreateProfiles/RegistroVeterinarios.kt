package it.grp.mameti.CreateProfiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_emergencias.*
import kotlinx.android.synthetic.main.activity_registro_veterinarios.*

var mailRC = ""
var provRC = ""

enum class ProviderTypeU{
    BASIC,
    GOOGLE
}

class RegistroVeterinarios : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_veterinarios)

        val bundle = intent.extras
        email = bundle?.getString("email").toString()
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        //GUARDAR LOS DATOS DEL CONTACTO
        btnGuardarContacto.setOnClickListener {
            if(etNombreVet.text.toString() != "" && etContactoPrincipal.text.toString() != ""
                && etContactoSecundario.text.toString() != "" && etDireccionVet.text.toString() != ""){
                db.collection("users").document(email!!).collection("data")
                    .document("contactData").collection("contacts")
                    .document(etNombreVet.text.toString()).set(
                        hashMapOf(
                            "contacto1" to etContactoPrincipal.text.toString(),
                            "contacto2" to etContactoSecundario.text.toString(),
                            "nombrecontacto" to etNombreVet.text.toString(),
                            "direccioncontacto" to etDireccionVet.text.toString()
                        )
                    )
                Toast.makeText(this,"¡Contacto creado con exito!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"¡Revisa los campos vacios!", Toast.LENGTH_SHORT).show()
            }
        }

        //EDITAR CONTACTOS
        btnEdit.setOnClickListener {
            if(etContactoEd.text.toString() != ""){
                db.collection("users").document(email!!).collection("data")
                    .document("contactData").collection("contacts")
                    .document(etContactoEd.text.toString()).set(
                        hashMapOf(
                            "contacto1" to etContactoPrincipalEd.text.toString(),
                            "contacto2" to etContactoSecundarioEd.text.toString(),
                            "nombrecontacto" to etContactoEd.text.toString(),
                            "direccioncontacto" to etDireccionVetEd.text.toString()
                        )
                    )
                Toast.makeText(this,"¡Contacto editado con exito!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"No se ha seleccionado un contacto", Toast.LENGTH_SHORT).show()
            }
        }

        //CARGAR LOS CONTACTOS AL SPINNER
        contactos_spn()

        //ELIMINAR EL CONTACTO
        btnDelete.setOnClickListener {
            if(etContactoEd.text.toString() != ""){
                val documentReference = db.collection("users")
                    .document(email!!)
                    .collection("data")
                    .document("contactData")
                    .collection("contacts")
                    .document(etContactoEd.text.toString())

                documentReference.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this,"¡Contacto eliminado con exito!", Toast.LENGTH_SHORT).show()
                        etContactoEd.setText("")
                        etContactoPrincipalEd.setText("")
                        etContactoSecundarioEd.setText("")
                        etDireccionVetEd.setText("")
                        contactos_spn()
                    }
                    .addOnFailureListener { _ ->
                        Toast.makeText(this,"¡El contacto no existe!", Toast.LENGTH_SHORT).show()
                    }
            }else{
                Toast.makeText(this,"No se ha seleccionado un contacto", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun contactos_spn(){
        db.collection("users")
            .document(email!!)
            .collection("data")
            .document("contactData")
            .collection("contacts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val documentNames = mutableListOf<String>()
                for (document in querySnapshot) {
                    val nombre = document.id //NOMBRE DEL DOCUMENTO DONDE SE GUARDAN LOS CONTACTOS
                    documentNames.add(nombre)
                }
                documentNames.add(0, "Selecciona un contacto")
                //SE CREA UN ARRAY ADAPTER Y SE CONFIGURA EL SPINNER
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, documentNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnContactosE.adapter = adapter

                spnContactosE.setSelection(0, false)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"¡No se obtuvieron contactos!", Toast.LENGTH_SHORT).show()
            }

        spnContactosE.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spnContactosE.selectedItem as String
                db.collection("users")
                    .document(email!!)
                    .collection("data")
                    .document("contactData")
                    .collection("contacts")
                    .document(selectedItem)
                    .get().addOnSuccessListener {
                        etContactoEd.setText(it.get("nombrecontacto") as String?)
                        etContactoPrincipalEd.setText(it.get("contacto1") as String?)
                        etContactoSecundarioEd.setText(it.get("contacto2") as String?)
                        etDireccionVetEd.setText(it.get("direccioncontacto") as String?)
                    }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@RegistroVeterinarios,"¡No se selecciono un contacto!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailRC = email
        provRC = proveedor.toString()
    }

}