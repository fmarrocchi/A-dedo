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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.User;

public class SignUpActivity extends BaseActivity {
    private EditText inputEmail, inputPassword, inputPasswordConfirm, inputNombre, inputApellido, inputFoto, inputTel;
    private Button btnSignIn, btnSignUp;
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
        inputPasswordConfirm = findViewById(R.id.password_confirm);
        inputNombre = findViewById(R.id.nombre);
        inputApellido = findViewById(R.id.apellido);
        inputFoto = findViewById(R.id.foto);
        inputTel = findViewById(R.id.telefono);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
           public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final String mail = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String passwordConfirm = inputPasswordConfirm.getText().toString().trim();
                // TODO volver a ingresar password y comaprar
                final String nombre = inputNombre.getText().toString().trim();
                final String apellido = inputApellido.getText().toString().trim();
                final String foto = inputFoto.getText().toString().trim();
                final String tel = inputTel.getText().toString().trim();

                if (TextUtils.isEmpty(mail)) {
                    Toast.makeText(getApplicationContext(), "Ingrese email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Ingrese contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordConfirm)) {
                    Toast.makeText(getApplicationContext(), "Confirmar contraseña", Toast.LENGTH_SHORT).show();
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
                if (TextUtils.isEmpty(tel)) {
                    Toast.makeText(getApplicationContext(), "Ingresar telefono!", Toast.LENGTH_SHORT).show();
                    return;
                }
              //TODO insertar foto y mostrarla
                /* if (!URLUtil.isValidUrl(foto))
                    foto = "http://www.prevenciondelaviolencia.org/sites/all/themes/pcc/images/user.png";*/

                if (!password.equals(passwordConfirm)){
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog();

                //create authentication
                auth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                hideProgressDialog();
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    FirebaseUser user = auth.getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(nombre+" "+ apellido).build();
                                    user.updateProfile(profileUpdates);
                                    Log.i("USER","agregue nombre");

                                    crearUserBD(mail, nombre+" "+apellido, tel, foto);

                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    private void crearUserBD(String mail, String nombre, String tel, String foto){
         //Instanciacion de la base de datos
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String key = auth.getCurrentUser().getUid();

        User user = new User(mail, nombre, tel, foto);
        mDatabase.child("usuarios").child(key).setValue(user, new DatabaseReference.CompletionListener(){
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
}
