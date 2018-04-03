package com.proyectos.florm.a_dedo.Holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Models.User;
import com.proyectos.florm.a_dedo.R;


    public class ViajeViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private Button botonSuscribir;
        private TextView nombre, tel, foto, info;
        private Boolean visible;

        public ViajeViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            botonSuscribir = view.findViewById(R.id.btn_suscribir);
            visible = false;
        }

        public void setDestino(String destino) {
            TextView field = view.findViewById(R.id.lblDestino);
            field.setText(destino);
        }

        public Button getBotonSuscribir() {
            return botonSuscribir;
        }

        public View getView() {
            return view;
        }

        public void setSalida(String salida) {
            TextView field = view.findViewById(R.id.lblSalida);
            field.setText(salida);
        }

        public void setFecha(String fecha) {
            TextView field = view.findViewById(R.id.lblFecha);
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

        public void setInformacion(String informacion) {
            info = (TextView) view.findViewById(R.id.lblInformacion);
            info.setText(informacion);
            info.setVisibility(View.GONE);
        }

        public void setDatosConductor(String conductor) {
            nombre = (TextView) view.findViewById(R.id.lblConductor);
            nombre.setVisibility(View.GONE);
            tel = (TextView) view.findViewById(R.id.lblTelefono);
            tel.setVisibility(View.GONE);
          //  foto = (TextView) view.findViewById(R.id.lblFoto);

            //Instancia a la base de datos
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //apuntamos al nodo que queremos leer
            DatabaseReference myRef = database.getReference("usuarios/"+conductor);

            //Agregamos un ValueEventListener para que los cambios que se hagan en la base de datos se reflejen en la aplicacion
            myRef.addValueEventListener(new ValueEventListener() {
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //leeremos un objeto de tipo Usuario
                    GenericTypeIndicator<User> u = new GenericTypeIndicator<User>() { };

                    //User user = dataSnapshot.getValue(u);

                    nombre.setText(dataSnapshot.child("nombre").getValue().toString());
                    tel.setText(dataSnapshot.child("telefono").getValue().toString());
                    // foto.setText(mDataBase.child(conductor).child("foto").toString());

                }

                public void onCancelled(DatabaseError error) {
                    Log.e("ERROR FIREBASE", error.getMessage());
                }
            });
        }

        public void masInfo(){
            if (visible) {
                nombre.setVisibility(View.GONE);
                tel.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                visible = false;
            }
            else{
                nombre.setVisibility(View.VISIBLE);
                tel.setVisibility(View.VISIBLE);
                info.setVisibility(View.VISIBLE);
                visible = true;
            }

        }
    }

