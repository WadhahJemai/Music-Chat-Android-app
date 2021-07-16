package com.example.praktikum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import music.player.MusicPlayer;

public class Login extends AppCompatActivity {
    TextView signUpText;
    TextView forgotPassword;
    EditText email;
    EditText password;
    Button button;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("Users")) {
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users");
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                User us = snapshot.getValue(User.class);
                                if (firebaseUser != null) {
                                    if (firebaseUser.getUid().equals(us.getId())) {
                                        Intent intent = new Intent(Login.this, MusicPlayer.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        signUpText = (TextView) findViewById(R.id.signUp_text);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSignUp = new Intent(Login.this, Register.class);
                startActivity(launchSignUp);
            }
        });
        email = (EditText) findViewById(R.id.email_id);
        password = (EditText) findViewById(R.id.pass_id);
        button = (Button) findViewById(R.id.signIn_id);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        forgotPassword = (TextView) findViewById(R.id.forgot_password_text);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lunchForgotPassword = new Intent(Login.this, ForgotPassword.class);
                startActivity(lunchForgotPassword);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = email.getText().toString().trim();
                String p = password.getText().toString().trim();

                //check if mail and password are empty
                if (m.isEmpty() && p.isEmpty()) {
                    email.setError("Must not be empty");
                    password.setError("Must not be empty");
                    return;
                }
                //check email is empty
                if (m.isEmpty() && !p.isEmpty()) {
                    email.setError("Must not be empty");
                    return;

                }
                if (p.isEmpty() && !m.isEmpty()) {
                    password.setError("Must not be empty");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(m).matches()) {
                    email.setError("Email not valide");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(m, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in ", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MusicPlayer.class));
                            progressBar.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {

    }


}
