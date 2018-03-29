package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Holders.ViajeViewHolder;
import com.proyectos.florm.a_dedo.Models.Viaje;

public class MainActivity extends BaseActivity
        implements View.OnClickListener {

    private TextView txtTitulo;
    private Button buttonListarViajes, buttonVerPerfil;
    private ProgressBar progressBar;
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

    //Variables para acceder a la BD
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView recycler;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBaseViajes = database.getReference().child("viajes");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTitulo = (TextView)findViewById(R.id.txtTitulo);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.buttonListarViajes).setOnClickListener(this);
        findViewById(R.id.buttonVerPerfil).setOnClickListener(this);
/*
        //Mica: ESTOY HACIENDO ESTO JAJA COMENTE PQ NO ANDA TODAVIA

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(
                getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(viewPager);
*/
        tabItemInicio = (TabItem) findViewById(R.id.tab_item_inicio);
        tabItemMisViajes = (TabItem) findViewById(R.id.tab_item_misviajes);

        recycler = (RecyclerView) findViewById(R.id.listaViajes);
        recycler.setOnClickListener(this);
        recycler.setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

    }

    //sign out method
    public void signOut() {
        auth.signOut();
        // Google sign out
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }
    }

    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    //Metodo para verificar si el usuario esta logueado antes de comenzar la actividad
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user != null) {
            txtTitulo.setText("Bienvenido "+ user.getDisplayName());
        } else {
            //Si no esta logueado voy a la actividad de login
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
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
                intent.putExtra("usuario", user.getEmail().toString());
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
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this,  ListarViajesActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
                break;
            case R.id.buttonVerPerfil:
                //Creamos el Intent de la clase viajes
                intent = new Intent(MainActivity.this,  UserProfileActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
                break;
            case R.id.tab_item_inicio:
                txtTitulo.setText("Inicio");
                mostrarViajes();
                break;
            case R.id.tab_item_misviajes:
                txtTitulo.setText("Mis viajes");
        }
    }

    private void mostrarViajes(){

        //Inicialización RecyclerView
        recycler.setVisibility(View.VISIBLE);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        mAdapter =
                new FirebaseRecyclerAdapter<Viaje, ViajeViewHolder>(
                        Viaje.class, R.layout.listitem_viaje, ViajeViewHolder.class, mDataBaseViajes) {

                    public void populateViewHolder(ViajeViewHolder viajeViewHolder, Viaje viaje, int position) {
                        viajeViewHolder.setDestino(viaje.getDestino()+ " - ");
                        viajeViewHolder.setSalida(viaje.getSalida());
                        viajeViewHolder.setFecha(viaje.getFecha());
                        viajeViewHolder.setHora(viaje.getHora()+ " hs");
                        viajeViewHolder.setPasajeros(viaje.getPasajeros().toString());
                    }
                };
        recycler.setAdapter(mAdapter);
    }
}
