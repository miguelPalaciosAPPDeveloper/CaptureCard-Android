package com.x1mexico.x1.capturecard.Administradores;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by X1 on 10/03/16.
 */
public class AdminTexto {
    private static final String TAG = AdminTexto.class.getSimpleName();
    private static final String LANG = "eng";
    private static final String CARPETA = "/CaptureCard/tessdata/";
    private static final String RUTA_FOLDER_CAPTURAS = Environment.getExternalStorageDirectory() + CARPETA;

    public void traineddata(Context context){
        File tessdataFolder = new File(RUTA_FOLDER_CAPTURAS);
        tessdataFolder.mkdirs();

        if(!(new File(RUTA_FOLDER_CAPTURAS + LANG + ".traineddata")).exists()){
            Log.e(TAG, "no existe");
            try {
                AssetManager assetManager = context.getAssets();
                InputStream in = assetManager.open("tessdata/" + LANG + ".traineddata");
                OutputStream out = new FileOutputStream(RUTA_FOLDER_CAPTURAS + LANG + ".traineddata");
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                Log.v(TAG, "Copied " + LANG + " traineddata");
            } catch (IOException e) {
                Log.e(TAG, "Was unable to copy " + LANG + " traineddata " + e.toString());
            }
        }
    }

    public ArrayList<String> obtenerTexto(String expresionRegular, String textoTesseract){
        ArrayList<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(expresionRegular);
        Matcher matcher = pattern.matcher(textoTesseract);

        int i = 0;
        while(matcher.find()){
            Log.e(TAG, "resultado: " + matcher.group(1));
            result.add(matcher.group(1));
            i++;
        }
        return result;
    }
}
