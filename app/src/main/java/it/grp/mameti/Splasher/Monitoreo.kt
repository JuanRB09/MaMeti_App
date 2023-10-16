package it.grp.mameti.Splasher

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import it.grp.mameti.R
import kotlinx.android.synthetic.main.activity_monitoreo.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

val id = 1
var temperatura:Float = 0.0f
var humedad:Float = 0.0f
var cardio = 0
var temp:Float = 0.0f
var hume = 0
var card = 0

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

class Monitoreo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitoreo)

        try {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    actualizaDatos()
                }
            }, 0, 1500)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //METODO PERSISTENTE PARA ACTUALIZAR LOS DATOS
    private fun actualizaDatos() {
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
                if(card>45 && card<65){
                    TXTConsejoCardio.setText(consejoCard[7]);
                    IMGCardio.setImageResource(R.drawable.cardio_medio);
                }else if(card>65 && card<95){
                    TXTConsejoCardio.setText(consejoCard[1]);
                    IMGCardio.setImageResource(R.drawable.cardio_bajo);
                }else if(card>95 && card<110){
                    TXTConsejoCardio.setText(consejoCard[1]);
                    IMGCardio.setImageResource(R.drawable.cardio_bajo);
                }else if(card>120 && card<125){
                    TXTConsejoCardio.setText(consejoCard[2]);
                    IMGCardio.setImageResource(R.drawable.cardio_bajo);
                }else if(card>125 && card<130){
                    TXTConsejoCardio.setText(consejoCard[3]);
                    IMGCardio.setImageResource(R.drawable.cardio_medio);
                }else if(card>130 && card <140){
                    TXTConsejoCardio.setText(consejoCard[4]);
                    IMGCardio.setImageResource(R.drawable.cardio_medio);
                }else if(card>140){
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