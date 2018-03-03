package com.proyectos.florm.a_dedo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.AdaptadorViaje;

public class ListarViajesActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView recycler;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);

        final AdaptadorViaje adaptador = new AdaptadorViaje();

        adaptador.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Muestro en log un mje con el item seleccioando
                Log.i("DemoRecView", "Pulsado el elemento " + recycler.getChildPosition(v));

                //Voy a la actividad ver detalles del viaje
            }
        });

        recycler.setAdapter(adaptador);

        //Inicialización RecyclerView
        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
/*
        mAdapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(
                        Viaje.class, R.layout.listitem_viaje, ViajeHolder.class, mDataBase) {

                    public void populateViewHolder(ViajeHolder viajeViewHolder, Viaje viaje, int position) {
                        viajeViewHolder.setDestino(viaje.getDestino()+ " - ");
                        viajeViewHolder.setSalida(viaje.getSalida());
                        viajeViewHolder.setFecha(viaje.getFecha());
                        viajeViewHolder.setHora(viaje.getHora()+ " hs");
                        viajeViewHolder.setPasajeros(viaje.getPasajeros().toString());
                    }
                };
*/
    }

    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
    }

}
