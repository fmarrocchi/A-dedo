package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Models.User;
import android.support.design.widget.Snackbar;


public class SignInActivity extends BaseActivity implements
        View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private Button btnReset;
    private SignInButton btnSignup, btnLogin;
    private SignInButton btnSignInGoogle;
    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        inputEmail = findViewById(R.id.email);
        inputEmail.setAlpha(0.75f);
        inputPassword = (EditText) findViewById(R.id.password);
        inputPassword.setAlpha(0.75f);

        //Listeners de botones
        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_reset_password).setOnClickListener(this);

        btnSignInGoogle = findViewById(R.id.sign_in_google_button);
        btnSignInGoogle.setOnClickListener(this);
        btnSignInGoogle.setAlpha(0.65f);

        auth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                break;
            case R.id.btn_reset_password:
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.btn_login:
                signIn();
                break;
            case R.id.sign_in_google_button:
                startActivity(new Intent(SignInActivity.this, SignInGoogleActivity.class));
                break;
        }
    }

    public void onStart() {
        super.onStart();
        updateUI(auth.getCurrentUser());
    }

    private void signIn(){
        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Ingresar direccion de email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Ingresar contraseña!", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            Log.i("AUTENTICADO","error");
                            Toast.makeText(SignInActivity.this, "Autenticación fallida, revise su correo electrónico y contraseña o regístrese", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Log.i("AUTENTICADO","sin errores");
                            finish();
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        hideProgressDialog();
        if (user != null) {
            finish();
        }
    }
}