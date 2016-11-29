package com.x1mexico.x1.capturecard.Activitys;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.x1mexico.x1.capturecard.Adapter.AdapterTarjetas;
import com.x1mexico.x1.capturecard.Administradores.AdminBaseDatos;
import com.x1mexico.x1.capturecard.Administradores.AdminCapturas;
import com.x1mexico.x1.capturecard.Administradores.AdminTarjetas;
import com.x1mexico.x1.capturecard.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityMain extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private static final String TAG = ActivityMain.class.getSimpleName();
    private AdminCapturas mAdminCapturas;
    private static final int TOMAR_FOTO = 1;
    private String rutaArchivo;
    private AdminBaseDatos mAdminBaseDatos;
    private AdapterTarjetas mAdapterTarjetas;
    private RecyclerView mRecyclerViewLista;
    private List<AdminTarjetas> mListAdminTarjetas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_activity_Main);

        mAdminBaseDatos = new AdminBaseDatos(this);

        mRecyclerViewLista = (RecyclerView) findViewById(R.id.recyclerViewLista);
        mRecyclerViewLista.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewLista.setLayoutManager(mLayoutManager);

        mAdapterTarjetas = new AdapterTarjetas(this);
        mRecyclerViewLista.setAdapter(mAdapterTarjetas);
        obtenerTarjetas();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarCamara();
            }
        });
    }

    public void iniciarCamara(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAdminCapturas = new AdminCapturas();
                mAdminCapturas.iniciarGuardado();

                mAdapterTarjetas.limpiarLista();
                rutaArchivo = mAdminCapturas.getRutaArchivo();
                File nuevaCaptura = new File(rutaArchivo);
                try{
                    if(nuevaCaptura.createNewFile()) {Log.d(TAG, "archivo creado");}
                }catch (Exception E){
                    Log.e(TAG, E.getMessage());
                }
                Uri uriSavedImage = Uri.fromFile(nuevaCaptura);
                final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivityForResult(cameraIntent, TOMAR_FOTO);
                    }
                });
            }
        }).start();
    }

    public void obtenerTarjetas(){
        mAdapterTarjetas.limpiarLista();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> ID = mAdminBaseDatos.consultaIdTarjetas();
                if(ID != null){
                    for(String mId: ID){
                        mAdapterTarjetas.agregarTarjeta(mAdminBaseDatos.consultarTarjeta(mId));
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if(ID != null){
                            for(String mId: ID){
                                mAdapterTarjetas.agregarTarjeta(mAdminBaseDatos.consultarTarjeta(mId));
                            }
                        }*/
                        mAdapterTarjetas.notifyDataSetChanged();
                        mListAdminTarjetas = mAdapterTarjetas.getTarjetas();
                        Log.e(TAG, "tamaño en hilo: " + mListAdminTarjetas.size());
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == TOMAR_FOTO && resultCode == RESULT_OK){
            Intent intentPreview = new Intent(this, ActivityPreview.class);
            intentPreview.putExtra(ActivityPreview.RUTA_ARCHIVO, rutaArchivo);
            startActivity(intentPreview);
            finish();
        }else{
            File borrarArchivo = new File(rutaArchivo);
            if (borrarArchivo.exists()) {
                if(borrarArchivo.delete()){
                    obtenerTarjetas();
                    Log.e(TAG, "no se tomo la captura");
                }
            }
        }
    }

    public void reiniciarLista() {
        mAdapterTarjetas.limpiarLista();
        obtenerTarjetas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        final MenuItem item = menu.findItem(R.id.itemBuscar);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e(TAG, "submit");
        if(query.isEmpty()){
            reiniciarLista();
        }else{
            Log.e(TAG, "busqueda");
            final List<AdminTarjetas> filtroBusqueda = filtro(mListAdminTarjetas, query);
            mAdapterTarjetas.animateTo(filtroBusqueda);
            mRecyclerViewLista.scrollToPosition(0);
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        Log.e(TAG, "tamaño: " + mListAdminTarjetas.size());
        Log.e(TAG, "change");
        if(query.isEmpty()){
            reiniciarLista();
        }else{
            final List<AdminTarjetas> filtroBusqueda = filtro(mAdapterTarjetas.getTarjetas(), query);
            mAdapterTarjetas.animateTo(filtroBusqueda);
            mRecyclerViewLista.scrollToPosition(0);
        }
        return true;
    }

    public List<AdminTarjetas> filtro(List<AdminTarjetas> adminTarjetasList, String query){
        query = query.toLowerCase();

        final List<AdminTarjetas> filtroTarjetas = new ArrayList<>();
        for(AdminTarjetas adminTarjetas: adminTarjetasList){
            final String empresa = adminTarjetas.getEmpresa().toLowerCase();
            final String nombre = adminTarjetas.getNombre().toLowerCase();
            if(nombre.contains(query) || empresa.contains(query)){
                filtroTarjetas.add(adminTarjetas);
            }
        }

        return filtroTarjetas;
    }
}
