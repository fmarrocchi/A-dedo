package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
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

public class MainActivity extends BaseActivity
        implements View.OnClickListener {

    private TextView txtTitulo;
    //Variables para registro de usuario
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    //Obtener usuario actual
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authListener;
    //Sign in con Google
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInGoogle;

    private TabLayout tabLayout;
    private TabItem tabItemInicio, tabItemMisViajes;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference referenceUsuarios = database.getReference("usuarios");

    String key;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTitulo = findViewById(R.id.txtTitulo);

        findViewById(R.id.buttonListarViajes).setOnClickListener(this);
        findViewById(R.id.buttonVerPerfil).setOnClickListener(this);

        tabItemInicio = findViewById(R.id.tab_item_inicio);
        tabItemMisViajes = findViewById(R.id.tab_item_misviajes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    //sign out method
    public void signOut() {
        auth.signOut();
        // Google sign out
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }
        startActivity(new Intent(this, MainActivity.class));//Refrescar
    }

    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }

    //Metodo para verificar si el usuario esta logueado antes de comenzar la actividad
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user != null) {
            txtTitulo.setText("Bienvenido "+ user.getDisplayName());
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
        int id = item.getItemId();
        switch (item.getItemId()) {
              case R.id.menu_crear_viaje:
                //Creamos el Intent de la clase viajes, pasando como dato el mail del usuario autenticado
                Intent intent = new Intent(MainActivity.this, ViajeActivity.class);
                intent.putExtra("usuario", user.getUid().toString());
                //Iniciamos la nueva actividad
                startActivity(intent);
                return true;
            case R.id.menu_configuracion:
                //Creamos el Intent de la clase viajes, pasando como dato el mail del usuario autenticado
                intent = new Intent(MainActivity.this, UserProfileActivity.class);
                intent.putExtra("usuario", user.getEmail().toString());
                //Iniciamos la nueva actividad
                startActivity(intent);
                return true;
            case R.id.menu_cerrar_sesion:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonListarViajes:
                //Creamos el Intent de listar viajes
                Intent intent = new Intent(this,  ListarViajesActivity.class);
                intent.putExtra("opcion", "misviajes");
                intent.putExtra("conductor", user.getUid().toString());
                //Iniciamos la nueva actividad
                startActivity(intent);
                break;
            case R.id.buttonVerPerfil:
                //Creamos el Intent de la clase viajes
                intent = new Intent(MainActivity.this,  UserProfileActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
                break;
            case R.id.buttonSuscripciones:
                //Creamos el Intent de la clase viajes
                intent = new Intent(MainActivity.this,  ListarViajesActivity.class);
                intent.putExtra("usuario", user.getEmail()); //TODO CAMBIAR A ID SI LA LISTA DE SUSCRIPTOS PASA A SER DE IDS
                intent.putExtra("opcion", "suscripciones");
                //Iniciamos la nueva actividad
                startActivity(intent);
                break;
            case R.id.tab_item_inicio:
                txtTitulo.setText("Inicio");
                break;
            case R.id.tab_item_misviajes:
                txtTitulo.setText("Mis viajes");
                break;
        }
    }

    public void buscarViaje(View view){
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void verificarNumeroTelefono(String key){
        final DatabaseReference referenceUser = database.getReference("usuarios/"+key);
        referenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null && user.getTelefono() == null){
                    //Dialogo para pedir ingresar numero de telefono
                    AlertDialog.Builder dialog = telefonoDialog(user);
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

        final EditText textoTel = new EditText(this);
        builder.setTitle("Numero de teléfono")
                .setView(textoTel)
                .setIcon(R.drawable.icono_tel)
                .setMessage("Por favor ingresá tu número de teléfono para poder contactarte con viajeros y que ellos puedan contactarse con vos");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String tel = textoTel.getText().toString();
                if (TextUtils.isEmpty(tel)) {
                    textoTel.setError("Requerido");
                }
                else{
                    Log.i("texto escrito por us", tel);
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
