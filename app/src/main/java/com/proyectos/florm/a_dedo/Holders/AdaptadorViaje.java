package com.proyectos.florm.a_dedo.Holders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.Viaje;
import com.proyectos.florm.a_dedo.R;

public class AdaptadorViaje
        extends RecyclerView.Adapter<AdaptadorViaje.ViajeViewHolder>
        implements View.OnClickListener{

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase= database.getReference().child("viajes");;
    private View.OnClickListener listener;

    public static class ViajeViewHolder extends RecyclerView.ViewHolder {
        private View view;

        //Variables viaje
        TextView lblDestino;
        TextView lblSalida;
        TextView lblFecha;
        TextView lblHora;
        TextView lblPasajeros;
        TextView lblEquipaje;
        TextView lblInformacion;

        public ViajeViewHolder(View itemView) {
            super(itemView);


            lblDestino = (TextView) itemView.findViewById(R.id.lblDestino);
            lblSalida = (TextView) itemView.findViewById(R.id.lblSalida);
            lblFecha = (TextView) itemView.findViewById(R.id.lblFecha);
            lblHora = (TextView) itemView.findViewById(R.id.lblHora);
            lblPasajeros = (TextView) itemView.findViewById(R.id.lblPasajeros);
            lblEquipaje = (TextView) itemView.findViewById(R.id.lblEquipaje);
            lblInformacion = (TextView) itemView.findViewById(R.id.lblInformacion);
        }

        //Vincula la info del viaje
        public void bindViaje(Viaje v){
            lblDestino.setText(v.getDestino());
            lblSalida.setText(v.getSalida());
            lblFecha.setText(v.getFecha());
            lblHora.setText(v.getHora());
            lblPasajeros.setText(v.getPasajeros());
            lblEquipaje.setText(v.getEquipaje());
            lblInformacion.setText(v.getInformacion());
        }

        public void setDestino(String destino) {
            TextView field = (TextView) view.findViewById(R.id.lblDestino);
            field.setText(destino);
        }

        public void setSalida(String salida) {
            TextView field = (TextView) view.findViewById(R.id.lblSalida);
            field.setText(salida);
        }

        public void setFecha(String fecha) {
            TextView field = (TextView) view.findViewById(R.id.lblFecha);
            field.setText(fecha);
        }

        public void setHora(String hora) {
            TextView field = (TextView) view.findViewById(R.id.lblHora);
            field.setText(hora);
        }

        public void setPasajeros(String pasajeros) {
            TextView field = (TextView) view.findViewById(R.id.lblPasajeros);
            field.setText(pasajeros);
        }

        public void setEquipaje(String equipaje) {
            TextView field = (TextView) view.findViewById(R.id.lblEquipaje);
            field.setText(equipaje);
        }

        public void setInformacion(String info) {
            TextView field = (TextView) view.findViewById(R.id.lblInformacion);
            field.setText(info);
        }
    }

    public AdaptadorViaje() {}

    public ViajeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_viaje, parent, false);

        itemView.setOnClickListener(this);

        ViajeViewHolder vh = new ViajeViewHolder(itemView);

        return vh;
    }

     public void onBindViewHolder(ViajeViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }


}
