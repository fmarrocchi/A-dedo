package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.Viaje;
import java.util.HashMap;
import java.util.Map;

public class ListarViajesActivity extends BaseActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recycler;

    static Integer eleccion;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mostrarViajes();
    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBase) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId  = getRef(position).getKey();

                        viajeViewHolder.setDestino(" "+viaje.getDestino());
                        viajeViewHolder.setSalida(" "+viaje.getSalida());
                        viajeViewHolder.setFecha(" "+viaje.getFecha());
                        viajeViewHolder.setHora(" "+viaje.getHora()+ " hs");
                        viajeViewHolder.setPasajeros(" "+viaje.getPasajeros());
                        viajeViewHolder.setInformacion(" "+viaje.getInformacion());
                        viajeViewHolder.setDatosConductor(viaje.getConductor());

                        viajeViewHolder.getBotonSuscribir().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                //Suscribir usuario actual a viaje seleccionado
                                suscribirUsuario(viaje, itemId);
                            }
                        });
                        final String list_viaje_id  = getRef(position).getKey();

                        viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                viajeViewHolder.masInfo();
                            }
                        });
                    }
                };
        recycler.setAlpha(0.90f); //Dar transparencia
        recycler.setAdapter(adapter);
    }

    public void suscribirUsuario(Viaje viaje, String key){
        int cantLugares = viaje.getPasajeros();
        if (cantLugares>0){
            DialogoSeleccionLugares dialogo = new DialogoSeleccionLugares();
            dialogo.show(getFragmentManager(), "Seleccionar pasajeros");
            Log.i("DIALOGO", "MOSTRADO");

            //Inicializo variable
            eleccion = 0;
          //  Snackbar.make(findViewById(R.id.listar_layout), "Suscripto!", Snackbar.LENGTH_SHORT).show();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();

            viaje.setPasajeros(cantLugares-eleccion);
            viaje.getSuscriptos().add(user.getEmail());

            Map<String, Object> viajeNuevo = viaje.toMap();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/" + key , viajeNuevo);
            mDataBase.updateChildren(childUpdates);
        }
        else{
            Snackbar.make(findViewById(R.id.listar_layout), "Viaje completo! No puede subscribirse", Snackbar.LENGTH_SHORT).show();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }

    //Metodo para verificar si el usuario esta logueado antes de comenzar la actividad
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            //Si no esta logueado voy a la actividad de login
            startActivity(new Intent(ListarViajesActivity.this, SignInActivity.class));
        }
    }

    //Clase para Dialogo para seleccionar cantidad de lugares a reservar
    public static class DialogoSeleccionLugares extends DialogFragment{

        public Dialog onCreateDialog(Bundle savedInstanceState, int cant) {

            //Opciones de dialogo segun catidad de lugares disponibles
            final String[] items = {};
            for (int i = 1; i < cant; i++) {
                items[i] = i+"";
            }
            Log.i("Opciones de dialogo", items.toString());
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("¿Cuantos pasajeros son?")
                    .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Log.i("Dialogos", "Opción elegida: " + items[item]);
                            eleccion = item;
                        }
                    })
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Dialogos", "Confirmacion Aceptada.");
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i("Dialogos", "Confirmacion Cancelada.");
                            dialog.cancel();
                        }
                    });
            return builder.create();
        }
    }

}
