package com.proyectos.florm.a_dedo;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.Viaje;

import java.util.Calendar;

public class SearchActivity extends AppCompatActivity {

    //Campos de la vista
    private EditText etPlannedDate, inputDestino, inputOrigen;

    //Referencia a la base de datos
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");
    //Para listar
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recycler;

    //Strings utilizados para el filtrado
    private String destino;
    private String fecha;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Inicializacion de los campos del formulario
        inputDestino = findViewById(R.id.destinoField);
        inputOrigen = findViewById(R.id.origenField);
        etPlannedDate = findViewById(R.id.etPlannedDate);
    }

    public void buscar(View view){
        String origen = inputOrigen.getText().toString();

        destino = inputDestino.getText().toString();
        fecha = etPlannedDate.getText().toString();;

        mostrarViajes(origen);
    }

    public void mostrarViajes(String origen){
        //Inicialización RecyclerView
        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBase.orderByChild("salida").equalTo(origen)) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId  = getRef(position).getKey();

                        //if (viaje.getDestino().equals(destino)){
                        if( (viaje.getDestino().equals(destino)) && (viaje.getFecha().equals(fecha))){
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
                                    //suscribirUsuario(viaje, itemId);
                                }
                            });
                            final String list_viaje_id  = getRef(position).getKey();

                            viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //viajeViewHolder.masInfo();
                                }
                            });
                        }
                    }
                };
        recycler.setAlpha(0.90f); //Dar transparencia
        recycler.setAdapter(adapter);
    }

    public void showDatePickerDialog(View view) {
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                etPlannedDate.setText(selectedDate);
            }
        },anio, mes, dia);
        //Muestro el widget
        dateDialog.show();
    }

    private String twoDigits(int n) {

        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

}

