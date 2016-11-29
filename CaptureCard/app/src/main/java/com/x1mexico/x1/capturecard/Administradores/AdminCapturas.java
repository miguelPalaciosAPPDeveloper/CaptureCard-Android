package com.x1mexico.x1.capturecard.Administradores;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by X1 on 24/02/16.
 */
public class AdminCapturas {
    private static final String TAG = AdminCapturas.class.getSimpleName();
    private static final String PREFIJO = "tp_";
    private static final String TIPO_ARCHIVO = ".jpg";
    private static final String DATE_FORMAT = "yyyymmddhhmmss";
    private static final String CARPETA = "/CaptureCard/";
    private static final String RUTA_FOLDER_CAPTURAS = Environment.getExternalStorageDirectory() + CARPETA;

    public void iniciarGuardado(){
        File imagesFolder = new File(RUTA_FOLDER_CAPTURAS);
        if(!imagesFolder.exists()){
            if(imagesFolder.mkdirs()){Log.i(TAG, "Carpeta creada");}
        }
    }

    public String getRutaArchivo(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String date = dateFormat.format(new Date());

        return RUTA_FOLDER_CAPTURAS + PREFIJO + date + TIPO_ARCHIVO;
    }

    public Bitmap rediMensionar(String ruta, int width, int height){
        Log.d(TAG, "redimensionando");
        Bitmap bitmap = BitmapFactory.decodeFile(ruta);
        return  Bitmap.createBitmap(Bitmap.createScaledBitmap(bitmap, width, height, false));
    }


    public void comprimir(String ruta){
        Bitmap bitmap = rediMensionar(ruta, 1280, 768);
        try {
            File reduccion = new File(ruta);
            FileOutputStream out = new FileOutputStream(reduccion);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.e(TAG, "comprimido");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "no comprimido");
        }

        try {
            File reduccion = new File(ruta);
            FileOutputStream out = new FileOutputStream(reduccion);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
            Log.e(TAG, "comprimido 2");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "no comprimido 2");
        }
    }

    public Bitmap filtrarImagen(String ruta){
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(ruta);
        Bitmap bitmapGrayScale = grayScale(bitmap);
        Bitmap bitmapBlackAndWhite = blackAndWhite(bitmapGrayScale);

        return bitmapBlackAndWhite;
    }

    public Bitmap grayScale(Bitmap bitmap){
        Log.e(TAG, "escala de grices");
        //Bitmap bitmap;
        //bitmap = BitmapFactory.decodeFile(ruta);
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        Bitmap bitmapGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmapGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        Log.e(TAG, "fin escala de grices");
        return bitmapGrayscale;
    }


    public Bitmap blackAndWhite(Bitmap bitmap) {
        Log.e(TAG, "black and white");
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap bitmapBlackAndWhite = Bitmap.createBitmap(width, height, bitmap.getConfig());
        int A, R, G, B;
        int pixel;

        // scan through all pixels
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = bitmap.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);

                // use 128 as threshold, above -> white, below -> black
                if (gray > 128)
                    gray = 255;
                else
                    gray = 0;
                // set new pixel color to output bitmap
                bitmapBlackAndWhite.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }

        Log.e(TAG, "fin black and white");
        return bitmapBlackAndWhite;
    }
}
