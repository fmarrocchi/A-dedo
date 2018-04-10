package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.EditViajeViewHolder;
import com.proyectos.florm.a_dedo.Holders.MisSuscripcionesViewHolder;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.Viaje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListarViajesActivity extends BaseActivity {

    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recycler;
    int contViajes;
    String destino;
    String fecha;
    String usuario;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_viajes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mostrarViajes();
    }

    public FirebaseRecyclerAdapter adapterBuscar(){
        contViajes=0;
        String origen = getIntent().getExtras().getString("origen");
        destino = getIntent().getExtras().getString("destino");
        fecha = getIntent().getExtras().getString("fecha");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBase.orderByChild("origen").equalTo(origen)) {
                    public void populateViewHolder(final ViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if ((viaje.getDestino().equals(destino)) && (viaje.getFecha().equals(fecha))) {
                            contViajes++;

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

                            viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
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
        contViajes=0;
        String conductor = getIntent().getExtras().getString("conductor");
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, EditViajeViewHolder>(Viaje.class, R.layout.listitem_editar_viaje, EditViajeViewHolder.class, mDataBase.orderByChild("conductor").equalTo(conductor)) {
                    public void populateViewHolder(final EditViajeViewHolder viajeViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();
                            contViajes++;

                            viajeViewHolder.setOrigen(" " + viaje.getOrigen());
                            viajeViewHolder.setDestino(" " + viaje.getDestino());
                            viajeViewHolder.setFecha(" " + viaje.getFecha());
                            viajeViewHolder.setHora(" " + viaje.getHora() + " hs");
                            viajeViewHolder.setLugares(" " + viaje.getLugares());
                            viajeViewHolder.setDireccion(" "+ viaje.getDireccion());
                            //viajeViewHolder.setInformacion(" " + viaje.getInformacion()); //TODO TIRA NULL POINTER EXCEPTION ACA NO SE PQ

                            final String list_viaje_id  = getRef(position).getKey();

//                            viajeViewHolder.getView().setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//                                    viajeViewHolder.masInfo();
//                                }
//                            });

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
                    }
                };

        return adapter;

    }

    public FirebaseRecyclerAdapter adapterMisSuscripciones(){
        contViajes=0;
        usuario = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseRecyclerAdapter adapter =
                new FirebaseRecyclerAdapter<Viaje, MisSuscripcionesViewHolder>(Viaje.class, R.layout.listitem_missuscripciones, MisSuscripcionesViewHolder.class, mDataBase) {
                    public void populateViewHolder(final MisSuscripcionesViewHolder suscripcionesViewHolder, final Viaje viaje, int position) {
                        final String itemId = getRef(position).getKey();

                        if (viaje != null && viaje.getSuscriptos()!= null ){
                            if (viaje.getSuscriptos().containsKey(usuario))
                                Log.i("DESUSCRIBIR", "usuario esta: "+usuario);
                            else
                                Log.i("DESUSCRIBIR", "usuario no esta: "+usuario);
                            //TODO CAMBIAR SI CAMBIA LA FORMA DE LA LISTA DE SUSCRIPTOS
                            contViajes++;

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
                            final String list_viaje_id  = getRef(position).getKey();

                            suscripcionesViewHolder.getView().setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    suscripcionesViewHolder.masInfo();
                                }
                            });
                        }
                        else
                            Log.i("DESUSCRIBIR", "algo es nulo");
                    }
                };

        return adapter;

    }

    private void mostrarViajes(){
        //Inicialización RecyclerView
        recycler = findViewById(R.id.listaViajes);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        String opcion = getIntent().getExtras().getString("opcion");
        if(opcion.equals("buscar"))
            adapter = adapterBuscar();
        else
            if(opcion.equals("misviajes"))
                adapter = adapterMisViajes();
            else
                if (opcion.equals("missuscripciones")){
                    Log.i("DESUSCRIBIR", "voy a llamar al adapter");
                    adapter = adapterMisSuscripciones();
                }


        recycler.setAlpha(0.90f); //Dar transparencia
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
    }

    public void editarViaje(EditViajeViewHolder viajeViewHolder, Viaje v, String k){
        final EditText lblDireccion = viajeViewHolder.getLblDireccion();
        final EditText lblHora = viajeViewHolder.getLblHora();
        final EditText lblFecha = viajeViewHolder.getLblFecha();
        final EditText lblInfo = viajeViewHolder.getLblInfo();
        final String key = k;
        final Viaje viaje = v;

        final Button botonGuardar = viajeViewHolder.getBotonGuardar();
        final Button botonEditar = viajeViewHolder.getBotonEditar();

        lblDireccion.setEnabled(true);
        lblInfo.setEnabled(true);
        lblHora.setEnabled(true);
        lblHora.setClickable(true);
        lblFecha.setEnabled(true);
        botonGuardar.setVisibility(View.VISIBLE);
        botonEditar.setEnabled(false);

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //guardarCambios(key, lblDireccion.getText(), lblFecha.getText(), lblHora.getText());

                viaje.setDireccion(lblDireccion.getText().toString());
                viaje.setHora(lblHora.getText().toString());
                viaje.setFecha(lblFecha.getText().toString());
                viaje.setInformacion(lblInfo.getText().toString());

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/" + key , viaje);
                mDataBase.updateChildren(childUpdates);

                lblDireccion.setEnabled(false);
                lblInfo.setEnabled(false);
                lblHora.setEnabled(false);
                lblFecha.setEnabled(false);
                botonGuardar.setVisibility(View.INVISIBLE);
                botonEditar.setEnabled(true);
                lblHora.setClickable(false);

                final AlertDialog.Builder builder = new AlertDialog.Builder(ListarViajesActivity.this);

                builder.setMessage("¿Desea informar el cambio a los viajantes de su viaje?")
                        .setTitle("Confirmación")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String subject = "Modificación de viaje";
                                String message = "Hola. Te informamos que se ha efectuado una modificacion en un viaje al que te has suscripto.";
                                String to = "mlevisrossi@gmail.com";//TODO mails de los usuarios suscriptos al viaje
                                sendEmails(subject, message, to);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

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


}
