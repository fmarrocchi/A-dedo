package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView lblEtiqueta;
    private TextView txtTitulo;
    private Button buttonSignIn;
    private Button buttonListarViajes;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txtTitulo = (TextView)findViewById(R.id.txtTitulo);
        lblEtiqueta = (TextView)findViewById(R.id.lblEtiqueta);

        buttonSignIn = (Button)findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
            }
        });

        buttonListarViajes = (Button)findViewById(R.id.buttonListarViajes);
        buttonListarViajes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this,  ListarViajesActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
            }
        });


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                Log.i("ActionBar", "Settings");
                return true;
            case R.id.MnuOpc1:
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this, ViajeActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
                return true;
            case R.id.MnuOpc2:
                Log.i("ActionBar", "Ver viajes");
                 return true;
            case R.id.MnuOpc3:
                Log.i("ActionBar", "Opcion 3");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




}
