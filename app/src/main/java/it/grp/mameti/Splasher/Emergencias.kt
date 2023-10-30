package it.grp.mameti.Splasher

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import it.grp.mameti.CreateProfiles.RegistroVeterinarios
import it.grp.mameti.R
import it.grp.mameti.databinding.ActivityEmergenciasBinding
import kotlinx.android.synthetic.main.activity_emergencias.*
import kotlinx.android.synthetic.main.activity_usuario.*

var mailE = ""
var provE = ""

enum class ProviderTypeE {
    BASIC,
    GOOGLE
}

class Emergencias : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergencias)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        guiaPerfilVet(mail, ProviderTypeU.BASIC)

        btnCallContact.setOnClickListener{ requestPermissions() }

        btnVetCercanos.setOnClickListener {
            val busqueda = "Veterinarios"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$busqueda"))
            intent.setPackage("com.google.android.apps.maps")
            if(intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            }else{
                alerta_noGoogleMaps()
            }
        }

        db.collection("users")
            .document(email!!)
            .collection("data")
            .document("contactData")
            .collection("contacts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val documentNames = mutableListOf<String>()
                for (document in querySnapshot) {
                    val nombre = document.id // Nombre del documento
                    documentNames.add(nombre)
                }
                documentNames.add(0, "Selecciona un contacto")
                // Crea un ArrayAdapter con los nombres y configura el Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, documentNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spnContactos.adapter = adapter

                spnContactos.setSelection(0, false)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this,"¡No se obtuvieron contactos!", Toast.LENGTH_SHORT).show()
            }

        spnContactos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spnContactos.selectedItem as String
                db.collection("users")
                    .document(email!!)
                    .collection("data")
                    .document("contactData")
                    .collection("contacts")
                    .document(selectedItem)
                    .get().addOnSuccessListener {
                        tvNombreContacto.text = it.get("nombrecontacto") as String?
                        etContactoPrincipalM.setText(it.get("contacto1") as String?)
                        etContactoSecundarioM.setText(it.get("contacto2") as String?)
                        etDireccionVetM.setText(it.get("direccioncontacto") as String?)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(this@Emergencias,"¡No se selecciono un contacto!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //PARTE DE LA LLAMADA
    private fun requestPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            when{
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    call()
                }else -> requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE)

            }
        }else{
            call()
        }
    }
    private fun call() {
        val numTel = etContactoPrincipalM.text.toString()
        if(numTel != "")
            startActivity(Intent(Intent.ACTION_CALL,Uri.parse("tel:$numTel")))
        else
            Toast.makeText(this,"¡No hay un numero de telefono!", Toast.LENGTH_SHORT).show()
    }
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted ->
        if(isGranted){
            call()
        }else{
            alerta_noAppCall()
        }
    }

    private fun setup(email: String, proveedor: String) {
        title = "Inicio"
        mailE = email
        provE = proveedor.toString()
    }

    //GUIA A OTRAS PANTALLAS
    private fun guiaPerfilVet(email: String, proveedor: ProviderTypeU){
        btnRegistrarVeterinarios.setOnClickListener {
            startActivity(Intent(this, RegistroVeterinarios::class.java).apply {
                putExtra("email", email)
                putExtra("proveedor", proveedor.name)
            })
        }
    }

    private fun alerta_noGoogleMaps(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Verifica que Google Maps este instalado.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun alerta_noAppCall(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Ha ocurrido un error inesperado, intentar por otro medio.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}

