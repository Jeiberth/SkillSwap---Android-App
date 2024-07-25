package com.example.skillswap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChooseSignInUp extends AppCompatActivity {

    private Button sin;
    private Button sup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_sign_in_up);

        sin = (Button) findViewById(R.id.sin);
        sup = (Button) findViewById(R.id.sup);

        sin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseSignInUp.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseSignInUp.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });




    }
}