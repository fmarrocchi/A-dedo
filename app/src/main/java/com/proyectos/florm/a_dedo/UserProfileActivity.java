package com.proyectos.florm.a_dedo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class UserProfileActivity extends AppCompatActivity {
    private EditText inputNombre, inputApellido, inputDireccion, inputLocalidad, inputFoto;
    private Button btnEditProfile;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnEditProfile = findViewById(R.id.edit_profile_button);

        inputNombre = (EditText) findViewById(R.id.nombre);
        inputApellido = (EditText) findViewById(R.id.apellido);
        inputDireccion = (EditText) findViewById(R.id.direccion);
        inputLocalidad = (EditText) findViewById(R.id.localidad);
        inputFoto = (EditText) findViewById(R.id.foto);
    }
}
