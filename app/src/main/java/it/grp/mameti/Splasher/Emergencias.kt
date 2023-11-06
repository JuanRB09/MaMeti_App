package it.grp.mameti.Splasher

import android.Manifest
import android.content.ActivityNotFoundException
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
import kotlinx.android.synthetic.main.activity_mascota.*
import kotlinx.android.synthetic.main.activity_usuario.*
import java.text.SimpleDateFormat
import java.util.*

var mailE = ""
var provE = ""

enum class ProviderTypeE {
    BASIC,
    GOOGLE
}

class Emergencias : AppCompatActivity() {

    var destinatario = ""
    var asunto = ""
    var contenido = ""
    var nombre_usuario = ""
    var direccion_usuario = ""
    var nombre_mascota = ""
    var mail_usuario = ""
    var telefono_usuario = ""
    var raza_mascota = ""
    var talla_mascota = ""
    var peso_mascota = ""
    var edad_mascota = ""
    var dato_cardio = ""
    var dato_temperatura = ""
    var dato_humedad = ""

    val calendar = Calendar.getInstance()
    val formatoFechaHora = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    val fechaHoraActual = formatoFechaHora.format(calendar.time)

    private val db = FirebaseFirestore.getInstance()
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergencias)

        val bundle = intent.extras
        email = bundle?.getString("email").toString()
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        guiaPerfilVet(mail, ProviderTypeU.BASIC)

        btnCallContact.setOnClickListener{ requestPermissions() }

        btnRefreshContacts.setOnClickListener{
            contactos_spn()
            Toast.makeText(this,"Lista de contactos actualizada", Toast.LENGTH_SHORT).show()
        }

        btnSendMail.setOnClickListener{

            destinatario = etContactoSecundarioM.text.toString()

            if(etContactoSecundarioM.text.toString() != ""){

                db.collection("users").document(email!!).collection("data")
                    .document("mailData").get().addOnSuccessListener { documentSnapshot ->
                        val data = documentSnapshot.data
                        if (data != null) {
                            nombre_usuario = (data["nombre"] as String?).toString()
                            direccion_usuario = (data["direccion"] as String?).toString()
                            nombre_mascota = (data["nombremascota"] as String?).toString()
                            raza_mascota = (data["razamascota"] as String?).toString()
                            talla_mascota = (data["tallamascota"] as String?).toString()
                            peso_mascota = (data["pesomascota"] as String?).toString()
                            dato_cardio = (data["cardio"] as String?).toString()
                            dato_temperatura = (data["temperatura"] as String?).toString()
                            edad_mascota = (data["edadmascota"] as String?).toString()
                            telefono_usuario = (data["telefono"] as String?).toString()
                            mail_usuario = (data["mail"] as String?).toString()
                            dato_humedad = (data["humedad"] as String?).toString()
                        }

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "text/plain"
                        intent.setPackage("com.google.android.gm")

                        asunto = "Amo: $nombre_usuario - Mascota: $nombre_mascota"
                        contenido = "Ficha de datos para solicitud de atención veterinaria:" +
                                "\nDatos del Usuario:\n" +
                                "\t- Nombre: $nombre_usuario\n" +
                                "\t- Dirección: $direccion_usuario\n" +
                                "\t- Teléfono: $telefono_usuario\n" +
                                "\t- e-Mail: $mail_usuario\n" +
                                "\nDatos de la Mascota:\n" +
                                "\t- Nombre: $nombre_mascota\n" +
                                "\t- Raza: $raza_mascota\n" +
                                "\t- Talla: $talla_mascota\n" +
                                "\t- Peso: $peso_mascota\n" +
                                "\t- Edad: $edad_mascota\n" +
                                "\nDatos del Sensor:\n" +
                                "\t- Ritmo Cardiaco: $dato_cardio\n" +
                                "\t- Temperatura del Ambiente: $dato_temperatura °C\n" +
                                "\t- Humedad del Ambiente: $dato_humedad%\n" +
                                "\t- Hora del Registro: $fechaHoraActual\n"

                        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario))
                        intent.putExtra(Intent.EXTRA_SUBJECT, asunto)
                        intent.putExtra(Intent.EXTRA_TEXT, contenido)

                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(this, "La aplicación de Gmail no está instalada", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(this,"¡No hay un correo registrado!", Toast.LENGTH_SHORT).show()
            }
        }

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

        contactos_spn()

    }

    //CARGAR LOS CONTACTOS AL SPINNER
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
            Toast.makeText(this,"¡No hay un número de teléfono!", Toast.LENGTH_SHORT).show()
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

