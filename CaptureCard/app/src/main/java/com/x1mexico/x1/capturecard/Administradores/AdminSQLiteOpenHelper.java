package com.x1mexico.x1.capturecard.Administradores;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;

/**
 * Created by X1 on 23/02/16.
 */
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String BASE_DATOS = "CaptureCard";
    private static final int VERSION = 1;

    private static final String nuevaBaseDatos = "create table " + AdminBaseDatos.TABLA + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, empresa, TEXT, nombre TEXT, correo TEXT, telefono1 TEXT, telefono2 TEXT, archivo TEXT)";
    private static final String actualizarBaseDatos = "drop table if exist " + AdminBaseDatos.TABLA;
    public AdminSQLiteOpenHelper(Context context) {
        super(context, BASE_DATOS, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(nuevaBaseDatos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(actualizarBaseDatos);
    }
}
