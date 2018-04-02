package com.proyectos.florm.a_dedo;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import com.proyectos.florm.a_dedo.Models.User;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
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

public class ViajeActivity extends BaseActivity
        implements View.OnClickListener {

    private DatabaseReference mDatabase;

    //Variable para guardar mail identificador del usuario actual
    private String usuario;

    private EditText inputDestino, inputOrigen, inputInfo, inputTel, inputDireccion, inputLocalidad, inputHora, inputFecha;
    private Spinner inputEquipajeSpinner, inputCantPasajerosSpinner;

    private Button mSubmitButton;

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
    ImageButton ibObtenerFecha, ibObtenerHora;

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
        inputDireccion = (EditText) findViewById(R.id.direccion);

        //Widget EditText donde se mostrara la fecha y hora obtenidas
        inputFecha = findViewById(R.id.et_mostrar_fecha_picker);
        inputHora = findViewById(R.id.et_mostrar_hora_picker);

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
        Integer[] cantPasajeros = {1, 2, 3, 4};
        mCantPasajerosSpinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, cantPasajeros));

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
        final String salida = inputOrigen.getText().toString();
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
        if (TextUtils.isEmpty(salida)) {
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
        Toast.makeText(this, "Creando...", Toast.LENGTH_SHORT).show();

        // Crear viaje
        createViaje(direccion, usuario, destino, salida, hora, fecha, equipaje, pasajeros, "Activo", informacion);

        setEditingEnabled(true);

        Toast.makeText(this, "Viaje creado con exito", Toast.LENGTH_SHORT).show();
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
            case R.id.ib_obtener_fecha:
                obtenerFecha();
            case R.id.ib_obtener_hora:
                obtenerHora();
            case R.id.submit_post:
                submitPost();
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
                inputFecha.setText(diaFormateado + BARRA + mesFormateado + BARRA + year);
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
                inputHora.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + " " + AM_PM);
            }
        }, hora, minuto, false);

            recogerHora.show();
    }

}
