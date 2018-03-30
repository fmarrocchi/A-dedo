package com.proyectos.florm.a_dedo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.proyectos.florm.a_dedo.Models.User;
import com.google.firebase.database.FirebaseDatabase;

import android.support.design.widget.Snackbar;


public class SignInActivity extends BaseActivity implements
        View.OnClickListener {
    private EditText inputEmail, inputPassword;
    private ProgressBar progressBar;
    private Button btnReset;
    private SignInButton btnSignup, btnLogin;

    private FirebaseAuth auth;

    //Sign in con Google
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton btnSignInGoogle;

    private static final int RC_SIGN_IN = 9001;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Vistas
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Listeners de botones
        findViewById(R.id.btn_signup).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);
        findViewById(R.id.btn_reset_password).setOnClickListener(this);
        findViewById(R.id.sign_in_google_button).setOnClickListener(this);

        //Configurar Sign In con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_signup:
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            case R.id.btn_reset_password:
                startActivity(new Intent(SignInActivity.this, ResetPasswordActivity.class));
            case R.id.btn_login:
                signIn();
            case R.id.sign_in_google_button:
                 signInGoogle();
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
        progressBar.setVisibility(View.VISIBLE);
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError("Contraseña demasiado corta, ingrese al menos 6 caracteres!");
                            } else {
                                Toast.makeText(SignInActivity.this, "Autenticación fallida, revise su correo electrónico y contraseña o regístrese", Toast.LENGTH_LONG).show();
                            }
                        }else {
                       //     Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                       //     startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void signInGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Sign In GOOGLE", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Sign In GOOGLE", "firebaseAuthWithGoogle:" + acct.getId());
        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Sign In GOOGLE", "Sign In Con credenciales GOOGLE: exitoso");
                            FirebaseUser user = auth.getCurrentUser();
                            //createUser(user);
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Sign In GOOGLE", "Sign In Con credenciales GOOGLE: FALLA", task.getException());
                            Snackbar.make(findViewById(R.id.main_layout), "Autenticación fallida.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void updateUI(FirebaseUser user){
        hideProgressDialog();
        if (user != null) {
           // startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private void createUser(FirebaseUser googleUser){
        //Instanciacion de la base de datos
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        String mail = googleUser.getEmail();
        /*User user = new User(mail);
        mDatabase.child("users").child(mail).setValue(user, new DatabaseReference.CompletionListener(){
            //El segundo parametro es para recibir un mensaje si hubo error en el setValue
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if(error == null){
                    Log.i("Success", "Creado con exito");
                }
                else{
                    Log.e("Error", "Error: " + error.getMessage());
                }
            }
        });*/
    }
}

