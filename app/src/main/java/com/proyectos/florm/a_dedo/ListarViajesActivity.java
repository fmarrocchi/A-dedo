package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Holders.EditViajeViewHolder;
import com.proyectos.florm.a_dedo.Holders.MisSuscripcionesViewHolder;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.User;
import com.proyectos.florm.a_dedo.Models.Viaje;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListarViajesActivity extends BaseActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recycler;
    private TextView toolbarUser;
    private TextView lblHora;
    private int contViajes;
    String destino;
    String fecha;
    String usuario;

    //Calendario para obtener fecha & hora
    public final Calendar c = Calendar.getInstance();

    //Variables para obtener la hora hora
    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Volver atras con la barra de tareas
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbarUser = findViewById(R.id.toolbar_user);

        mostrarViajes();
    }

    public FirebaseRecyclerAdapter adapterBuscar(){
        String origen = getIntent().getExtras().getString("origen");
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destino = getIntent().getExtras().getString("destino");
        fecha = getIntent().getExtras().getString("fecha");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBase.orderByChild("origen").equalTo(origen)) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if ((viaje.getDestino().equals(destino)) && (viaje.getFecha().equals(fecha)) && (!viaje.getConductor().equals(usuario)) ) {
                            viajeViewHolder.setDestino(" " + viaje.getDestino());
                            viajeViewHolder.setOrigen(" " + viaje.getOrigen());
                            viajeViewHolder.setFecha(" " + viaje.getFecha());
                            viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                            viajeViewHolder.setLugares(" " + viaje.getLugares());
                            viajeViewHolder.setInformacion(" " + viaje.getInformacion());
                            viajeViewHolder.setDatosConductor(viaje.getConductor());

                            viajeViewHolder.getBotonSuscribir().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //Suscribir usuario actual a viaje seleccionado
                                    suscribirUsuario(viaje, itemId);
                                }
                            });
                            final String list_viaje_id  = getRef(position).getKey();

                            viajeViewHolder.getBtnVerMas().setOnClickListener(new View.OnClickListener() {
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
       String conductor = getIntent().getExtras().getString("conductor");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, EditViajeViewHolder>(Viaje.class, R.layout.listitem_editar_viaje, EditViajeViewHolder.class, mDataBase.orderByChild("conductor").equalTo(conductor)) {
                    public void populateViewHolder(final EditViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();
                        viajeViewHolder.setOrigen(" " + viaje.getOrigen());
                        viajeViewHolder.setDestino(" " + viaje.getDestino());
                        viajeViewHolder.setFecha(" " + viaje.getFecha());
                        viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                        viajeViewHolder.setLugares(" " + viaje.getLugares());
                        viajeViewHolder.setDireccion(" "+ viaje.getDireccion());
                        viajeViewHolder.setInformacion(" " + viaje.getInformacion());

                        final String list_viaje_id  = getRef(position).getKey();

                        viajeViewHolder.getBotonEditar().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editarViaje(viajeViewHolder, viaje, itemId);
                            }
                        });

                        viajeViewHolder.getBotonEliminar().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                eliminarViaje(itemId);
                            }
                        });

                        viajeViewHolder.getBotonVerSuscriptos().setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (viajeViewHolder.getBotonVerSuscriptos().getText().equals("Ver suscriptos")){
                                    verSuscriptos(viajeViewHolder, viaje, itemId);
                                    viajeViewHolder.getBotonVerSuscriptos().setText("Ocultar");
                                }
                                else {
                                    viajeViewHolder.getTextSuscriptos().setVisibility(View.GONE);
                                    viajeViewHolder.getBotonVerSuscriptos().setText("Ver suscriptos");
                                }
                            }
                        });
                    }
                };
        return adapter;
    }

    public FirebaseRecyclerAdapter adapterMisSuscripciones(){
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, MisSuscripcionesViewHolder>(Viaje.class, R.layout.listitem_missuscripciones, MisSuscripcionesViewHolder.class, mDataBase) {
                    public void populateViewHolder(final MisSuscripcionesViewHolder suscripcionesViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if (viaje != null && viaje.getSuscriptos()!= null ) {
                            if (viaje.getSuscriptos().containsKey(usuario)){
                               suscripcionesViewHolder.setDestino(" " + viaje.getDestino());
                                suscripcionesViewHolder.setOrigen(" " + viaje.getOrigen());
                                suscripcionesViewHolder.setFecha(" " + viaje.getFecha());
                                suscripcionesViewHolder.setHora(" " + viaje.getHora() + " hs");
                                suscripcionesViewHolder.setLugares(" " + viaje.getLugares());
                                suscripcionesViewHolder.setInformacion(" " + viaje.getInformacion());
                                suscripcionesViewHolder.setDatosConductor(viaje.getConductor());

                                suscripcionesViewHolder.getBotonDesuscribir().setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        desuscribirUsuario(viaje, itemId);
                                    }
                                });
                                final String list_viaje_id = getRef(position).getKey();

                                suscripcionesViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        suscripcionesViewHolder.masInfo();
                                    }
                                });
                            }
                        }
                    }
                };
        return adapter;
    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = findViewById(R.id.recycler_viajes);
        recycler.setHasFixedSize(true);

        String opcion = getIntent().getExtras().getString("opcion");
        if(opcion.equals("buscar"))
            adapter = adapterBuscar();
        else
            if(opcion.equals("misviajes")){
                Log.i("MIS VIAJES", "la opcion es misviajes");
                adapter = adapterMisViajes();}
            else
                if (opcion.equals("missuscripciones")){
                    Log.i("MIS VIAJES", "voy a llamar al adapter");
                    adapter = adapterMisSuscripciones();
                }
                else
                    Log.i("MIS VIAJES", "no hay adapter");
        Log.i("MIS VIAJES", "adapter es null: "+adapter.equals(null));
       // recycler.setAlpha(0.90f); //Dar transparencia
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    public void suscribirUsuario(Viaje v, String k){
        final int cantLugares = v.getLugares();
        final Viaje viaje =v;
        final String key = k;
        Log.i("ItemId", "es: "+k+" y el userId es: "+FirebaseAuth.getInstance().getCurrentUser().getUid());

        //Opciones de dialogo segun catidad de lugares disponibles
        final String[] items = {"1","2","3","4"};

        AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);
        builder.setTitle("¿Cuantos pasajeros son?")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //a item le sumo 1 ya que comienza en 0
                        int cant_a_reservar= item + 1;
                        if (cantLugares>=cant_a_reservar){
                            try {
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                String id_user = user.getUid();
                                viaje.setLugares(cantLugares-cant_a_reservar);
                                Map<String, Integer> suscriptos = viaje.getSuscriptos();

                                if (suscriptos == null) {
                                    suscriptos = new HashMap<String, Integer>();
                                    viaje.setSuscriptos(suscriptos);
                                }
                                //Agrego usuario y cantidad de reservas a la lista de suscritos del viaje
                                if (suscriptos.containsKey(id_user)){
                                    //El usuario ya tenia reservas en este viaje, las sumo
                                    int reserva_anterior = suscriptos.get(id_user);
                                    cant_a_reservar += reserva_anterior;
                                }
                                suscriptos.put(id_user, cant_a_reservar);

                            } catch (NullPointerException e) {
                                // Google Sign In failed, update UI appropriately
                                Log.w("Suscripcion", "fallo ", e);
                            }

                            //Editar el viaje para agregar al nuevo suscripto
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("/" + key , viaje);
                            mDataBase.updateChildren(childUpdates);

                            //mostrar cartel de reservacion de lugares
                            Snackbar.make(findViewById(R.id.listar_layout), cant_a_reservar + " lugares reservados", Snackbar.LENGTH_INDEFINITE)
                                    .setActionTextColor(getResources().getColor(R.color.snackbar_aceptar))
                                    .setAction("Aceptar", new View.OnClickListener() {
                                        public void onClick(View view) {

                                        }
                                    })
                                    .show();
                        }
                        else{
                            //mostrar cartel de lugares insuficientes
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

    public void desuscribirUsuario(Viaje v, String k) {
        Log.i("DESUSCRIBIR", "LLEGUE");
        final Viaje viaje = v;
        final String key = k; //Clave del viaje en bd para luego modificarlo

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String id_user = user.getUid();

        try {
            Map<String, Integer> suscriptos = viaje.getSuscriptos();
            Log.i("DESUSCRIBIR", "obtuve suscriptos");
            if (suscriptos.containsKey(id_user)) {
                //obtengo la cantidad de reservas que realizo el usuario para este viaje
                int cant_reservados = suscriptos.get(id_user);
                Log.i("DESUSCRIBIR", "cant de reservas: "+cant_reservados);
                //devuelvo los lugares al viaje
                viaje.setLugares(viaje.getLugares() + cant_reservados);
                //elimino el usuario de la lista de suscritos del viaje
                suscriptos.remove(id_user);
                // TODO viaje.setSuscriptos(suscriptos);
            }
        } catch (NullPointerException e) {
            // Google Sign In failed, update UI appropriately
            Log.e("DESUSCRIBIR", "fallo ", e);
        }
        //Editar el viaje para eliminar el suscripto
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + key, viaje);
        mDataBase.updateChildren(childUpdates);

        //Mostrar mensaje al usuario
        Snackbar.make(findViewById(R.id.listar_layout), "Desuscrito del viaje!", Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.snackbar_aceptar))
                .setAction("Aceptar", new View.OnClickListener() {
                    public void onClick(View view) {

                    }
                })
                .show();
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
        else{
            String name = user.getDisplayName();
            if (name != null)
                toolbarUser.setText(name);
        }
    }

    public void verSuscriptos(EditViajeViewHolder viajeViewHolder, Viaje viaje, String k){
        Map<String, Integer> suscriptos = viaje.getSuscriptos();
        if (suscriptos!=null){
            if (suscriptos.isEmpty()){
                Log.i("Suscriptos: ", "no hay");
                Snackbar.make(findViewById(R.id.listar_layout), "Aún no tenés suscriptos", Snackbar.LENGTH_LONG).show();
            }
            else{
                Log.i("LOS SUSCRIPTOS SON: ", suscriptos.toString());
                listarSuscritos(viajeViewHolder, suscriptos);
            }

        }

    }

    public void listarSuscritos(EditViajeViewHolder viajeViewHolder, Map<String, Integer> suscriptos){
        final TextView suscriptosText = viajeViewHolder.getTextSuscriptos();

        //Itero sobre el mapeo de suscritos al viaje (idUsuario,cantsuscripciones)
        for (Map.Entry<String, Integer> entry : suscriptos.entrySet()) {
            String idSuscripto = entry.getKey();
            final String cantLugares = entry.getValue().toString();

            suscriptosText.setText(""); //Vacio el campo de texto
            suscriptosText.setVisibility(View.VISIBLE);

            //Obtengo datos del usuario con id en el elemento actual del mapeo
            database.getReference().child("usuarios/"+idSuscripto)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           User user = dataSnapshot.getValue(User.class);
                            String suscriptoAMostrar = "\n"+ user.getNombre()+" \n@ "+user.getMail()+" \n☎ "+ user.getTelefono()+" \nLugares reservados: "+cantLugares+"\n";
                            suscriptosText.setText(suscriptosText.getText()+ suscriptoAMostrar);
                        }
                        public void onCancelled(DatabaseError error) {
                            Log.e("ERROR FIREBASE", error.getMessage());
                        }

                    });
        }
    }

    public void editarViaje(EditViajeViewHolder viajeViewHolder, Viaje v, String k){
        final TextView lblDireccion = viajeViewHolder.getLblDireccion();
        lblHora = viajeViewHolder.getLblHora();
        final TextView lblFecha = viajeViewHolder.getLblFecha();
        final EditText lblInfo = viajeViewHolder.getLblInfo();
        final String key = k;
        final Viaje viaje = v;
        final TextView suscriptosAViaje = viajeViewHolder.getTextSuscriptos();

        final ImageButton botonGuardar = viajeViewHolder.getBotonGuardar();
        final ImageButton botonEditar = viajeViewHolder.getBotonEditar();
        final Button botonVerSuscriptos = viajeViewHolder.getBotonVerSuscriptos();

        lblDireccion.setEnabled(true);
        lblInfo.setEnabled(true);
        lblHora.setEnabled(true);
        lblHora.setClickable(true);
        lblFecha.setEnabled(true);
        botonGuardar.setVisibility(View.VISIBLE);
        botonEditar.setEnabled(false);

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String hs = lblHora.getText().toString();
                String direccion = lblDireccion.getText().toString();
                String fecha = lblFecha.getText().toString();
                String info = lblInfo.getText().toString();

                viaje.setDireccion(direccion);
                viaje.setHora(hs);
                viaje.setFecha(fecha);
                viaje.setInformacion(info);

                if(validar(hs, info, direccion) ){
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/" + key , viaje);
                    mDataBase.updateChildren(childUpdates);

                    ValueEventListener postListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Snackbar.make(findViewById(R.id.listar_layout), "Los cambios fueron realizados con exito.", Snackbar.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Snackbar.make(findViewById(R.id.listar_layout), "Lamentamos que los cambios no pudieron ser guardados.", Snackbar.LENGTH_SHORT).show();
                        }
                    };
                    mDataBase.addValueEventListener(postListener);

                final AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);

                builder.setMessage("¿Desea informar el cambio a los viajantes de su viaje?")
                        .setTitle("Confirmación")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String subject = "Modificación de viaje";
                                String message = "Hola. Te informamos que se ha efectuado una modificacion en un viaje al que te has suscripto.";

                                sendEmails(subject, message, obtenerMailsSuscriptos(viaje.getSuscriptos()));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                    lblDireccion.setEnabled(false);
                    lblInfo.setEnabled(false);
                    lblHora.setEnabled(false);
                    lblFecha.setEnabled(false);
                    botonGuardar.setVisibility(View.INVISIBLE);
                    botonEditar.setEnabled(true);
                    lblHora.setClickable(false);
                }


            }
        });
    }

     public void eliminarViaje(String k){
        final String key = k;

        final AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);

        builder.setMessage("¿Está seguro de que quiere eliminar este viaje?")
                .setTitle("Confirmación")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.i("Dialogos", "confirmado.");
                        //eliminar el viaje
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/" + key , null);
                        mDataBase.updateChildren(childUpdates);

                        Snackbar.make(findViewById(R.id.lytContenedor), "Su viaje ha sido eliminado con éxito.", Snackbar.LENGTH_SHORT).show();

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

        //TODO mandar mails para avisar a los pasajeros
         final AlertDialog.Builder builder2 = new AlertDialog.Builder(ListarViajesActivity.this);

         builder.setMessage("¿Desea informar el cambio a los viajantes de su viaje?")
                 .setTitle("Confirmación")
                 .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         String subject = "Cancelación de viaje";
                         String message = "Hola. Te informamos que se ha cancelado un viaje al que te has suscripto.";
                         String to = "mlevisrossi@gmail.com";//TODO mails de los usuarios suscriptos al viaje
                         sendEmails(subject, message, to);
                     }
                 })
                 .setNegativeButton("No", new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         dialog.cancel();
                     }
                 });
         AlertDialog dialog2 = builder2.create();
         dialog.show();
    }

    public void sendEmails(String subject, String message, String to) {

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);

        //need this to prompts email client only
        email.setType("message/rfc822");

        startActivity(Intent.createChooser(email, "Seleccione un cliente de mail:"));
    }

    public String obtenerMailsSuscriptos(Map<String, Integer> suscriptos){
        final TextView mailsSuscriptos = findViewById(R.id.mails_suscriptos);
        mailsSuscriptos.setText("");

        for (Map.Entry<String, Integer> entry : suscriptos.entrySet()) {
            String idSuscripto = entry.getKey();
            //Obtengo mail del usuario con id en el elemento actual del mapeo
            database.getReference().child("usuarios/"+idSuscripto)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            Log.i("MAILS", "agrego "+user.getMail());
                            mailsSuscriptos.setText(mailsSuscriptos.getText()+";"+user.getMail());
                        }
                        public void onCancelled(DatabaseError error) {
                            Log.e("ERROR FIREBASE", error.getMessage());
                        }
                    });
        }
        String mails = mailsSuscriptos.getText().toString();
        Log.i("MAILS ", "(termine) "+mails);
        return mails;
    }


    public void showTimePickerDialog(View view) {
        Log.d("asd", "clickie en hora");
        TimePickerDialog timeDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hour, int minutes) {
                final String selectedTime = twoDigits(hour) + ":" + twoDigits(minutes);
                lblHora.setText(selectedTime);
                //horaViaje=hour; minViaje=minutes;
            }
        },hora, minuto, true);
        //Muestro el widget
        timeDialog.show();
    }

    private String twoDigits(int n) {
        return (n<=9) ? ("0"+n) : String.valueOf(n);
    }

    public boolean validar(String hora, String informacion, String direccion) {
        boolean esValido= true;
        // Hora is required
        if (TextUtils.isEmpty(hora)) {
            esValido= false;
        }

        //Si no hay informacion extra le asigno un guion (no es campo requerido)
        if (TextUtils.isEmpty(informacion)) {
            informacion = " No hay información adicional.";
        }

        if (TextUtils.isEmpty(direccion)) {
            //origenAutocomplFrag.
            esValido= false;
        }

        if(!esValido){
            AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);
            builder.setMessage("La hora y direccion no pueden guardarse vacios.").setTitle("Error");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return esValido;
    }


}
