package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private TextView txtTitulo;
    private Button buttonListarViajes, buttonVerPerfil;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference mDataBase = database.getReference().child("viajes");

    //Obtener usuario actual
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //Variables para registro de usuario
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, btnRemoveUser,
            changeEmail, changePassword, sendEmail, remove, signOut;
    private EditText oldEmail, newEmail, password, newPassword;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        txtTitulo = (TextView)findViewById(R.id.txtTitulo);


        //--------------------usuario--------------------
        authListener = new FirebaseAuth.AuthStateListener() {
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    //Si no esta logueado voy a la actividad de login
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                   finish();
                    //Si no esta logueado voy a la actividad de login
                    Log.i("USER", "NO ESTA LOGUEADO");
                }
                else {
                    Log.d("USER", "ESTA LOGUEADO");
                    txtTitulo.setText("Hola "+ user.getEmail());
                }
            }
        };

        //Si el usuario no es nulo la actividad no finaliza. Inicializo los campos del layout
        btnChangeEmail = findViewById(R.id.change_email_button);
        btnChangePassword = findViewById(R.id.change_password_button);
        btnSendResetEmail = findViewById(R.id.sending_pass_reset_button);
        changeEmail = findViewById(R.id.changeEmail);
        changePassword = findViewById(R.id.changePass);
        sendEmail = findViewById(R.id.send);

        oldEmail =  findViewById(R.id.old_email);
        newEmail =  findViewById(R.id.new_email);
        password = findViewById(R.id.password);
        newPassword = findViewById(R.id.newPassword);


        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        btnChangeEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.VISIBLE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.VISIBLE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);

                Log.i("EMAIL", "-------------- BOTON CHANGE EMAIL");
            }
        });

        changeEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("EMAIL", "-------------- CHANGE EMAIL");
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(newEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else if (newEmail.getText().toString().trim().equals("")) {
                    newEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
                remove.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                    }
                } else if (newPassword.getText().toString().trim().equals("")) {
                    newPassword.setError("Enter password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                oldEmail.setVisibility(View.VISIBLE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.GONE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.GONE);
                sendEmail.setVisibility(View.VISIBLE);
                remove.setVisibility(View.GONE);
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                } else {
                    oldEmail.setError("Enter email");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });



     /*   signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                signOut();
            }
        });}*/

        //-----------------viajes--------------------------
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

        buttonVerPerfil = (Button)findViewById(R.id.buttonVerPerfil);
        buttonVerPerfil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this,  UserProfileActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
            }
        });

    }

    public void deleteAccount(){
          progressBar.setVisibility(View.VISIBLE);
            if (user != null) {
                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Su cuenta se elimino:( Crea una nueva ahora!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, SignUpActivity.class));


     //----------------------FALTA ELIMINAR DE LA BASE DE DATOS------------------------------------

                                    finish();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(MainActivity.this, "Error al eliminar la cuenta!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
    }
    //sign out method
    public void signOut() {
        auth.signOut();
    }

    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
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
            case R.id.action_settings:
                Log.i("ActionBar", "Settings");
                return true;
            case R.id.MnuOpc1:
                //Creamos el Intent de la clase viajes
                Intent intent = new Intent(MainActivity.this, ViajeActivity.class);
                //Iniciamos la nueva actividad
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_eliminar_cuenta:
                deleteAccount();
                return true;
            case R.id.menu_cerrar_sesion:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
