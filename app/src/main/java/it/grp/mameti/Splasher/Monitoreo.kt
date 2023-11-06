package it.grp.mameti.Splasher

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_mascota.*
import kotlinx.android.synthetic.main.activity_monitoreo.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.random.Random

private val db = FirebaseFirestore.getInstance()
var mailMon = ""
var provMon = ""
var email = ""

val id = 1
var temperatura:Float = 0.0f
var humedad:Float = 0.0f
var cardio = 0
var temp:Float = 0.0f
var hume = 0
var card = 0
//VARIABLES POR DEFECTO PARA EL MONITOREO
var cardMax = 0
var cardMin = 0
var humedad_establecida = 0f
var temperatura_establecida = 0f
var talla:String? = ""
var advertencia_valores = ""
/*LOS VALORES DEL ARRAY PARA EL RITMO CARDIACO VAN DE PAR
    - POS 0 Y 1: TALLA CHICA, POS 2 Y 3: TALLA MEDIANA, POS 4 Y 5: TALLA GRANDE*/
var valores_par:IntArray = intArrayOf(90, 130, 70, 120, 60, 100)

var requestQueue: RequestQueue? = null
private val handler = Handler()
val timer = Timer()

var consejoCard = arrayOf(
    "¡LA INFORMACIÓN SE ESTA CARGANDO!\nRECUERDA: EL NO ES UNA MASCOTA, ES TU AMIGO ❤",
    "SU RITMO CARDIACO ES NORMAL, SIGUE JUGANDO CON EL",
    "TU MASCOTA SE ENCUENTRA UN POCO ALTERADA DEBERÍAS DISTRAERLA, JUEGA CON ELLA, HABLA CON ELLA",
    "TU MASCOTA ESTA ALTERADA, REFÚGIALA EN UN LUGAR SEGURO Y LEJOS DE RUIDOS FUERTES",
    "CON CUIDADO TU MASCOTA ESTA TENIENDO UNA CRISIS DE MIEDO, AJUSTA BIEN EL CHALECO",
    "TU MASCOTA ESTÁ CERCA DEL COLAPSO, BUSCA AYUDA O COMUNÍCATE CON TU VETERINARIO(A) DE CONFIANZA",
    "¡TU MASCOTA NO CONOCE EL MIEDO, ESTA BIEN!",
    "EL RITMO CARDIACO ES BAJO:\nLLEVALO AL VETERINARIO INMEDIATAMENTE"
)

var consejoTemp = arrayOf(
    "¡LA INFORMACIÓN SE ESTA CARGANDO!\nRECUERDA: EL NO ES UNA MASCOTA, ES TU AMIGO ❤",
    "LA TEMPERATURA AMBIENTE ES NORMAL:\nTU MASCOTA ESTA CÓMODA",
    "LA TEMPERATURA AMBIENTE ES BAJA:\nDEBERÍAS ABRIGARLO O LLEVARLO A UN LUGAR CÁLIDO",
    "LA TEMPERATURA AMBIENTE ES ALTA:\nDALE AGUA Y LLÉVALO A UN LUGAR FRESCO, EVITA LA ACTIVIDAD FISICA INTENSA",
    "LA HÚMEDAD ES MUY ALTA:\nDEBERÍAS QUITARLE EL CHALECO, VENTILA EL ÁREA Y EVITA LA ACTIVIDAD FISICA INTENSA",
    "LA HÚMEDAD ES BAJA:\nDALE UN BAÑO Y UN CEPILLADO, PUEDES USAR UN HUMIFICADOR PARA GENERAR CALOR",
    "LA HÚMEDAD ESTA BIEN, NO HAY DE QUE PREOCUPARSE"
)

enum class ProviderTypeMon{
    BASIC,
    GOOGLE
}

