package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {


    private EditText edtname;
    private EditText edtcity;
    private EditText edtemail;
    private EditText edtpassword;
    private Button btmsignup;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        edtcity = (EditText) findViewById(R.id.edtcity);
        edtname = (EditText) findViewById(R.id.edtname);
        edtemail = (EditText) findViewById(R.id.edtemail);
        edtpassword = (EditText) findViewById(R.id.edtpasswordL);
        btmsignup = (Button) findViewById(R.id.btmsignup);

        btmsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Email = edtemail.getText().toString();
                final String Password = edtpassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(RegistrationActivity.this,"Successfully Registrated ", Toast.LENGTH_LONG).show();
                            String UserId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(UserId);
                            currentUserDb.child("name").setValue(edtname.getText().toString());
                            currentUserDb.child("city").setValue(edtcity.getText().toString());
                            currentUserDb.child("Things").setValue(new ArrayList<>());
                            currentUserDb.child("Likes").setValue(new ArrayList<>());
                            currentUserDb.child("Dislikes").setValue(new ArrayList<>());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}