package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText inputNombre, inputApellido, inputDireccion, inputLocalidad, inputFoto;
    private Button btnEditProfile;

    private Button btnChangeEmail, btnChangePassword, btnSendResetEmail, changeEmail, changePassword, sendEmail;
    private EditText oldEmail, newEmail, password, newPassword;


    //Variables para registro de usuario
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    //Variable para guardar mail identificador del usuario actual
    private String usuario;
    //Obtener usuario actual
    private FirebaseUser user;

    //Sign in con Google
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInGoogle;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Obtengo datos pasados de la actividad anterior
        usuario = getIntent().getStringExtra("usuario");

        progressBar = findViewById(R.id.progressBar);

        btnEditProfile = findViewById(R.id.edit_profile_button);

        inputNombre = (EditText) findViewById(R.id.nombre);
        inputApellido = (EditText) findViewById(R.id.apellido);
        inputDireccion = (EditText) findViewById(R.id.direccion);
        inputLocalidad = (EditText) findViewById(R.id.localidad);
        inputFoto = (EditText) findViewById(R.id.foto);

        //Inicializo los campos del layout
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
                                        Toast.makeText(UserProfileActivity.this, "Email address is updated. Please sign in with new email id!", Toast.LENGTH_LONG).show();
                                        signOut();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
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
            public void onClick(View v) {
                oldEmail.setVisibility(View.GONE);
                newEmail.setVisibility(View.GONE);
                password.setVisibility(View.GONE);
                newPassword.setVisibility(View.VISIBLE);
                changeEmail.setVisibility(View.GONE);
                changePassword.setVisibility(View.VISIBLE);
                sendEmail.setVisibility(View.GONE);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newPassword.getText().toString().trim().equals("")) {
                    if (newPassword.getText().toString().trim().length() < 6) {
                        newPassword.setError("Password too short, enter minimum 6 characters");
                        progressBar.setVisibility(View.GONE);
                    } else {
                        user.updatePassword(newPassword.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(UserProfileActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                            signOut();
                                            progressBar.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
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
            }
        });

        sendEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (!oldEmail.getText().toString().trim().equals("")) {
                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(UserProfileActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(UserProfileActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
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
    }

    public void deleteAccount(){
        // Google sign out
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.revokeAccess();
        }
        progressBar.setVisibility(View.VISIBLE);
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserProfileActivity.this, "Su cuenta se elimino:( Crea una nueva ahora!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UserProfileActivity.this, SignUpActivity.class));
                                //----------------ELIMINAR DE LA BASE DE DATOS EL USER
                                finish();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(UserProfileActivity.this, "Error al eliminar la cuenta!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }

    }

    //Al comenzar actividad verifico que este logueado
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UserProfileActivity.this, SignInActivity.class));
            finish();
        }
    }

    public void signOut() {
        auth.signOut();

        // Google sign out
        if(mGoogleSignInClient!=null){
            mGoogleSignInClient.signOut();
        }
    }
}
