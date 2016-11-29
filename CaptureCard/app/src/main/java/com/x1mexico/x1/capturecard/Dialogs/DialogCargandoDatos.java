package com.x1mexico.x1.capturecard.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.x1mexico.x1.capturecard.R;

/**
 * Created by X1 on 02/03/16.
 */
public class DialogCargandoDatos extends AsyncTask<Void, String, Boolean> {
    private static final String TAG = DialogCargandoDatos.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private TextView mTextViewTituloProgress;

    private static final String MENSAJE_UNO = "Obteniendo información";
    private static final String MENSAJE_DOS = "Obteniendo información .";
    private static final String MENSAJE_TRES = "Obteniendo información ..";
    private static final String MENSAJE_CUATRO = "Obteniendo información ...";
    private static final String MENSAJE_CINCO = "Obteniendo información ....";

    public DialogCargandoDatos(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(mContext, R.style.Theme_Dialog_Translucent);
        mProgressDialog.show();
        mProgressDialog.setContentView(R.layout.dialog_progress);
        mProgressDialog.setCancelable(false);

        mTextViewTituloProgress = (TextView)mProgressDialog.findViewById(R.id.textViewTituloProgress);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        int i = 0;
        while(true){
            delay();
            i++;
            switch (i){
                case 1:
                    publishProgress(MENSAJE_UNO);
                    break;
                case 2:
                    publishProgress(MENSAJE_DOS);
                    break;
                case 3:
                    publishProgress(MENSAJE_TRES);
                    break;
                case 4:
                    publishProgress(MENSAJE_CUATRO);
                    break;
                case 5:
                    publishProgress(MENSAJE_CINCO);
                    i = 0;
                    break;
            }
            if(isCancelled()){
                break;
            }
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        String mensaje = values[0];
        mTextViewTituloProgress.setText(mensaje);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mProgressDialog.dismiss();
    }

    private void delay(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
