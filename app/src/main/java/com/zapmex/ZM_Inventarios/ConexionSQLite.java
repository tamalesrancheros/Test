package com.zapmex.ZM_Inventarios;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.zapmex.ZM_Inventarios.Utilidad.Utilidades;

public class ConexionSQLite extends SQLiteOpenHelper {

    //Constructor de la BD el el cual contiene las caracteristicas de la Base
    //Al llamar al constructor se crea la BD
    public ConexionSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //Encargado de generar las tablas por script correspondientes de nuestras entidades
    //Generamos Scripts
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Utilidades.CREAR_TABLA_INVTBARCODE);
        db.execSQL(Utilidades.CREAR_TABLA_INVTFIJO);
    }

    //Encargada de verificar si ya existe antes una version antigua de nuestra BD
    //Refrescamos la BD
    @Override
    public void onUpgrade(SQLiteDatabase db, int versionAntigua, int versionNueva) {
        db.execSQL("DROP TABLE IF EXISTS "+ Utilidades.TABLA_INVTBARCODE);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS "+ Utilidades.TABLA_INVTFIJO);
        onCreate(db);
    }
}
