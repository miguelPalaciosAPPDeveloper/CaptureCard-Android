package com.x1mexico.x1.capturecard.Adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.x1mexico.x1.capturecard.Activitys.ActivityDatosTarjeta;
import com.x1mexico.x1.capturecard.Activitys.ActivityMain;
import com.x1mexico.x1.capturecard.Administradores.AdminTarjetas;
import com.x1mexico.x1.capturecard.Dialogs.DialogBorrarTarjeta;
import com.x1mexico.x1.capturecard.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguelpalacios on 23/02/16.
 */
public class AdapterTarjetas extends RecyclerView.Adapter<AdapterTarjetas.ContactosViewHolder> {
    private static final String TAG = AdapterTarjetas.class.getSimpleName();
    private List<AdminTarjetas> mTarjetas;
    private ActivityMain mActivityMain;

    public class ContactosViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextViewEmpresa, mTextViewNombre, mTextViewCorreo, mTextViewTelefono1, mTextViewTelefono2;
        public ImageView mImageViewCaptura;
        public CardView mCardViewTarjetas;

        public ContactosViewHolder(View itemView) {
            super(itemView);

            mTextViewEmpresa = (TextView) itemView.findViewById(R.id.textViewEmpresa);
            mTextViewNombre = (TextView) itemView.findViewById(R.id.textViewNombre);
            mTextViewCorreo = (TextView) itemView.findViewById(R.id.textViewCorreo);
            mTextViewTelefono1 = (TextView) itemView.findViewById(R.id.textViewTelefono1);
            mTextViewTelefono2 = (TextView) itemView.findViewById(R.id.textViewTelefono2);

            mImageViewCaptura = (ImageView) itemView.findViewById(R.id.imageViewCaptura);

            mCardViewTarjetas = (CardView) itemView.findViewById(R.id.cardViewTarjetas);
        }
    }

    public AdapterTarjetas(ActivityMain activityMain){
        mTarjetas = new ArrayList<>();
        mActivityMain = activityMain;
    }

    public void limpiarLista(){
        mTarjetas.clear();}

    public void agregarTarjeta(AdminTarjetas adminTarjetas){
        mTarjetas.add(adminTarjetas);
    }

    public List<AdminTarjetas> getTarjetas(){return mTarjetas;}

    @Override
    public ContactosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_lista_tarjetas, parent, false);
        return new ContactosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ContactosViewHolder holder, final int position) {
        holder.mTextViewEmpresa.setText(mTarjetas.get(position).getEmpresa());
        holder.mTextViewNombre.setText(mTarjetas.get(position).getNombre());
        holder.mTextViewCorreo.setText(mTarjetas.get(position).getCorreo());
        holder.mTextViewTelefono1.setText(mTarjetas.get(position).getTelefono1());
        holder.mTextViewTelefono2.setText(mTarjetas.get(position).getTelefono2());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap captura = BitmapFactory.decodeFile(mTarjetas.get(position).getArchivo());
                holder.mImageViewCaptura.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.mImageViewCaptura.setImageBitmap(captura);
                    }
                });
            }
        }).start();

        holder.mCardViewTarjetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "click");
                Intent intent = new Intent(mActivityMain, ActivityDatosTarjeta.class);
                intent.putExtra(ActivityDatosTarjeta.TARJETA, (Serializable) mTarjetas.get(position));
                mActivityMain.startActivity(intent);
                mActivityMain.finish();
            }
        });

        holder.mCardViewTarjetas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e(TAG, "Largo");
                new DialogBorrarTarjeta(mActivityMain).crearDialog(mTarjetas.get(position), mActivityMain);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTarjetas.size();
    }
///agregado para searchView
    public AdminTarjetas removeItem(int position){
        final AdminTarjetas adminTarjetas = mTarjetas.remove(position);
        notifyItemRemoved(position);

        return adminTarjetas;
    }

    public void addItem(int position, AdminTarjetas adminTarjetas){
        mTarjetas.add(position, adminTarjetas);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition){
        final AdminTarjetas adminTarjetas = removeItem(fromPosition);
        mTarjetas.add(toPosition, adminTarjetas);
        notifyItemMoved(fromPosition, toPosition);
    }

    public void animateTo(List<AdminTarjetas> adminTarjetasList){
        applyAndAnimateRemovals(adminTarjetasList);
        applyAndAnimateAdditions(adminTarjetasList);
        applyAndAnimateMovedItems(adminTarjetasList);
    }

    public void applyAndAnimateRemovals(List<AdminTarjetas> adminTarjetasList){
        for(int i = mTarjetas.size() -1; i >= 0; i--){
            final AdminTarjetas adminTarjetas = mTarjetas.get(i);
            if(!adminTarjetasList.contains(adminTarjetas)){
                removeItem(i);
            }
        }
    }

    public void applyAndAnimateAdditions(List<AdminTarjetas> adminTarjetasList){
        for(int i = 0, count = adminTarjetasList.size(); i < count; i++){
            final AdminTarjetas adminTarjetas = adminTarjetasList.get(i);
            if(!adminTarjetasList.contains(adminTarjetas)){
                addItem(i, adminTarjetas);
            }
        }
    }

    public void applyAndAnimateMovedItems(List<AdminTarjetas> adminTarjetasList){
        for(int toPosition = adminTarjetasList.size() - 1; toPosition >= 0; toPosition--){
            final AdminTarjetas adminTarjetas = adminTarjetasList.get(toPosition);
            final int fromPosition = mTarjetas.indexOf(adminTarjetas);
            if(fromPosition >= 0 && fromPosition != toPosition){
                moveItem(fromPosition, toPosition);
            }
        }
    }
}
