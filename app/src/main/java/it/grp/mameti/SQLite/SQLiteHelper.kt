package it.grp.mameti.SQLite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context): SQLiteOpenHelper(context, "mameti.db", null, 1) {

    // DISTINTAS TABLAS CONFORMAN LA BASE DE DATOS INTERNA
    val tablaHumano1 = "CREATE TABLE IF NOT EXISTS humano1 (" +
            "_idhum INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombres TEXT NOT NULL," +
            "apellido_p TEXT NOT NULL," +
            "apellido_m TEXT NOT NULL," +
            "correo TEXT UNIQUE NOT NULL," +
            "password TEXT NOT NULL," +
            "telefono TEXT UNIQUE NOT NULL);"

    val tablaHumano2 = "CREATE TABLE IF NOT EXISTS humano2 (" +
            "num_ext TEXT," +
            "num_int TEXT," +
            "calle TEXT," +
            "colonia TEXT," +
            "ciudad TEXT," +
            "estado TEXT);"

    val tablaVeterinario = "CREATE TABLE IF NOT EXISTS veterinario (" +
            "_idvet INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nombres TEXT NOT NULL," +
            "apellido_p TEXT," +
            "apellido_m TEXT," +
            "correo TEXT UNIQUE NOT NULL," +
            "telefono TEXT UNIQUE NOT NULL," +
            "num_ext TEXT NOT NULL," +
            "num_int TEXT," +
            "calle TEXT NOT NULL," +
            "colonia TEXT NOT NULL," +
            "ciudad TEXT NOT NULL," +
            "estado TEXT NOT NULL);"

    val tablaPerro = "CREATE TABLE IF NOT EXISTS perro (" +
            "_idcan INTEGER PRIMARY KEY AUTOINCREMENT," +
            "_idhum INTEGER," +
            "nombre TEXT NOT NULL," +
            "raza TEXT NOT NULL," +
            "talla TEXT NOT NULL," +
            "edad INTEGER NOT NULL," +
            "peso REAL NOT NULL);"

    // EN LA PRIMERA INSTANCIA Y SI NO EXISTEN LAS TABLAS ENTONCES SE CREAN
    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(tablaHumano1)
        db!!.execSQL(tablaHumano2)
        db!!.execSQL(tablaVeterinario)
        db!!.execSQL(tablaPerro)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val ordenRestablecimiento1 = "DROP TABLE IF EXISTS humano1"
        val ordenRestablecimiento2 = "DROP TABLE IF EXISTS humano2"
        val ordenRestablecimiento3 = "DROP TABLE IF EXISTS veterinario"
        val ordenRestablecimiento4 = "DROP TABLE IF EXISTS perro"

        db!!.execSQL(ordenRestablecimiento1)
        db!!.execSQL(ordenRestablecimiento2)
        db!!.execSQL(ordenRestablecimiento3)
        db!!.execSQL(ordenRestablecimiento4)

        onCreate(db)
    }

    //FUNCION PARA AÑADIR UN HUMANO (PARTE 1 Y 2)
    fun crearHumano1(nombres: String, apellidop: String, apellidom: String, correo: String, tel: String, pass: String){
        val datos = ContentValues()
        datos.put("nombres",nombres)
        datos.put("apellido_p",apellidop)
        datos.put("apellido_m",apellidom)
        datos.put("correo",correo)
        datos.put("password",pass)
        datos.put("telefono",tel)

        val db = this.writableDatabase
        db.insert("humano1",null,datos)
        db.close()
    }

    fun crearHumano2(num_ext: String, num_int: String, calle: String, colonia: String, ciudad: String, estado: String){
        val datos = ContentValues()
        datos.put("num_ext", num_ext)
        datos.put("num_int", num_int)
        datos.put("calle", calle)
        datos.put("colonia", colonia)
        datos.put("ciudad", ciudad)
        datos.put("estado", estado)

        val db = this.writableDatabase
        db.insert("humano2", null, datos)
        db.close()
    }

    //FUNCION PARA AÑADIR UN VETERINARIO
    fun crearVeterinario(nombres: String, apellidop: String, apellidom: String, correo: String, tel: String,
                         num_ext: String, num_int: String, calle: String, colonia: String, ciudad: String, estado: String){
        val datos = ContentValues()
        datos.put("nombres",nombres)
        datos.put("apellido_p",apellidop)
        datos.put("apellido_m",apellidom)
        datos.put("correo",correo)
        datos.put("telefono",tel)
        datos.put("num_ext", num_ext)
        datos.put("num_int", num_int)
        datos.put("calle", calle)
        datos.put("colonia", colonia)
        datos.put("ciudad", ciudad)
        datos.put("estado", estado)

        val db = this.writableDatabase
        db.insert("veterinario", null, datos)
        db.close()
    }

    //FUNCION PARA AÑADIR UN PERRO
    fun crearPerro(nombre: String, raza: String, talla: String, edad: Int, peso: Float){
        val datos = ContentValues()
        datos.put("nombre", nombre)
        datos.put("raza", raza)
        datos.put("talla", talla)
        datos.put("edad", edad)
        datos.put("peso", peso)

        val db = this.writableDatabase
        db.insert("veterinario", null, datos)
        db.close()
    }

}