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


    public class ViajeViewHolder extends RecyclerView.ViewHolder {
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
            view = itemView;
         }

        public void setDestino(String destino) {
            TextView field =  view.findViewById(R.id.lblDestino);
            field.setText(destino);
        }

        public void setSalida(String salida) {
            TextView field =  view.findViewById(R.id.lblSalida);
            field.setText(salida);
        }

        public void setFecha(String fecha) {
            TextView field =  view.findViewById(R.id.lblFecha);
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

