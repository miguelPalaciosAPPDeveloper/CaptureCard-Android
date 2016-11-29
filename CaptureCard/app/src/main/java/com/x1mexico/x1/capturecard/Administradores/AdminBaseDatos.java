package com.x1mexico.x1.capturecard.Administradores;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by X1 on 23/02/16.
 */
public class AdminBaseDatos {
    private static final String TAG = AdminBaseDatos.class.getSimpleName();
    public static final String TABLA = "Tarjetas";
    private static final String EMPRESA = "empresa";
    private static final String NOMBRE = "nombre";
    private static final String CORREO = "correo";
    private static final String TELEFONO1 = "telefono1";
    private static final String TELEFONO2 = "telefono2";
    private static final String ARCHIVO = "archivo";

    private static int mBorrado = 1;
    private static int mCambiado = 1;

    private AdminSQLiteOpenHelper mAdmin;
    private SQLiteDatabase mDatabase;

    public AdminBaseDatos(Context context){
        mAdmin = new AdminSQLiteOpenHelper(context);
    }

    public ArrayList<String> consultaIdTarjetas(){
        try {

            mDatabase = mAdmin.getWritableDatabase();
            Cursor mFilas = mDatabase.rawQuery("select id from " + TABLA, null);
            ArrayList<String> mDatos = new ArrayList<String>();
            mDatos.clear();
            if (mFilas.moveToFirst()) {
                mDatos.add(mFilas.getString(0));
                while (mFilas.moveToNext()) {
                    mDatos.add(mFilas.getString(0));
                }
            } else {
                mDatos = null;
                mDatabase.close();
            }

            return mDatos;
        } catch (Exception Error){return null;}
    }

    public AdminTarjetas consultarTarjeta(String id){
        try {

            mDatabase = mAdmin.getWritableDatabase();
            Cursor mFilas = mDatabase.rawQuery("select id, empresa, nombre, correo, telefono1, telefono2, archivo from " + TABLA +" where id=" + id, null);
            AdminTarjetas mDatos;
            if (mFilas.moveToFirst()) {
                mDatos = new AdminTarjetas(mFilas.getString(0), mFilas.getString(1), mFilas.getString(2), mFilas.getString(3), mFilas.getString(4), mFilas.getString(5), mFilas.getString(6));
            } else {
                mDatos = null;
                mDatabase.close();
            }
            return mDatos;
        } catch (Exception Error){return null;}
    }

    public boolean subirTarjeta(String empresa, String nombre, String correo, String telefono1, String telefono2, String archivo)
    {
        try {
            mDatabase = mAdmin.getWritableDatabase();
            ContentValues mRegistro = new ContentValues();
            mRegistro.put(EMPRESA, empresa);
            mRegistro.put(NOMBRE, nombre);
            mRegistro.put(CORREO, correo);
            mRegistro.put(TELEFONO1, telefono1);
            mRegistro.put(TELEFONO2, telefono2);
            mRegistro.put(ARCHIVO, archivo);
            mDatabase.insert(TABLA, null, mRegistro);
            mDatabase.close();

            return true;
        }
        catch (Exception Error) {
            return false;
        }
    }

    public boolean borrarTarjeta(String id, String nombre, String archivo){
        try{
            mDatabase = mAdmin.getWritableDatabase();
            int mBorrando = mDatabase.delete(TABLA, "id=" + id + " AND nombre='" + nombre + "'", null);
            mDatabase.close();

            File deleteFile = new File(archivo);
            if(deleteFile.delete()){Log.e(TAG, "archivo borrado");}

            if(mBorrando == mBorrado){
                return true;
            }else
                return false;
        }catch (Exception Error){
            return false;
        }
    }

    public boolean modificarTarjeta(String id, String empresa, String nombre, String correo, String telefono1, String telefono2){
        try{
            mDatabase = mAdmin.getWritableDatabase();
            ContentValues mCambios = new ContentValues();
            mCambios.put(EMPRESA, empresa);
            mCambios.put(NOMBRE, nombre);
            mCambios.put(CORREO, correo);
            mCambios.put(TELEFONO1, telefono1);
            mCambios.put(TELEFONO2, telefono2);
            int mCambiando = mDatabase.update(TABLA, mCambios, "id=" + id, null);
            mDatabase.close();

            if(mCambiando == mCambiado){
                return true;
            }else
                return false;
        }catch (Exception Error){
            Log.e(TAG, Error.getMessage());
            return false;
        }
    }
}
