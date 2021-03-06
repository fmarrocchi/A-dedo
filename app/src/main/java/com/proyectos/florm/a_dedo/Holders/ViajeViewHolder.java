package com.proyectos.florm.a_dedo.Holders;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Models.User;
import com.proyectos.florm.a_dedo.R;

public class ViajeViewHolder extends RecyclerView.ViewHolder{
        private View view;
        private Button botonSuscribir;
        private TextView nombre, tel, foto, info, direccion;
        private Boolean visible;
        private ImageButton btnVerMas;

    public ViajeViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            botonSuscribir = view.findViewById(R.id.btn_suscribir);
            btnVerMas = view.findViewById(R.id.btn_ver_mas);
            visible = false;
        }

    public ImageButton getBtnVerMas() {
        return btnVerMas;
    }

    public void setBtnVerMas(ImageButton btnVerMas) {
        this.btnVerMas = btnVerMas;
    }

    public Button getBotonSuscribir() {
        return botonSuscribir;
    }

    public View getView() {
        return view;
    }

    public void setOrigen(String origen) {
        TextView field = view.findViewById(R.id.lblOrigen);
        field.setText(origen);
    }

    public void setFecha(String fecha) {
        TextView field = view.findViewById(R.id.lblFecha);
        field.setText(fecha);
    }

    public void setHora(String hora) {
        TextView field = view.findViewById(R.id.lblHora);
        field.setText(hora);
    }

    public void setLugares(String lugares) {
        TextView field = view.findViewById(R.id.lblLugares);
        field.setText(lugares);
    }

    public void setDestino(String destino) {
        TextView field = view.findViewById(R.id.lblDestino);
        field.setText(destino);
    }

    public void setInformacion(String informacion) {
        info = view.findViewById(R.id.lblInformacion);
        info.setText(informacion);
        info.setVisibility(View.GONE);
    }

    public void setDireccion(String dir) {
        TextView field = view.findViewById(R.id.lblDireccion);
        field.setText(dir);
    }

    public void setDatosConductor(String conductor) {
        nombre = view.findViewById(R.id.lblConductor);
        nombre.setVisibility(View.GONE);
        tel = view.findViewById(R.id.lblTelefono);
        tel.setVisibility(View.GONE);

        //Instancia a la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //apuntamos al nodo que queremos leer
        DatabaseReference myRef = database.getReference("usuarios/"+conductor);

        //Agregamos un ValueEventListener para que los cambios que se hagan en la base de datos se reflejen en la aplicacion
        myRef.addValueEventListener(new ValueEventListener() {
             public void onDataChange(DataSnapshot dataSnapshot) {
                //leeremos un objeto de tipo Usuario
                User user = dataSnapshot.getValue(User.class);

                nombre.setText(user.getNombre());
                tel.setText(user.getTelefono());
                //foto.setText(mDataBase.child(conductor).child("foto").toString());

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
            btnVerMas.setImageResource(R.drawable.icono_flecha);
            visible = false;
        }
        else{
            nombre.setVisibility(View.VISIBLE);
            tel.setVisibility(View.VISIBLE);
            info.setVisibility(View.VISIBLE);
            btnVerMas.setImageResource(R.drawable.icono_ver_menos);
            visible = true;
        }
    }

    public void ocultar(){
        botonSuscribir.setVisibility(View.GONE);
        btnVerMas.setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion2).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion3).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion4).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion5).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion6).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion7).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion8).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion9).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion10).setVisibility(View.GONE);
        view.findViewById(R.id.layout_suscripcion11).setVisibility(View.GONE);
        view.findViewById(R.id.lblHora).setVisibility(View.GONE);
        view.findViewById(R.id.lblFecha).setVisibility(View.GONE);
        view.findViewById(R.id.lblDestino).setVisibility(View.GONE);
        view.findViewById(R.id.lblOrigen).setVisibility(View.GONE);
    }
}

