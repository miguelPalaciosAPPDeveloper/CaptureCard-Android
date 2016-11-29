package com.x1mexico.x1.capturecard.Activitys;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;
import com.x1mexico.x1.capturecard.Administradores.AdminCapturas;
import com.x1mexico.x1.capturecard.Administradores.AdminTexto;
import com.x1mexico.x1.capturecard.Dialogs.DialogCargandoDatos;
import com.x1mexico.x1.capturecard.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ActivityPreview extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ActivityPreview.class.getSimpleName();
    public static final String RUTA_ARCHIVO = "ruta_archivo";
    private static final String LANG = "eng";
    private static final String CONTACTO = "(([a-zA-Z.ñáéíóú]{2,}+ ){2,}(.*)[a-zA-Zñáéíóú]{3,})";
    private static final String NUMERO = "((([0-9()]{2,}( |-){0,4}){2,10}){2})";
    private static final String CORREO = "([\\\\a-zA-Z0-9._%-|]+@[\\\\a-zA-Z0-9.-|]+(.|_)[\\\\a-zA-Z|]{2,4})";

    private static final long TIEMPO_LECTURA = 30000;

    private AdminCapturas mAdminCapturas;
    private AdminTexto mAdminTexto;
    private DialogCargandoDatos mDialogCargandoDatos;
    private Handler mHandler;

    private String rutaArchivo;
    private String[] arrayNombre;
    private String[] arrayCorreo;
    private String[] arrayNumero;

    private boolean SinInformacion = false;
    private boolean conInformacion = false;

    private EditText mEditTextEmpresa, mEditTextNombre, mEditTextCorreo, mEditTextTelefono1, mEditTextTelefono2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdminCapturas = new AdminCapturas();
        mAdminTexto = new AdminTexto();

        mAdminTexto.traineddata(this);

        Intent intent = getIntent();
        rutaArchivo = intent.getStringExtra(RUTA_ARCHIVO);
        Log.e(TAG, rutaArchivo);

        mHandler = new Handler();

        mEditTextEmpresa = (EditText)findViewById(R.id.editTextEmpresa);
        mEditTextNombre = (EditText)findViewById(R.id.editTextNombre);
        mEditTextCorreo = (EditText)findViewById(R.id.editTextCorreo);
        mEditTextTelefono1 = (EditText)findViewById(R.id.editTextTelefono1);
        mEditTextTelefono2 = (EditText)findViewById(R.id.editTextTelefono2);

        ImageButton mImageButtonEmpresa = (ImageButton)findViewById(R.id.imageButtonEmpresa);
        ImageButton mImageButtonNombre = (ImageButton)findViewById(R.id.imageButtonNombre);
        ImageButton mImageButtonCorreo = (ImageButton)findViewById(R.id.imageButtonCorreo);
        ImageButton mImageButtonTelefono1 = (ImageButton)findViewById(R.id.imageButtonTelefono1);
        ImageButton mImageButtonTelefono2 = (ImageButton)findViewById(R.id.imageButtonTelefono2);

        Button mButtonGuardar = (Button)findViewById(R.id.buttonGuardar);
        Button mButtonCancelar = (Button)findViewById(R.id.buttonCancelar);

        mImageButtonEmpresa.setOnClickListener(this);
        mImageButtonNombre.setOnClickListener(this);
        mImageButtonCorreo.setOnClickListener(this);
        mImageButtonTelefono1.setOnClickListener(this);
        mImageButtonTelefono2.setOnClickListener(this);

        mButtonGuardar.setOnClickListener(this);
        mButtonCancelar.setOnClickListener(this);

        Bitmap capturaRealizada = mAdminCapturas.rediMensionar(rutaArchivo, 1920, 1080);
        generarVista(capturaRealizada);
        mDialogCargandoDatos = new DialogCargandoDatos(ActivityPreview.this);
        mDialogCargandoDatos.execute();
        iniciarOCR();
    }

    public void generarVista(final Bitmap capturaRealizada){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView mImageViewCaptura = (ImageView)findViewById(R.id.imageViewPreview);
                        try{
                            mImageViewCaptura.post(new Runnable() {
                                @Override
                                public void run() {
                                    mImageViewCaptura.setImageBitmap(capturaRealizada);
                                }
                            });
                        }catch (Exception Error){
                            Log.e(TAG, Error.getMessage());
                        }
                    }
                });
            }
        }).start();
    }

    public void iniciarOCR(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap imageTesseract;
                imageTesseract = mAdminCapturas.filtrarImagen(rutaArchivo);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "inicio OCR");
                                OCR(imageTesseract);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(SinInformacion){
                                            Toast.makeText(getApplicationContext(), R.string.toast_no_se_obtuvo_informacion, Toast.LENGTH_SHORT).show();
                                        } else
                                            Toast.makeText(getApplicationContext(), R.string.toast_informacion_obtenida, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }).start();
    }

    protected void OCR(Bitmap bitmap) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(rutaArchivo);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        int salida = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        int rotacion = 0;

        switch (salida) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotacion = 90;
                Log.e(TAG, "90 grados");
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotacion = 180;
                Log.e(TAG, "180 grados");
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotacion = 270;
                Log.e(TAG, "270 grados");
                break;
        }

        if (rotacion != 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Matrix mat = new Matrix();
            mat.preRotate(rotacion);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, mat, false);
        }
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Log.e(TAG, "Width: " + bitmap.getWidth() + " " + "Heigh: " + bitmap.getHeight());
        TessBaseAPI tessapi = new TessBaseAPI();
        temporizador(tessapi);

        String rutaCarpeta = rutaArchivo.substring(0, 32);
        Log.d(TAG, rutaCarpeta);
        tessapi.init(rutaCarpeta, LANG);
        tessapi.setImage(bitmap);
        String informacion = tessapi.getUTF8Text();
        tessapi.end();
        Log.e(TAG, "fin de lectura");
        try{
            if ( informacion.length() != 0 ) {
                conseguirTexto(informacion);
            }else
                mDialogCargandoDatos.cancel(true);
        }catch (Exception Error){
            Log.e(TAG, Error.getMessage());
        }
    }

    public void conseguirTexto(String informacion){
        //ArrayList<String> Contacto, Correo, Numero;
        if (LANG.equalsIgnoreCase(LANG) ) {
            Log.e(TAG, "informacion: " + informacion);
            ArrayList<String> Contacto;
            ArrayList<String> Correo;
            ArrayList<String> Numero;

            Contacto = mAdminTexto.obtenerTexto(CONTACTO, informacion);
            Correo = mAdminTexto.obtenerTexto(CORREO, informacion);
            Numero = mAdminTexto.obtenerTexto(NUMERO, informacion);

            if(Contacto.size() > 1){
                enviarTexto(mEditTextNombre, Contacto.get(0));
                arrayNombre = new String[Contacto.size()];

                int i = 0;
                for(String nombre:Contacto){
                    arrayNombre[i] = nombre;
                    i++;
                    Log.e(TAG, "Contacto: " + nombre);
                }
            }else if(Contacto.size() > 0 && Contacto.size() < 2){
                enviarTexto(mEditTextNombre, Contacto.get(0));
            }

            if(Correo.size() > 1){
                enviarTexto(mEditTextCorreo, Correo.get(0));
                arrayCorreo = new String[Correo.size()];

                int i = 0;
                for(String correo:Correo){
                    arrayCorreo[i] = correo;
                    i++;
                    Log.e(TAG, "Correo: " + correo);
                }
            }else if(Correo.size() > 0 && Correo.size() < 2){
                enviarTexto(mEditTextCorreo, Correo.get(0));
            }

            if(Numero.size() > 2){
                enviarTexto(mEditTextTelefono1, Numero.get(0));
                enviarTexto(mEditTextTelefono2, Numero.get(1));
                arrayNumero = new String[Numero.size()];

                int i = 0;
                for(String numero:Numero){
                    arrayNumero[i] = numero;
                    i++;
                    Log.e(TAG, "Numero: " + numero);
                }
            }else if(Numero.size() > 1 && Numero.size() < 3){
                enviarTexto(mEditTextTelefono1, Numero.get(0));
                enviarTexto(mEditTextTelefono2, Numero.get(1));
            } else if(Numero.size() > 0 && Numero.size() < 2){
                enviarTexto(mEditTextTelefono1, Numero.get(0));
            }

            if(Contacto.size() <= 0 && Correo.size() <= 0 && Numero.size() <= 0){SinInformacion = true;}
        }
        conInformacion = true;
        mDialogCargandoDatos.cancel(true);
    }

    public void temporizador(final TessBaseAPI tessBaseAPI){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "tiempo terminado");
                if (!conInformacion) {
                    SinInformacion = true;
                    tessBaseAPI.stop();
                    mDialogCargandoDatos.cancel(true);
                    Toast.makeText(getApplicationContext(), R.string.toast_no_se_obtuvo_informacion, Toast.LENGTH_SHORT).show();
                }
            }
        }, TIEMPO_LECTURA);
    }

    public void enviarTexto(final EditText editText, final String texto){
        new Thread(new Runnable() {
            @Override
            public void run() {
                editText.post(new Runnable() {
                    @Override
                    public void run() {
                        editText.setText(texto);
                    }
                });
            }
        }).start();
    }
    @Override
    public void onClick(View v) {
        int idButton = v.getId();
        switch (idButton){
            case R.id.imageButtonEmpresa:
                Log.e(TAG, "empresa");
                if(arrayNombre != null){
                    if(arrayNombre.length > 1){generarDialogLista(arrayNombre, mEditTextEmpresa);}
                    else
                        Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonNombre:
                Log.e(TAG, "nombre");
                if(arrayNombre != null){
                    if(arrayNombre.length > 1){generarDialogLista(arrayNombre, mEditTextNombre);}
                    else
                        Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonCorreo:
                Log.e(TAG, "correo");
                if(arrayCorreo != null){
                    if(arrayCorreo.length > 1){generarDialogLista(arrayCorreo, mEditTextCorreo);}
                    else
                        Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonTelefono1:
                Log.e(TAG, "telefono1");
                if(arrayNumero != null) {
                    if(arrayNumero.length > 2){generarDialogLista(arrayNumero, mEditTextTelefono1);}
                    else
                        Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();

                break;
            case R.id.imageButtonTelefono2:
                Log.e(TAG, "telefono2");
                if(arrayNumero != null) {
                    if(arrayNumero.length > 2){generarDialogLista(arrayNumero, mEditTextTelefono2);}
                    else
                        Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(this, R.string.toast_no_hay_mas_datos, Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonGuardar:
                guardarTarjeta();
                Log.e(TAG, "guardar");
                break;
            case R.id.buttonCancelar:
                onBackPressed();
                Log.e(TAG, "cancelar");
                break;
        }
    }

    public void generarDialogLista(final String[] array, final EditText editText){
        final Dialog mDialogListaArchivos = new Dialog(this, R.style.Theme_Dialog_Translucent);
        mDialogListaArchivos.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogListaArchivos.setCancelable(false);
        mDialogListaArchivos.setContentView(R.layout.dialog_lista_archivos);

        ListView mListViewTextos = (ListView)mDialogListaArchivos.findViewById(R.id.listViewTextos);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_lista_archivos, array);

        mListViewTextos.setAdapter(arrayAdapter);
        mListViewTextos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, arrayAdapter.getItem(position));
                enviarTexto(editText, arrayAdapter.getItem(position));
                mDialogListaArchivos.dismiss();
            }
        });

        ((Button)mDialogListaArchivos.findViewById(R.id.buttonCancelar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogListaArchivos.dismiss();
            }
        });

        mDialogListaArchivos.show();
    }

    public void guardarTarjeta(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdminCapturas.comprimir(rutaArchivo);

                String Empresa = mEditTextEmpresa.getText().toString();
                String Nombre = mEditTextNombre.getText().toString();
                String Correo = mEditTextCorreo.getText().toString();
                String Telefono1 = mEditTextTelefono1.getText().toString();
                String Telefono2 = mEditTextTelefono2.getText().toString();

                AdminBaseDatos adminBaseDatos = new AdminBaseDatos(getApplicationContext());
                adminBaseDatos.subirTarjeta(Empresa, Nombre, Correo, Telefono1, Telefono2, rutaArchivo);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        salirPreview();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        File deleteFile = new File(rutaArchivo);
        if(deleteFile.delete()){
            Log.e(TAG, "archivo borrado");
        }

        salirPreview();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void salirPreview(){
        Intent intentMain = new Intent(this, ActivityMain.class);
        startActivity(intentMain);
        finish();
    }
}
