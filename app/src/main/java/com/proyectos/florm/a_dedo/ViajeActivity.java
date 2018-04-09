package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.Viaje;
import java.util.Calendar;

public class ViajeActivity extends BaseActivity  {

    private DatabaseReference mDatabase;
    //Variable para guardar mail identificador del usuario actual
    private String usuario;
    private EditText inputInfo, inputTel, inputLocalidad, inputHora, inputFecha;
    private Spinner inputCantPasajerosSpinner;
    private Button mSubmitButton;
    private PlaceAutocompleteFragment origenAutocomplFrag, destinoAutocomplFrag, direccionAutocomplFrag;

    //Variables para guardar el origen, destino y direccion que ingresa el usuario
    private String origen;
    private String destino;
    private String direccion;

    private static final String REQUIRED = "Required";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Asocia variables del formulario para obtener los datos ingresados para crear un viaje
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);

        //Obtengo datos pasados de la actividad anterior
        usuario = getIntent().getStringExtra("usuario");

        //Instanciacion de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Variables formulario
        inputCantPasajerosSpinner = findViewById(R.id.spinner_pasajeros);
        inputInfo = findViewById(R.id.field_info_adicional);

        //Widget EditText donde se mostrara la fecha y hora obtenidas
        inputFecha = findViewById(R.id.etDate);
        inputHora = findViewById(R.id.etTime);

        //Crear spinner con los datos posibles para CantPasajeros
        Spinner mCantPasajerosSpinner = findViewById(R.id.spinner_pasajeros);
        Integer[] cantPasajeros = {1, 2, 3, 4};
        mCantPasajerosSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cantPasajeros));

        //Boton de envio de formulario para crear un nuevo viaje
        mSubmitButton = findViewById(R.id.submit_post);

        //Fragmento autocompletado para el origen
        origenAutocomplFrag = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.origen_autocomplete_fragment);
        origenAutocomplFrag.setHint("Desde");

        origenAutocomplFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                origen = place.getName().toString();
            }
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
            public void onPlaceSelected(Place place) {
                destino = place.getName().toString();
            }
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });

        //Fragmento autocompletado para la direccion
        direccionAutocomplFrag = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.direccion_autocomplete_fragment);
        direccionAutocomplFrag.setHint("Dirección de salida");

        direccionAutocomplFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            public void onPlaceSelected(Place place) {
                direccion = place.getName().toString();
            }
             public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("ERROR", "An error occurred: " + status);
            }
        });
    }

    //Crea un viaje llamando al constructor de la clase viaje y luego lo inserta en la base de datos
    private void createViaje(String conductor, String hora, String fecha, Integer pasajeros, String informacion) {
        Log.d("mgs", "create viaje");
        String key = mDatabase.child("viajes").push().getKey();
        Viaje viaje = new Viaje(direccion, conductor, destino, origen, hora, fecha, pasajeros, informacion);
        mDatabase.child("viajes").child(key).setValue(viaje, new DatabaseReference.CompletionListener(){
            //El segundo parametro es para recibir un mensaje si hubo error en el setValue
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if(error == null){
                    Snackbar.make(findViewById(R.id.drawer_layout), "Viaje creado correctamente", Snackbar.LENGTH_SHORT).show();
                    //Creo con exito y vuelvo a la actividad anterior
                    hideProgressDialog();
                    onBackPressed(); //vuelve a la Actividad o Fragmento anterior al que te encuentras en el momento
                }
                else{
                    Snackbar.make(findViewById(R.id.drawer_layout), "No se pudo crear viaje!", Snackbar.LENGTH_SHORT).show();
                    Log.e("ERROR", "Error: " + error.getMessage());
                }
            }
        });
    }

    //Enviar formulario con datos del viaje a crear
    private void submitPost() {
        final String hora = inputHora.getText().toString();
        final String fecha = inputFecha.getText().toString();
        final Integer pasajeros = (Integer)inputCantPasajerosSpinner.getSelectedItem();
        String informacion = inputInfo.getText().toString();

        // Hora is required
        if (TextUtils.isEmpty(hora)) {
            inputHora.setError(REQUIRED);
            return;
        }
        // Fecha is required
        if (TextUtils.isEmpty(fecha)) {
            inputFecha.setError(REQUIRED);
            return;
        }

        //Si no hay informacion extra le asigno un guion (no es campo requerido)
        if (TextUtils.isEmpty(informacion)) {
           informacion = " No hay información adicional.";
        }

        // Deshabilitar el boton para que no se cree mas de una vez
        setEditingEnabled(false);

        showProgressDialog();
        createViaje(usuario, hora, fecha, pasajeros, informacion);
        setEditingEnabled(true);
    }

    private void setEditingEnabled(boolean enabled) {
        // TODO ver como hacer esto con un fragmento
//        inputDestino.setEnabled(enabled);
//        inputOrigen.setEnabled(enabled);
        inputCantPasajerosSpinner.setEnabled(enabled);
        inputHora.setEnabled(enabled);
        inputFecha.setEnabled(enabled);
        inputInfo.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    public void crear(View view){
        submitPost();
    }

    public void showDatePickerDialog(View view) {
        DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int day) {
                // +1 because january is zero
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                inputFecha.setText(selectedDate);
            }
        },anio, mes, dia);
        //Muestro el widget
        dateDialog.show();
    }

    public void showTimePickerDialog(View view) {
        TimePickerDialog timeDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minutes) {
                final String selectedTime = twoDigits(hour) + ":" + twoDigits(minutes);
                inputHora.setText(selectedTime);
            }
        },hora, minuto, true);
        //Muestro el widget
        timeDialog.show();
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }
}
