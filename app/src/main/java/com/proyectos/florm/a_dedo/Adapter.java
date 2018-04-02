package com.proyectos.florm.a_dedo;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.proyectos.florm.a_dedo.Models.Viaje;

public class Adapter extends RecyclerView.Adapter{
    private DatabaseReference datos;

    public Adapter(DatabaseReference data){
        datos = data;

    }
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_viaje, parent, false);
        ViewHolderViaje vh = new ViewHolderViaje(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void onBindViewHolder(ViewHolderViaje holder, int position) {
       // Viaje item = datos.get(position);
       // holder.bindViaje(item);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolderViaje extends RecyclerView.ViewHolder{
        TextView lblDestino;
        TextView lblSalida;
        TextView lblFecha;
        TextView lblHora;
        TextView lblPasajeros;
        TextView lblEquipaje;
        TextView lblInformacion;
        Button botonSuscribir;

        public ViewHolderViaje(View itemView) {
            super(itemView);
            lblSalida = (TextView) itemView.findViewById(R.id.lblSalida);
            lblDestino = (TextView) itemView.findViewById(R.id.lblDestino);
            lblFecha = (TextView) itemView.findViewById(R.id.lblFecha);
            lblHora = (TextView) itemView.findViewById(R.id.lblHora);

            botonSuscribir = (Button) itemView.findViewById(R.id.btn_suscribir);
        }

        public void bindViaje(Viaje v) {
            lblSalida.setText(v.getSalida());
            lblDestino.setText(v.getDestino());
            lblFecha.setText(v.getFecha());
            lblHora.setText(v.getHora());
        }

    }
}
