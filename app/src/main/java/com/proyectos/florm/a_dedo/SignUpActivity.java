package com.proyectos.florm.a_dedo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.File;

public class SignUpActivity extends BaseActivity {
    private EditText inputEmail, inputPassword, inputPasswordConfirm, inputNombre, inputApellido, inputFoto, inputTel;
    private Button btnSignIn, btnSignUp;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    //Variables para la foto
    private AlertDialog _photoDialog;
    private Uri mImageUri;
    private static final int ACTIVITY_SELECT_IMAGE = 1020, ACTIVITY_SELECT_FROM_CAMERA = 1040, ACTIVITY_SHARE = 1030;
    private PhotoUtils photoUtils;
    private Button photoButton;
    private ImageView photoViewer;

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

        //Foto
        Boolean fromShare;
        photoUtils = new PhotoUtils(this);
        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                fromShare = true;
            } else
            if (type.startsWith("image/")) {
                fromShare = true;
                mImageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
                getImage(mImageUri);
            }
        }
        photoButton = (Button) findViewById(R.id.photoButton);
        photoViewer = (ImageView) findViewById(R.id.photoViewer);
        getPhotoDialog();

        photoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                if(!getPhotoDialog().isShowing() && !isFinishing())
                    getPhotoDialog().show();
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

    private AlertDialog getPhotoDialog() {
        if (_photoDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            builder.setTitle("Seleccionar foto desde..");
            builder.setPositiveButton("Camara", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    File photo = null;
                    try {
                        // place where to store camera taken picture
                        photo = PhotoUtils.createTemporaryFile("picture", ".jpg", SignUpActivity.this);
                        photo.delete();
                    } catch (Exception e) {
                        Log.v(getClass().getSimpleName(),
                                "Can't create file to take picture!");
                    }
                    mImageUri = Uri.fromFile(photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(intent, ACTIVITY_SELECT_FROM_CAMERA);
                }
            });
            builder.setNegativeButton("Galeria", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, ACTIVITY_SELECT_IMAGE);
                }

            });
            _photoDialog = builder.create();

        }
        return _photoDialog;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mImageUri != null)
            outState.putString("Uri", mImageUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("Uri")) {
            mImageUri = Uri.parse(savedInstanceState.getString("Uri"));
        }
    }

    public void getImage(Uri uri) {
        Bitmap bounds = photoUtils.getImage(uri);
        if (bounds != null) {
            setImageBitmap(bounds);
        } else {
            //showErrorToast();

        }
    }

    private void setImageBitmap(Bitmap bitmap){
        photoViewer.setImageBitmap(bitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_SELECT_IMAGE && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            getImage(mImageUri);
        } else if (requestCode == ACTIVITY_SELECT_FROM_CAMERA && resultCode == RESULT_OK) {
            getImage(mImageUri);
        }
    }
}
