package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabItem;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Models.User;

public class MainActivity extends BaseActivity {

    //Variables para registro de usuario
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    //Obtener usuario actual
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    //Sign in con Google
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInGoogle;
    private TabItem tabItemInicio, tabItemMisViajes;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference referenceUsuarios = database.getReference("usuarios");
    Button crear, buscar;
    String key;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        crear = findViewById(R.id.boton_crea);
        crear.setAlpha(0.70f); //Dar transparencia
        buscar = findViewById(R.id.boton_busca);
        buscar.setAlpha(0.70f);

    }

    public void crear(View v){
        crearViaje();
    }

    public void buscar(View v){
        buscarViaje();
    }

    //Metodo para verificar si el usuario esta logueado antes de comenzar la actividad
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user != null) {
            key = user.getUid();
            verificarNumeroTelefono(key);
        } else {
            //Si no esta logueado voy a la actividad de login
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
        }
    }

    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MIS VIAJES","estoy en el menu");
        switch (item.getItemId()) {
            case R.id.menu_crear_viaje:
                crearViaje();
                return true;
            case R.id.menu_buscar_viaje:
                Log.i("MIS VIAJES","menu-buscAR");
                buscarViaje();
                return true;
            case R.id.menu_mis_suscripciones:
                Log.i("MIS VIAJES","menu-suscripciones");
                misSuscripciones();
                return true;
            case R.id.menu_mis_viajes:
                Log.i("MIS VIAJES","menu");
                misViajes();
                return true;
            case R.id.menu_cerrar_sesion:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void crearViaje(){
        //Creamos el Intent de la clase viajes, pasando como dato el mail del usuario autenticado
        Intent intent = new Intent(MainActivity.this, ViajeActivity.class);
        intent.putExtra("usuario", user.getUid().toString());
        //Iniciamos la nueva actividad
        startActivity(intent);
    }

     public void buscarViaje(){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void signOut() {
        auth.signOut();
        // Google sign out
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }
        startActivity(new Intent(this, MainActivity.class));//Refrescar
    }

    public void misViajes(){
        Intent intent = new Intent(this,  ListarViajesActivity.class);
        intent.putExtra("opcion", "misviajes");
        intent.putExtra("conductor", user.getUid().toString());
        //Iniciamos la nueva actividad
        startActivity(intent);
    }

    public void misSuscripciones(){
        Intent intent = new Intent(this,  ListarViajesActivity.class);
        intent.putExtra("opcion", "missuscripciones");
        intent.putExtra("conductor", user.getUid().toString());
        //Iniciamos la nueva actividad
        startActivity(intent);
    }

    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    public void verificarNumeroTelefono(String key){
        final DatabaseReference referenceUser = database.getReference("usuarios/"+key);
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null)
                    Log.i("TELEFONO","usuario nulo");
                else
                if (user.getTelefono() == null){
                    Log.i("TELEFONO","pido telefono");
                    //Dialogo para pedir ingresar numero de telefono
                    AlertDialog.Builder dialog = telefonoDialog(user);
                    dialog.setCancelable(false);
                    dialog.show();
                }
                else
                    Log.i("TELEFONO","no es nulo");

            }
            public void onCancelled(DatabaseError databaseError) {      }
        });
    }

    private AlertDialog.Builder telefonoDialog(final User user){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        //obtiene la vista en la cual se buscaran los elementos.
        View dialogView = inflater.inflate(R.layout.dialogo_telefono, null);
        //builder.setView(R.layout.dialog_clientes);
        builder.setView(dialogView);

        final EditText textTel = (EditText) dialogView.findViewById(R.id.text_telefono);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.i("TELEFONO", "id: "+id);
                String tel = textTel.getText().toString();
                if (TextUtils.isEmpty(tel)) {
                    textTel.setError("Requerido");
                    Log.i("TELEFONO", "no ingresado");
                }
                else{
                    Log.i("TELEFONO", "a setear");
                    setearTelefono(user, tel);
                }
            }
        });
        return builder;
    }

    private void setearTelefono(final User user, final String tel){
        user.setTelefono(tel);
        final DatabaseReference referenceUser = database.getReference("usuarios/"+key);
        referenceUser.setValue(user, new DatabaseReference.CompletionListener(){
            //El segundo parametro es para recibir un mensaje si hubo error en el setValue
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if(error == null){
                    Log.i("Success", "Setear telefono. Usuario modificado con exito");
                }
                else{
                    Log.e("Error", "Setear telefono. Error al modificar usuario: " + error.getMessage());
                }
            }
        });
    }
}
