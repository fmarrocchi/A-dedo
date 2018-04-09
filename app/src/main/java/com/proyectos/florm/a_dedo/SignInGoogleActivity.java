package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.proyectos.florm.a_dedo.Models.User;

import java.util.HashMap;
import java.util.Map;

public class SignInGoogleActivity extends BaseActivity {

    //Sign in con Google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        //Configurar Sign In con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInGoogle();
    }


    private void signInGoogle(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.i("Sign In GOOGLE", "sin errores");
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
                            createAuthUser(user);
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
            //Retorno a laactividad anterior
            onBackPressed();
        }
    }

    private void createAuthUser(FirebaseUser googleUser){
        //Uso mismo id de auth para el usuario
        final String key = googleUser.getUid();
        final String mail = googleUser.getEmail();
        final String foto = googleUser.getPhotoUrl().toString();
        final String nombre = googleUser.getDisplayName();
        final String telefono = googleUser.getPhoneNumber();
        //Instanciacion de la base de datos
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usuarios/"+key);

        //Compruebo si existe usuario con id de auth en la bd
        //Creo usuario User
        User user = new User(mail, nombre, telefono, foto);
        crearUserEnBD(reference, key, user);

    }

    private void crearUserEnBD(final DatabaseReference reference, String key, final User user){
       reference.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null){
                    reference.setValue(user, new DatabaseReference.CompletionListener(){
                        //El segundo parametro es para recibir un mensaje si hubo error en el setValue
                        public void onComplete(DatabaseError error, DatabaseReference ref) {
                            if(error == null){
                                Log.i("Success", "Usuario creado con exito");
                            }
                            else{
                                Log.e("Error", "Error al crear usuario: " + error.getMessage());
                            }
                        }
                    });
                }
            }
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public AlertDialog createSimpleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SignInGoogleActivity.this);
        Log.i("Dialog", "cree");
        builder.setTitle("Titulo")
                .setMessage("El Mensaje para el usuario")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                                Log.i("Alert dialog", "Aceptar");
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                           public void onClick(DialogInterface dialog, int which) {
                               Log.i("Alert dialog", "Cancelar");
                            }
                        });
        return builder.create();
    }

}
