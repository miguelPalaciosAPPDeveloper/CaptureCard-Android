package com.x1mexico.x1.capturecard.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;
import com.x1mexico.x1.capturecard.Administradores.AdminCapturas;
import com.x1mexico.x1.capturecard.Administradores.AdminTarjetas;
import com.x1mexico.x1.capturecard.Dialogs.DialogOpciones;
import com.x1mexico.x1.capturecard.R;

public class ActivityDatosTarjeta extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = ActivityDatosTarjeta.class.getSimpleName();
    private static final String EDITAR = "EDITAR";
    private static final String GUARDAR = "GUARDAR";
    private EditText mEditTextEmpresa, mEditTextNombre, mEditTextCorreo, mEditTextTelefono1, mEditTextTelefono2;
    private AdminBaseDatos mAdminBaseDatos;
    private AdminTarjetas mAdminTarjetas;
    public static final String TARJETA = "tarjeta";
    private boolean mCambios = false;
    public boolean mEditando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_tarjeta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.title_activity_activity_datos_tarjeta);

        AdminCapturas mAdminCapturas = new AdminCapturas();
        mAdminBaseDatos = new AdminBaseDatos(this);

        Intent intent = getIntent();
        mAdminTarjetas = (AdminTarjetas) intent.getExtras().getSerializable(TARJETA);

        enviarDatosTarjeta();

        ImageButton mImageButtonTelefono1 = (ImageButton)findViewById(R.id.imageButtonTelefono1);
        ImageButton mImageButtonTelefono2 = (ImageButton)findViewById(R.id.imageButtonTelefono2);
        Button mButtonOpciones = (Button)findViewById(R.id.buttonOpciones);

        mImageButtonTelefono1.setOnClickListener(this);
        mImageButtonTelefono2.setOnClickListener(this);
        mButtonOpciones.setOnClickListener(this);

        Bitmap bitmap = mAdminCapturas.rediMensionar(mAdminTarjetas.getArchivo(), 1920, 1080);
        ImageView imageViewCaptura = (ImageView)findViewById(R.id.imageViewCaptura);
        imageViewCaptura.setImageBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentMain = new Intent(this, ActivityMain.class);
        startActivity(intentMain);
        finish();
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

    public void enviarDatosTarjeta(){
        mEditTextEmpresa = (EditText)findViewById(R.id.editTextEmpresa);
        mEditTextNombre = (EditText)findViewById(R.id.editTextNombre);
        mEditTextCorreo = (EditText)findViewById(R.id.editTextCorreo);
        mEditTextTelefono1 = (EditText)findViewById(R.id.editTextTelefono1);
        mEditTextTelefono2 = (EditText)findViewById(R.id.editTextTelefono2);

        mEditTextEmpresa.setText(mAdminTarjetas.getEmpresa());
        mEditTextNombre.setText(mAdminTarjetas.getNombre());
        mEditTextCorreo.setText(mAdminTarjetas.getCorreo());
        mEditTextTelefono1.setText(mAdminTarjetas.getTelefono1());
        mEditTextTelefono2.setText(mAdminTarjetas.getTelefono2());

        textoCambiado(mEditTextEmpresa);
        textoCambiado(mEditTextNombre);
        textoCambiado(mEditTextCorreo);
        textoCambiado(mEditTextTelefono1);
        textoCambiado(mEditTextTelefono2);
    }

    public void habilitarEdicion(){
        mEditTextEmpresa.setEnabled(true);
        mEditTextNombre.setEnabled(true);
        mEditTextCorreo.setEnabled(true);
        mEditTextTelefono1.setEnabled(true);
        mEditTextTelefono2.setEnabled(true);
    }

    private void textoCambiado(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(TAG, "after");
                mCambios = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        int idButton = v.getId();
        switch (idButton){
            case R.id.imageButtonTelefono1:
                llamarNumero(mEditTextTelefono1.getText().toString());
                break;
            case R.id.imageButtonTelefono2:
                llamarNumero(mEditTextTelefono2.getText().toString());
                break;
            case R.id.buttonOpciones:
                if(mEditando){
                    opciones(GUARDAR);
                }else {
                    opciones(EDITAR);
                }
                break;
        }
    }

    private void opciones(String button){
        String id = mAdminTarjetas.getID();
        String empresa = mEditTextEmpresa.getText().toString();
        String nombre = mEditTextNombre.getText().toString();
        String correo = mEditTextCorreo.getText().toString();
        String telefono1 = mEditTextTelefono1.getText().toString();
        String telefono2 = mEditTextTelefono2.getText().toString();
        String archivo = mAdminTarjetas.getArchivo();

        AdminTarjetas nuevosDatos = new AdminTarjetas(id, empresa, nombre, correo, telefono1, telefono2, archivo);

        new DialogOpciones(this).crearDialog(nuevosDatos, button, mCambios);
    }

    private void llamarNumero(String telefono){
        if(!telefono.isEmpty()){
            String expresionRegular = "[^0-9]+";
            String telefonoFiltrado = telefono.replaceAll(expresionRegular, "");
            Log.e(TAG, "Telefono: " + telefonoFiltrado);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + telefonoFiltrado));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }else
            Toast.makeText(this, R.string.toast_no_hay_numero, Toast.LENGTH_SHORT).show();
    }
}
