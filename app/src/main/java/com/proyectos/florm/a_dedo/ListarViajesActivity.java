package com.proyectos.florm.a_dedo;

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
                                Log.i("ELEMENTO DE LA LISTA", "SELECCIONADO "+list_viaje_id);
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
            Snackbar.make(findViewById(R.id.listar_layout), "Suscripto!", Snackbar.LENGTH_SHORT).show();

            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            viaje.setPasajeros(cantLugares-1);
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
}
