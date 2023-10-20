package it.grp.mameti.Splasher

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_mascota.*
import kotlinx.android.synthetic.main.activity_usuario.*
import java.io.ByteArrayOutputStream

var mailM = ""
var provM = ""
var base64String: String? = ""

enum class ProviderTypeM{
    BASIC,
    GOOGLE
}

class Mascota : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        //CARGAR LOS DATOS A LA VISTA
        db.collection("users").document(email!!).collection("data").document("petData").get().addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            if (data != null) {
                etNombreMascota.setText(data["nombremascota"] as String?)
                etEdad.setText(data["edadmascota"] as String?)
                etPesoMascota.setText(data["pesomascota"] as String?)
                etRazaMascota.setText(data["razamascota"] as String?)
                etTalla.setText(data["tallamascota"] as String?)
                base64String = data["mapaimagenmascota"] as String?
                if(base64String != null && base64String!!.isNotEmpty()){
                    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    imageView.setImageBitmap(bitmap)
                }
            }
        }

        //BOTON PARA ALMACENAR LOS DATOS DE LA MASCOTA
        btnGuardarCambios.setOnClickListener{
            val talla = etTalla.text.toString()
            val opcionesTalla = arrayOf("Mini", "Chica", "Mediana", "Grande", "Gigante")
            if (opcionesTalla.contains(talla)) {
                db.collection("users").document(email!!).collection("data").document("petData").set(
                    hashMapOf(
                        "nombremascota" to etNombreMascota.text.toString(),
                        "edadmascota" to etEdad.text.toString(),
                        "pesomascota" to etPesoMascota.text.toString(),
                        "razamascota" to etRazaMascota.text.toString(),
                        "tallamascota" to etTalla.text.toString(),
                        "mapaimagenmascota" to base64String.toString()
                    )
                )
                Toast.makeText(applicationContext, "¡Datos Guardados!", Toast.LENGTH_SHORT).show()
            }else{
                alertaTalla()
            }
        }

        //BOTON PARA SELECCIONAR UNA IMAGEN
        btnGuardarImagen.setOnClickListener {
            abrirGaleria()
        }

    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailM = email
        provM = proveedor.toString()
    }

    private fun alertaTalla(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("La talla de la mascota solo puede ser:" +
                "\n- Mini" +
                "\n- Chica" +
                "\n- Mediana" +
                "\n- Grande" +
                "\n- Gigante")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //MANEJO DE LA IMAGEN SELECCIONADA PARA LA MASCOTA
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            if (selectedImageUri != null) {
                //ENVIAR LA IMAGEN SELECCIONADA AL IMAGEVIEW
                imageView.setImageURI(selectedImageUri)
                //CONVERTIR LA IMAGEN A BASE64
                try {
                    val inputStream = contentResolver.openInputStream(selectedImageUri)
                    val bytes = ByteArrayOutputStream()
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (inputStream?.read(buffer).also { bytesRead = it!! } != -1) {
                        bytes.write(buffer, 0, bytesRead)
                    }

                    val imageBytes = bytes.toByteArray()
                    base64String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}