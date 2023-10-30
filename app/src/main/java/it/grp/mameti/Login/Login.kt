package it.grp.mameti.Login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import it.grp.mameti.CreateProfiles.CreateHumanProfile
import it.grp.mameti.R
import it.grp.mameti.Splasher.ProviderType
import kotlinx.android.synthetic.main.activity_login.*
import it.grp.mameti.Splasher.SplashMain

class Login : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        //EVENTOS DE GOOGLE ANALYTICS
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración de Firebase completa")
        analytics.logEvent("InitScreen",bundle)

        acceso()
        session()
    }

    //AUTENTICACION CON FIREBASE (CORREO)
    private fun acceso() {
        title = "Autenticación"

        btLogin.setOnClickListener{
            if(etUsuario.text.isNotEmpty() && etContrasena.text.isNotBlank()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etUsuario.text.toString(),
                    etContrasena.text.toString()).addOnCompleteListener(){
                    if (it.isSuccessful){
                        principal(it.result?.user?.email ?:"", ProviderType.BASIC)
                        //startActivity(Intent(this, Home::class.java))
                        finish()
                    }else{
                        alerta_noAuth()
                    }
                }
            }else{
                alerta_noData()
            }
        }

        btnLoginGmail.setOnClickListener{
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        //btnLoginFB

    }
    //METODOS COMPLEMENTO: MUESTRA DE ALERTAS
    private fun alerta_noAuth(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("No se ha podido autenticar al usuario.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun alerta_noData(){
        val builder = AlertDialog.Builder(this, R.style.AlertDialog)
        builder.setTitle("¡Error!")
        builder.setMessage("Complete ambos campos.")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //NAVEGAR A LAS DIFERENTES PANTALLAS
    fun createAccount(view: View) {
        startActivity(Intent(this, CreateHumanProfile::class.java))
    }
    private fun principal(email: String, proveedor: ProviderType) {
        val homeIntent = Intent(this, SplashMain::class.java).apply {
            putExtra("email", email)
            putExtra("proveedor", proveedor.name)
        }
        startActivity(homeIntent)
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val proveedor = prefs.getString("proveedor", null)

        if (email != null && proveedor != null){
            principal(email, ProviderType.valueOf(proveedor))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(){
                        if (it.isSuccessful){
                            principal(account.email ?: "", ProviderType.GOOGLE)
                            //startActivity(Intent(this, Home::class.java))
                            finish()
                        }else{
                            alerta_noAuth()
                        }
                    }
                }
            }catch (e: ApiException){
                alerta_noAuth()
            }

        }
    }

}