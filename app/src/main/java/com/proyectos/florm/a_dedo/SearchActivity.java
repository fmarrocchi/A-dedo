package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.view.View;

//Google api places
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Calendar;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    //Campos de la vista
    private EditText etPlannedDate, inputDestino, inputOrigen;
    private PlaceAutocompleteFragment origenAutocomplFrag, destinoAutocomplFrag;

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
        if(validar()) {
            Intent intent = new Intent(this, ListarViajesActivity.class);
            intent.putExtra("origen", origen);
            intent.putExtra("destino", destino);
            intent.putExtra("fecha", fecha);
            intent.putExtra("opcion", "buscar");

            startActivity(intent);
        }
    }

    public boolean validar() {
        boolean esValido= true;
        // Fecha is required
        if (TextUtils.isEmpty(fecha)) {
            esValido= false;
        }

        if (TextUtils.isEmpty(origen)) {
            esValido= false;
        }
        if (TextUtils.isEmpty(destino)) {
            esValido= false;
        }

        if(!esValido){
            AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
            builder.setMessage("Por favor complete todos los campos requeridos.").setTitle("Error");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return esValido;
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

