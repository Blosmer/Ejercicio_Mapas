package com.example.mac.mismapas1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LugaresSQLiteHelper extends SQLiteOpenHelper {
    String sqlCreate = "CREATE TABLE Lugares (_id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT, direccion TEXT, latitud DOUBLE, longitud DOUBLE)";

    public LugaresSQLiteHelper(Context contexto, String nombre,
                               SQLiteDatabase.CursorFactory factory, int version) {
        super(contexto, nombre, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Se ejecuta la sentencia SQL de creacion de la tabla
        db.execSQL(sqlCreate);

        String nombre = "Ruta 66";
        String direccion = "Calle Mayor 50";
        double latitud = 38.992650;
        double longitud = -1.853078;

        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, direccion, latitud, longitud) " +
                "VALUES ('" + nombre + "', '"+direccion+"', " + latitud + ", " + longitud + " )");

        nombre = "Heartbreak";
        direccion = "Calle Del Tinte 35";
        latitud = 38.992458;
        longitud = -1.853758;

        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, direccion, latitud, longitud) " +
                "VALUES ('" + nombre + "', '"+direccion+"', " + latitud + ", " + longitud + " )");


        nombre = "Bigote Blanco";
        direccion = "Calle Marqués de Villores 25";
        latitud = 38.990974;
        longitud = -1.857540;

        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, direccion, latitud, longitud) " +
                "VALUES ('" + nombre + "', '"+direccion+"', " + latitud + ", " + longitud + " )");

        nombre = "Siroco";
        direccion = "Calle Concepcion 31";
        latitud = 38.993167;
        longitud = -1.852725;

        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, direccion, latitud, longitud) " +
                "VALUES ('" + nombre + "', '"+direccion+"', " + latitud + ", " + longitud + " )");

        nombre = "St Patricks";
        direccion = "Plaza Periodista Antonio Andújar 1";
        latitud = 38.996413;
        longitud = -1.858173;

        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, direccion, latitud, longitud) " +
                "VALUES ('" + nombre + "', '"+direccion+"', " + latitud + ", " + longitud + " )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Se elimina la version anterior de la tabla
        db.execSQL("DROP TABLE IF EXISTS Lugares");

        //Se crea la nueva version de la tabla
        db.execSQL(sqlCreate);
    }

    public void insertarLugar(SQLiteDatabase db, String nombre, double latitud, double longitud){
        //Insertamos los datos en la tabla Lugares
        db.execSQL("INSERT INTO Lugares (nombre, latitud, longitud) " +
                "VALUES ('" + nombre + "', " + latitud + ", " + longitud + " )");
    }
}
