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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
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

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mostrarViajes();
    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = findViewById(R.id.listaViajes);
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

    public void suscribirUsuario(Viaje v, String k){
        final int cantLugares = v.getPasajeros();
        final Viaje viaje =v;
        final String key = k;

        //Opciones de dialogo segun catidad de lugares disponibles
        final String[] items = {"1","2","3","4"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);
        builder.setTitle("¿Cuantos pasajeros son?")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Log.i("Dialogos", "Opción elegida: " + items[item]);
                        //a item le sumo 1 ya que comienza en 0
                        int cant= item + 1;

                        if (cantLugares>item){
                            try {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();

                                viaje.setPasajeros(cantLugares-cant);
                                viaje.getSuscriptos().add(user.getEmail());
                            } catch (NullPointerException e) {
                                // Google Sign In failed, update UI appropriately
                                Log.w("Suscripcion", "fallo ", e);
                            }


                //--------Aca faltaria agregar cant de suscripciones a viaje al usuario---------

                            Map<String, Object> viajeNuevo = viaje.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/" + key , viajeNuevo);
                            mDataBase.updateChildren(childUpdates);

                            Snackbar.make(findViewById(R.id.listar_layout), cant + " lugares reservados", Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.snackbar_aceptar))
                                    .setAction("Aceptar", new View.OnClickListener() {
                                        public void onClick(View view) {

                                        }
                                    })
                            .show();
                        }
                        else{
                            Snackbar.make(findViewById(R.id.listar_layout), "No hay suficientes lugares!", Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.snackbar_aceptar))
                                    .setAction("Aceptar", new View.OnClickListener() {
                                       public void onClick(View view) {

                                        }
                                    })
                                    .show();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "Confirmacion Cancelada.");
                        dialog.cancel();
                    }
                });
            AlertDialog dialog = builder.create();
            dialog.show();
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


}