class Monitoreo : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoreo)

        val bundle = intent.extras
        email = bundle?.getString("email").toString()
        val prov = bundle?.getString("proveedor")
        setup(email ?:"", prov ?: "")

        //CARGAR LA TALLA DE LA MASCOTA EN UNA VARIABLE
        db.collection("users").document(email!!).collection("data").document("petData").get()
            .addOnSuccessListener { documentSnapshot ->
            val data = documentSnapshot.data
            if (data != null)
                talla = data["tallamascota"] as String?

            if (talla != ""){
                if (talla == "Chica"){
                    cardMin = valores_par[0]
                    cardMax = valores_par[1]
                }else if(talla == "Mediana"){
                    cardMin = valores_par[2]
                    cardMax = valores_par[3]
                }else if(talla == "Grande") {
                    cardMin = valores_par[4]
                    cardMax = valores_par[5]
                }
                tvValoresTalla.text = "Talla de Mascota: $talla\n" +
                        "Mínimo de Cardio: $cardMin - Máximo de Cardio: $cardMax"
                etT1.setText(cardMin.toString())
                etT2.setText(cardMax.toString())
            }
        }

        handler.postDelayed({

            if (etT1.text.toString() != ""){
                cardMin = (etT1.text.toString()).toInt()
                cardMax = (etT2.text.toString()).toInt()
                //ITERATIVIDAD PARA CARGAR LOS DATOS
                try {
                    timer.scheduleAtFixedRate(object : TimerTask() {
                        override fun run() {
                            actualizaDatos(cardMin, cardMax)
                        }
                    }, 0, 1500)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }, 1500)



    }

    override fun onBackPressed() {
        db.collection("users").document(email!!).collection("data")
            .document("mailData").set(
                hashMapOf(
                    "cardio" to TXTDatoCardio.text.toString(),
                    "temperatura" to TXTDatoTemperatura.text.toString(),
                    "humedad" to TXTdatoH.text.toString()
                ), SetOptions.merge()
            )
        super.onBackPressed()
    }

    private fun setup(email:String, proveedor:String) {
        title = "Inicio"
        mailMon = email
        provMon = proveedor.toString()
    }

    //METODO PERSISTENTE PARA ACTUALIZAR LOS DATOS
    private fun actualizaDatos(cardMin: Int, cardMax: Int) {

        val runnable = object : Runnable{
            override fun run() {
                consultaBD()
                //LLEVAR LOS DATOS NUMERICOS
                TXTDatoCardio.text = card.toString()
                TXTDatoTemperatura.text = temp.toString()
                TXTdatoH.text = hume.toString()

                //LLEVAR LOS CONSEJOS A SUS CASILLAS
                if(card == 0 && hume == 0 && temp == 0.0f) {
                    TXTConsejoCardio.setText(consejoCard[0])
                    TXTConsejoTemperatura.setText(consejoTemp[0])
                }

                //CONDICIONALES PARA CARDIO
                if(card>45 && card<cardMin){
                    TXTConsejoCardio.setText(consejoCard[7]);
                    IMGCardio.setImageResource(R.drawable.cardio_medio);
                }else if(card>cardMin && card<cardMax){
                    TXTConsejoCardio.setText(consejoCard[1]);
                    IMGCardio.setImageResource(R.drawable.cardio_bajo);
                }else if(card>cardMax && card<cardMax+25){
                    TXTConsejoCardio.setText(consejoCard[3]);
                    IMGCardio.setImageResource(R.drawable.cardio_medio);
                }else if(card>cardMax+26){
                    TXTConsejoCardio.setText(consejoCard[5]);
                    IMGCardio.setImageResource(R.drawable.cardio_alto);
                }

                //CONDICIONALES PARA TEMPERATURA
                if(temp>1 && temp<28){
                    TXTConsejoTemperatura.setText(consejoTemp[2]);
                    IMGTemperatura.setImageResource(R.drawable.temp_baja);
                }else if(temp>28 && temp<34){
                    TXTConsejoTemperatura.setText(consejoTemp[1]);
                    IMGTemperatura.setImageResource(R.drawable.temp_normal);
                }else if(temp>34){
                    TXTConsejoTemperatura.setText(consejoTemp[3]);
                    IMGTemperatura.setImageResource(R.drawable.temp_normal);
                }

                handler.postDelayed(this,1500)
            }
        }
        handler.postDelayed(runnable,1500)
    }

    //CONEXIÓN A LA BASE DE DATOS
    private fun consultaBD() {
        val URL1 = "https://19590323.tecsanjuan.com/MaMeti_Data/consulta.php?id=$id"
        val jsonArrayRequest = JsonArrayRequest(URL1, Listener<JSONArray?>() {
            fun onResponse(response: JSONArray) {
                var jsonObject: JSONObject? = null
                for (x in 0 until response.length()) {
                    try {
                        jsonObject = response.getJSONObject(x)
                        temperatura = jsonObject.getString("temperatura").toFloat()
                        humedad = jsonObject.getString("humedad").toFloat()
                        cardio = jsonObject.getString("frec_card").toInt()
                        card = cardio
                        temp = temperatura
                        hume = humedad.toInt()
                    } catch (e: JSONException) {
                        Toast.makeText(this@Monitoreo, "Revise los datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            onResponse(it)
        }, ErrorListener() {
            fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@Monitoreo, "Revise los datos", Toast.LENGTH_SHORT).show()
            }
        })
        requestQueue = Volley.newRequestQueue(this)
        requestQueue!!.add(jsonArrayRequest)
    }

}