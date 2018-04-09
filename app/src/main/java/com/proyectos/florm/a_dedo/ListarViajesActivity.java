package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
    int contViajes;
    String destino;
    String fecha;
    String usuario;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mostrarViajes();
    }

    public FirebaseRecyclerAdapter adapterBuscar(){
        contViajes=0;
        String origen = getIntent().getExtras().getString("origen");
        destino = getIntent().getExtras().getString("destino");
        fecha = getIntent().getExtras().getString("fecha");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBase.orderByChild("salida").equalTo(origen)) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if ((viaje.getDestino().equals(destino)) && (viaje.getFecha().equals(fecha))) {
                            contViajes++;

                            viajeViewHolder.setDestino(" " + viaje.getDestino());
                            viajeViewHolder.setSalida(" " + viaje.getSalida());
                            viajeViewHolder.setFecha(" " + viaje.getFecha());
                            viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                            viajeViewHolder.setPasajeros(" " + viaje.getPasajeros());
                            viajeViewHolder.setInformacion(" " + viaje.getInformacion());
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
                    }
                };

        return adapter;
    }


    public FirebaseRecyclerAdapter adapterMisViajes(){
        contViajes=0;
        String conductor = getIntent().getExtras().getString("conductor");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_editar_viaje, ViajeViewHolder.class, mDataBase.orderByChild("conductor").equalTo(conductor)) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();
                            contViajes++;

                            viajeViewHolder.setDestino(" " + viaje.getDestino());
                            viajeViewHolder.setSalida(" " + viaje.getSalida());
                            viajeViewHolder.setFecha(" " + viaje.getFecha());
                            viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                            viajeViewHolder.setPasajeros(" " + viaje.getPasajeros());
                            //viajeViewHolder.setInformacion(" " + viaje.getInformacion()); //TIRA NULL POINTER EXCEPTION ACA NO SE PQ

                            final String list_viaje_id  = getRef(position).getKey();

                            viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    viajeViewHolder.masInfo();
                                }
                            });
                    }
                };

        return adapter;

    }

    public FirebaseRecyclerAdapter adapterMisSuscripciones(){
        contViajes=0;
        usuario = getIntent().getExtras().getString("usuario");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_missuscripciones, ViajeViewHolder.class, mDataBase.orderByChild("suscriptos")) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if ((viaje.getSuscriptos().contains(usuario))){ //TODO CAMBIAR SI CAMBIA LA FORMA DE LA LISTA DE SUSCRIPTOS
                            contViajes++;

                            viajeViewHolder.setDestino(" " + viaje.getDestino());
                            viajeViewHolder.setSalida(" " + viaje.getSalida());
                            viajeViewHolder.setFecha(" " + viaje.getFecha());
                            viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                            viajeViewHolder.setPasajeros(" " + viaje.getPasajeros());
                            viajeViewHolder.setInformacion(" " + viaje.getInformacion());
                            viajeViewHolder.setDatosConductor(viaje.getConductor());

                            viajeViewHolder.getBotonSuscribir().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //Desinscribir usuario actual a viaje seleccionado
                                    //dessuscribirUsuario(viaje, itemId);
                                }
                            });
                            final String list_viaje_id  = getRef(position).getKey();

                            viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    viajeViewHolder.masInfo();
                                }
                            });
                        }
                    }
                };

        return adapter;

    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        String opcion = getIntent().getExtras().getString("opcion");
        if(opcion.equals("buscar"))
            adapter = adapterBuscar();
        else
            if(opcion.equals("misviajes"))
                adapter = adapterMisViajes();
            else //opcion = mis suscripciones
                adapter = adapterMisSuscripciones();


        recycler.setAlpha(0.90f); //Dar transparencia
        recycler.setAdapter(adapter);
        if (contViajes == 0) {
            Snackbar.make(findViewById(R.id.lytContenedor), "Lo sentimos, aún no hay viajes que coincidan con su búsqueda.", Snackbar.LENGTH_LONG).show();
        }

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


                            //TODO Aca faltaria agregar cant de suscripciones a viaje al usuario---------
                            //Editar el viaje para agregar al nuevo suscripto
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/" + key , viaje);
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
