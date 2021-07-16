package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import music.player.MusicPlayer;


public class Register extends AppCompatActivity {

    public static final int PICK_IMAGE_REQUEST = 1;
    EditText username, email, password;
    Spinner spinner;
    FirebaseAuth firebaseAuth;
    Button button;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    Button imageButton;
    //Button chooseButton;
    StorageTask storageTask;
    private Uri uuri;
    private Uri uriTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = (EditText) findViewById(R.id.username_text);
        email = (EditText) findViewById(R.id.email_text);
        password = (EditText) findViewById(R.id.password_text);
        spinner = (Spinner) findViewById(R.id.list_text);
        button = (Button) findViewById(R.id.signUp_id);
        imageButton = (Button) findViewById(R.id.upload_image_profile_id);
        //chooseButton = (Button) findViewById(R.id.choose_image_profile_id);
        storageReference = FirebaseStorage.getInstance().getReference().child("Uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");


        // getting the current instance of the databasse
        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String m = setEmail();
                final String p = setPassword();
                final String u = setUsername();
                final String c = setCountry();
                if (uuri == null || u == null || m == null || p == null || c == null)
                    Toast.makeText(Register.this, "You Must choose your Image", Toast.LENGTH_SHORT).show();
                else {
                    if (u == null) return;
                    if (m == null) return;
                    if (p == null) return;
                    if (c == null) return;
                    firebaseAuth.createUserWithEmailAndPassword(m, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = user.getUid();
                                try {
                                    User use = new User(u, m, c, uriTwo.toString(), uid);
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    String usid = firebaseUser.getUid();
                                    databaseReference.child(usid).setValue(use);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                startActivity(new Intent(getApplicationContext(), MusicPlayer.class));
                                finish();
                            } else
                                Toast.makeText(Register.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileChooser();
            }
        });

    }

    public String setUsername() {
        if (username.getText().toString().trim().isEmpty()) {
            username.setError("Username must not be empty");
            return null;
        } else return username.getText().toString().trim();
    }

    public String setCountry() {
        if (spinner.getSelectedItem().toString().equals("Choose Country")) {
            ((TextView) spinner.getChildAt(0)).setError("Must choose a country");
            return null;
        } else return spinner.getSelectedItem().toString();
    }


    public String setEmail() {

        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError("Email not valide");
            return null;
        } else return email.getText().toString().trim();
    }

    public String setPassword() {
        if (checkPass(password.getText().toString().trim()) == false) {
            password.setError("Password needs to contain at least : 1 Upper Case, 1 number");
            return null;
        } else return password.getText().toString().trim();
    }

    public boolean checkPass(String pass) {
        int counter_1 = 0;
        int counter_2 = 0;
        if (pass.length() < 6) return false;
        for (int i = 0; i < pass.length(); ++i) {
            if (Character.isUpperCase(pass.charAt(i)))
                counter_1++;
            if (Character.isDigit(pass.charAt(i)))
                counter_2++;
        }
        if (counter_1 >= 1 && counter_2 >= 1)
            return true;
        else return false;
    }


    public void fileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    //this methode will be called when we picked our file
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null && data.getData() != null) {
            uuri = data.getData();
            if (storageTask != null && storageTask.isInProgress()) {
                Toast.makeText(Register.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage(setUsername());
            }
        }
    }

    public void uploadImage(final String username) {
        if (uuri != null) {
            //unique name for uploaded file
            final StorageReference mStoarage = storageReference.child(username + "_image");
            storageTask = mStoarage.putFile(uuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStoarage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            setUriTwo(uri);
                        }
                    });
                    Toast.makeText(Register.this, "Upload successful", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Register.this, "Error during Upload", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(Register.this, "Upload in Progress...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setUriTwo(Uri uri) {
        uriTwo = uri;
    }

}
