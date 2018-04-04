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
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.Viaje;
import java.util.Calendar;

public class ViajeActivity extends BaseActivity implements View.OnClickListener {

    private DatabaseReference mDatabase;
    //Variable para guardar mail identificador del usuario actual
    private String usuario;
    private ProgressBar progressBar;
    private EditText inputDestino, inputOrigen, inputInfo, inputTel, inputDireccion, inputLocalidad, inputHora, inputFecha;
    private Spinner inputEquipajeSpinner, inputCantPasajerosSpinner;
    private Button mSubmitButton;

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
        inputDestino = findViewById(R.id.field_destino);
        inputOrigen = findViewById(R.id.field_salida);
        inputEquipajeSpinner = findViewById(R.id.spinner_equipaje);
        inputCantPasajerosSpinner = findViewById(R.id.spinner_pasajeros);
        inputInfo = findViewById(R.id.field_info_adicional);
        inputDireccion = findViewById(R.id.direccion);

        //Widget EditText donde se mostrara la fecha y hora obtenidas
        inputFecha = findViewById(R.id.etDate);
        inputHora = findViewById(R.id.etTime);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Crear spinner con los datos posibles para equipaje
        Spinner mEquipajeSpinner = findViewById(R.id.spinner_equipaje);
        String[] equipaje = {"Si", "No"};
        mEquipajeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, equipaje));

        //Crear spinner con los datos posibles para CantPasajeros
        Spinner mCantPasajerosSpinner = findViewById(R.id.spinner_pasajeros);
        Integer[] cantPasajeros = {1, 2, 3, 4};
        mCantPasajerosSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cantPasajeros));

        //Boton de envio de formulario para crear un nuevo viaje
        mSubmitButton = findViewById(R.id.submit_post);
    }

    //Crea un viaje llamando al constructor de la clase viaje y luego lo inserta en la base de datos
    private void createViaje(String direccion, String conductor, String destino, String salida, String hora,
                             String fecha, String equipaje, Integer pasajeros, String estado, String informacion) {
        String key = mDatabase.child("viajes").push().getKey();
        Viaje viaje = new Viaje(direccion, conductor, destino, salida, hora, fecha, equipaje, pasajeros, estado, informacion);
        mDatabase.child("viajes").child(key).setValue(viaje, new DatabaseReference.CompletionListener(){
            //El segundo parametro es para recibir un mensaje si hubo error en el setValue
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if(error == null){
                    Snackbar.make(findViewById(R.id.drawer_layout), "Viaje creado correctamente", Snackbar.LENGTH_SHORT).show();
                    //Creo con exito y vuelvo a la actividad anterior
                    progressBar.setVisibility(View.GONE);
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
        final String destino = inputDestino.getText().toString();
        final String origen = inputOrigen.getText().toString();
        final String hora = inputHora.getText().toString();
        final String fecha = inputFecha.getText().toString();
        final String equipaje = inputEquipajeSpinner.getSelectedItem().toString();
        final Integer pasajeros = (Integer)inputCantPasajerosSpinner.getSelectedItem();
        final String direccion = inputDireccion.getText().toString();
        String informacion = inputInfo.getText().toString();

        // Destino is required
        if (TextUtils.isEmpty(destino)) {
            inputDestino.setError(REQUIRED);
            return;
        }
        // Salida is required
        if (TextUtils.isEmpty(origen)) {
            inputOrigen.setError(REQUIRED);
            return;
        }
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

        // Direccion is required
        if (TextUtils.isEmpty(direccion)) {
            inputDireccion.setError(REQUIRED);
            return;
        }

        //Si no hay informacion extra le asigno un guion (no es campo requerido)
        if (TextUtils.isEmpty(informacion)) {
           informacion = " No hay información adicional.";
        }

        // Deshabilitar el boton para que no se cree mas de una vez
        setEditingEnabled(false);

        progressBar.setVisibility(View.VISIBLE);

        createViaje(direccion, usuario, destino, origen, hora, fecha, equipaje, pasajeros, "Activo", informacion);
        setEditingEnabled(true);
    }

    private void setEditingEnabled(boolean enabled) {
        inputDestino.setEnabled(enabled);
        inputOrigen.setEnabled(enabled);
        inputCantPasajerosSpinner.setEnabled(enabled);
        inputHora.setEnabled(enabled);
        inputFecha.setEnabled(enabled);
        inputEquipajeSpinner.setEnabled(enabled);
        inputInfo.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    //Oyente botones del widget de fecha y hora
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit_post:
                submitPost();
                break;
        }
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
