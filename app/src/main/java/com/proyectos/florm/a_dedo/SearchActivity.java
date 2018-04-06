package com.proyectos.florm.a_dedo;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.Viaje;

import java.util.Calendar;

//API GOOGLE PLACES
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.location.places.Places;

public class SearchActivity extends AppCompatActivity {

    //Campos de la vista
    private EditText etPlannedDate, inputDestino, inputOrigen;
    private PlaceAutocompleteFragment origenAutocomplFrag, destinoAutocomplFrag;

    //Referencia a la base de datos
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");
    //Para listar
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recycler;

    //Strings utilizados para el filtrado
    private String origen;
    private String destino;
    private String fecha;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();
    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    int contViajes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Inicializacion de los campos del formulario
        etPlannedDate = findViewById(R.id.etPlannedDate);

        //Fragmento autocompletado para el origen
        origenAutocomplFrag = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origen_autocomplete_fragment);
        origenAutocomplFrag.setHint("Desde");

        origenAutocomplFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                origen = place.getName().toString();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });

        //Fragmento autocompletado para el destino
        destinoAutocomplFrag = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destino_autocomplete_fragment);
        destinoAutocomplFrag.setHint("Hacia");

        destinoAutocomplFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destino = place.getName().toString();
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });
    }

    public void buscar(View view){
        fecha = etPlannedDate.getText().toString();

        mostrarViajes();
    }

    public void mostrarViajes() {
        //Inicialización RecyclerView
        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        contViajes = 0;

        adapter =
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
                                    //suscribirUsuario(viaje, itemId);
                                }
                            });
                            final String list_viaje_id = getRef(position).getKey();

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
        if (contViajes == 0) {
             Snackbar.make(findViewById(R.id.lytContenedor), "Lo sentimos, aún no hay viajes que coincidan con su búsqueda.", Snackbar.LENGTH_LONG).show();
        }
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

