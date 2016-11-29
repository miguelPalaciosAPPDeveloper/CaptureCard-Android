package com.x1mexico.x1.capturecard.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.x1mexico.x1.capturecard.Activitys.ActivityMain;
import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;
import com.x1mexico.x1.capturecard.Administradores.AdminTarjetas;
import com.x1mexico.x1.capturecard.R;

/**
 * Created by X1 on 12/04/16.
 */
public class DialogBorrarTarjeta {
    private static final String TAG = DialogBorrarTarjeta.class.getSimpleName();
    private Dialog mDialogBorrarTarjeta;
    private Activity mActivity;
    private AdminBaseDatos mAdminBaseDatos;

    public DialogBorrarTarjeta(Activity activity){
        mActivity = activity;
        mAdminBaseDatos = new AdminBaseDatos(mActivity);
    }

    public Dialog crearDialog(final AdminTarjetas adminTarjetas, final ActivityMain activityMain){
        mDialogBorrarTarjeta = new Dialog(mActivity, R.style.Theme_Dialog_Translucent);
        mDialogBorrarTarjeta.show();
        mDialogBorrarTarjeta.setCancelable(false);
        mDialogBorrarTarjeta.setContentView(R.layout.dialog_borrar_tarjeta);

        TextView mTextViewTitulo = (TextView)mDialogBorrarTarjeta.findViewById(R.id.textViewTitulo);
        mTextViewTitulo.setText(adminTarjetas.getEmpresa());

        Button mButtonSi = (Button) mDialogBorrarTarjeta.findViewById(R.id.buttonSi);
        mButtonSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean borrado = mAdminBaseDatos.borrarTarjeta(adminTarjetas.getID(),
                        adminTarjetas.getNombre(), adminTarjetas.getArchivo());

                if (borrado) {
                    Toast.makeText(mActivity, R.string.toast_tarjeta_borrada, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(mActivity, R.string.toast_no_se_pudo_borrar_tarjeta, Toast.LENGTH_SHORT).show();

                if(mActivity.getTitle().toString().equals(mActivity.getResources().getString(R.string.title_activity_activity_Main))){
                    activityMain.reiniciarLista();
                }else
                    mActivity.onBackPressed();

                mDialogBorrarTarjeta.dismiss();
            }
        });

        Button mButtonNo = (Button) mDialogBorrarTarjeta.findViewById(R.id.buttonNo);
        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogBorrarTarjeta.dismiss();
            }
        });

        return mDialogBorrarTarjeta;
    }
}
