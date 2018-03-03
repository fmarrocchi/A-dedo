package com.proyectos.florm.a_dedo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.Viaje;
import java.util.Calendar;

public class ViajeActivity extends AppCompatActivity
        implements View.OnClickListener {

    private DatabaseReference mDatabase;

    private EditText mDestinoField;
    private EditText mSalidaField;
    private Spinner mEquipajeSpinner;
    private Spinner mCantPasajerosSpinner;
    private EditText mHoraField;
    private EditText mFechaField;
    private EditText mInfoField;

    private FloatingActionButton mSubmitButton;

    private static final String REQUIRED = "Required";
    private static final String CERO = "0";
    private static final String BARRA = "/";
    private static final String DOS_PUNTOS = ":";

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la fecha
    final int mes = c.get(Calendar.MONTH);
    final int dia = c.get(Calendar.DAY_OF_MONTH);
    final int anio = c.get(Calendar.YEAR);

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    //Widgets para fecha y hora
    ImageButton ibObtenerFecha;
    ImageButton ibObtenerHora;

    //Asocia variables del formulario para obtener los datos ingresados para crear un viaje
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);

        //Instanciacion de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Variables formulario
        mDestinoField = findViewById(R.id.field_destino);
        mSalidaField = findViewById(R.id.field_salida);
        mEquipajeSpinner = findViewById(R.id.spinner_equipaje);
        mCantPasajerosSpinner = findViewById(R.id.spinner_pasajeros);
        mInfoField = findViewById(R.id.field_info_adicional);

        //Widget EditText donde se mostrara la fecha y hora obtenidas
        mFechaField = findViewById(R.id.et_mostrar_fecha_picker);
        mHoraField = findViewById(R.id.et_mostrar_hora_picker);
        //Widget ImageButton del cual usaremos el evento clic para obtener la fecha y hora
        ibObtenerFecha = (ImageButton) findViewById(R.id.ib_obtener_fecha);
        ibObtenerHora = (ImageButton) findViewById(R.id.ib_obtener_hora);
        //Evento setOnClickListener - clic
        ibObtenerFecha.setOnClickListener(this);
        ibObtenerHora.setOnClickListener(this);

        //Crear spinner con los datos posibles para equipaje
        Spinner mEquipajeSpinner = (Spinner) findViewById(R.id.spinner_equipaje);
        String[] equipaje = {"Si", "No"};
        mEquipajeSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, equipaje));

        //Crear spinner con los datos posibles para CantPasajeros
        Spinner mCantPasajerosSpinner = (Spinner) findViewById(R.id.spinner_pasajeros);
        Integer[] cantPasajeros = {0, 1, 2, 3, 4, 5};
        mCantPasajerosSpinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, cantPasajeros));

        //Boton de envio de formulario para crear un nuevo viaje
        mSubmitButton = findViewById(R.id.fab_submit_post);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                submitPost();
            }
        });
    }

    //Crea un viaje llamando al constructor de la clase viaje y luego lo inserta en la base de daos
    private void createViaje(String destino, String salida, String hora,
                             String fecha, String equipaje, Integer pasajeros, String estado, String informacion) {
        String key = mDatabase.child("viajes").push().getKey();
        Viaje viaje = new Viaje(destino, salida, hora, fecha, equipaje, pasajeros, estado, informacion);

        mDatabase.child("viajes").child(key).setValue(viaje, new DatabaseReference.CompletionListener(){
            //El segundo parametro es para recibir un mensaje si hubo error en el setValue
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if(error == null){
                    //PONER MENSAJE
                }
                else{
                    // Log.e(LOGTAG, "Error: " + error.getMessage());
                }
            }
        });
    }

    //Enviar formulario con datos del viaje a crear
    private void submitPost() {
        final String destino = mDestinoField.getText().toString();
        final String salida = mSalidaField.getText().toString();
        final String hora = mHoraField.getText().toString();
        final String fecha = mFechaField.getText().toString();
        final String equipaje = mEquipajeSpinner.getSelectedItem().toString();
        final Integer pasajeros = (Integer)mCantPasajerosSpinner.getSelectedItem();
        String informacion = mInfoField.getText().toString();

        // Destino is required
        if (TextUtils.isEmpty(destino)) {
            mDestinoField.setError(REQUIRED);
            return;
        }
        // Salida is required
        if (TextUtils.isEmpty(salida)) {
            mSalidaField.setError(REQUIRED);
            return;
        }
        // Hora is required
        if (TextUtils.isEmpty(hora)) {
            mHoraField.setError(REQUIRED);
            return;
        }
        // Fecha is required
        if (TextUtils.isEmpty(fecha)) {
            mFechaField.setError(REQUIRED);
            return;
        }

        //Si no hay informacion extra le asigno un guion (no es campo requerido)
        if (TextUtils.isEmpty(informacion)) {
           informacion = "-";
        }

        // Deshabilitar el boton para que no se cree mas de una vez
        setEditingEnabled(false);
        Toast.makeText(this, "Creando...", Toast.LENGTH_SHORT).show();

        // Crear viaje
        createViaje(destino, salida, hora, fecha, equipaje, pasajeros, "Activo", informacion);

        setEditingEnabled(true);

        Toast.makeText(this, "Viaje creado con exito", Toast.LENGTH_SHORT).show();

       // finish(); //destruye la actividad y no se puede acceder a ella hasta volverla a crear
        onBackPressed(); //vuelve a la Actividad o Fragmento anterior al que te encuentras en el momento

    }

    private void setEditingEnabled(boolean enabled) {
        mDestinoField.setEnabled(enabled);
        mSalidaField.setEnabled(enabled);
        mCantPasajerosSpinner.setEnabled(enabled);
        mHoraField.setEnabled(enabled);
        mFechaField.setEnabled(enabled);
        mEquipajeSpinner.setEnabled(enabled);
        mInfoField.setEnabled(enabled);

        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    //Oyente botones del widget de fecha y hora
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ib_obtener_fecha:
                obtenerFecha();
                break;
            case R.id.ib_obtener_hora:
                obtenerHora();
                break;
        }
    }

    private void obtenerFecha(){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                mFechaField.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
            }
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    private void obtenerHora(){
        TimePickerDialog recogerHora = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                //Formateo el hora obtenido: antepone el 0 si son menores de 10
                String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario
                String AM_PM;
                if(hourOfDay < 12) {
                    AM_PM = "a.m.";
                } else {
                    AM_PM = "p.m.";
                }
                //Muestro la hora con el formato deseado
                mHoraField.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
        }, hora, minuto, false);

            recogerHora.show();
    }

}
