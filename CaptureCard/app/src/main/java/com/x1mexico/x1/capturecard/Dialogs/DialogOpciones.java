package com.x1mexico.x1.capturecard.Dialogs;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.x1mexico.x1.capturecard.Activitys.ActivityDatosTarjeta;
import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;
import com.x1mexico.x1.capturecard.Administradores.AdminTarjetas;
import com.x1mexico.x1.capturecard.R;

/**
 * Created by X1 on 12/04/16.
 */
public class DialogOpciones {
    private static final String TAG = DialogOpciones.class.getSimpleName();
    private static final String EDITAR = "EDITAR";
    private static final String GUARDAR = "GUARDAR";
    private Dialog mDialogOpciones;
    private ActivityDatosTarjeta mActivityDatosTarjeta;
    private AdminBaseDatos mAdminBaseDatos;

    public DialogOpciones(ActivityDatosTarjeta activityDatosTarjeta){
        mActivityDatosTarjeta = activityDatosTarjeta;
        mAdminBaseDatos = new AdminBaseDatos(mActivityDatosTarjeta);
    }

    public Dialog crearDialog(final AdminTarjetas adminTarjetas, String tituloBoton, final Boolean cambios){
        mDialogOpciones = new Dialog(mActivityDatosTarjeta, R.style.Theme_Dialog_Translucent);
        mDialogOpciones.show();
        mDialogOpciones.setCancelable(false);
        mDialogOpciones.setContentView(R.layout.dialog_opciones);

        final Button mButtonEditarGuardar = (Button)mDialogOpciones.findViewById(R.id.buttonEditar);
        mButtonEditarGuardar.setText(tituloBoton);
        mButtonEditarGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mButtonEditarGuardar.getText().toString().equals(EDITAR)) {
                    mActivityDatosTarjeta.mEditando = true;
                    mActivityDatosTarjeta.habilitarEdicion();
                    mDialogOpciones.dismiss();
                } else if (mButtonEditarGuardar.getText().toString().equals(GUARDAR)) {
                    if (cambios) {
                        String empresa = adminTarjetas.getEmpresa();
                        String nombre = adminTarjetas.getNombre();
                        String correo = adminTarjetas.getCorreo();
                        String telefono1 = adminTarjetas.getTelefono1();
                        String telefono2 = adminTarjetas.getTelefono2();

                        boolean cambiado = mAdminBaseDatos.modificarTarjeta(adminTarjetas.getID(), empresa, nombre, correo, telefono1, telefono2);

                        if (cambiado) {
                            Toast.makeText(mActivityDatosTarjeta, R.string.toast_tarjeta_guardada, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(mActivityDatosTarjeta, R.string.toast_no_se_pudo_guardar_tarjeta, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(mActivityDatosTarjeta, R.string.toast_no_hay_cambios, Toast.LENGTH_SHORT).show();

                    mDialogOpciones.dismiss();
                }
            }
        });

        final Button mButton = (Button)mDialogOpciones.findViewById(R.id.buttonBorrar);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogBorrarTarjeta(mActivityDatosTarjeta).crearDialog(adminTarjetas, null);
                mDialogOpciones.dismiss();
            }
        });

        final ImageButton mImageButtonSalir = (ImageButton)mDialogOpciones.findViewById(R.id.imageButtonSalir);
        mImageButtonSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogOpciones.dismiss();
            }
        });

        return mDialogOpciones;
    }
}
