package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.User;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputNombre, inputApellido, inputDireccion, inputLocalidad, inputFoto, inputTel;
    private Button btnSignIn, btnSignUp;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    private DatabaseReference mDatabase;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = findViewById(R.id.sign_in_button);
        btnSignUp = findViewById(R.id.sign_up_button);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputNombre = (EditText) findViewById(R.id.nombre);
        inputApellido = (EditText) findViewById(R.id.apellido);
        inputDireccion = (EditText) findViewById(R.id.direccion);
        inputLocalidad = (EditText) findViewById(R.id.localidad);
        inputFoto = (EditText) findViewById(R.id.foto);
        inputTel = (EditText) findViewById(R.id.tel);

        progressBar = findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String mail = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String nombre = inputNombre.getText().toString().trim();
                String apellido = inputApellido.getText().toString().trim();
                String direccion = inputDireccion.getText().toString().trim();
                String localidad = inputLocalidad.getText().toString().trim();
                String foto = inputFoto.getText().toString().trim();
                String tel = inputTel.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), "Ingrese email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Ingrese contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Contraseña demasiado corta, ingrese al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(nombre)) {
                    Toast.makeText(getApplicationContext(), "Ingresar nombre!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(apellido)) {
                    Toast.makeText(getApplicationContext(), "Ingresar apellido!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(direccion)) {
                    Toast.makeText(getApplicationContext(), "Ingresar direccion!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(localidad)) {
                    Toast.makeText(getApplicationContext(), "Ingresar localidad!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });

                //-------------Crear nuevo User en la BD------------------
                //Instanciacion de la base de datos
                mDatabase = FirebaseDatabase.getInstance().getReference();

                if (!URLUtil.isValidUrl(foto))
                    foto = "http://www.prevenciondelaviolencia.org/sites/all/themes/pcc/images/user.png";

                User user = new User(nombre, apellido, direccion, localidad, foto, mail, tel);
                String key = mDatabase.child("users").push().getKey();

                mDatabase.child("users").child(tel).setValue(user, new DatabaseReference.CompletionListener(){
                    //El segundo parametro es para recibir un mensaje si hubo error en el setValue
                    public void onComplete(DatabaseError error, DatabaseReference ref) {
                        if(error == null){
                            Log.i("Success", "Creado con exito");
                        }
                        else{
                            Log.e("Error", "Error: " + error.getMessage());
                        }
                    }
                });
            }
        });
    }

    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
