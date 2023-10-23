package it.grp.mameti.Splasher

//CLAVE DE API: AIzaSyC7nuTxegFOPU0f7lpjeTHXJ2BkIGFi4vg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.text.android.style.PlaceholderSpan
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.type.LatLng
import it.grp.mameti.CreateProfiles.RegistroVeterinarios
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_emergencias.*
import kotlinx.android.synthetic.main.activity_splash_main.*

var mailE = ""
var provE = ""

enum class ProviderTypeE {
    BASIC,
    GOOGLE
}

class Emergencias : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergencias)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        guiaPerfilVet(mail, ProviderTypeU.BASIC)

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

    }

    private fun setup(email: String, proveedor: String) {
        title = "Inicio"
        mailE = email
        provE = proveedor.toString()
    }

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

}

