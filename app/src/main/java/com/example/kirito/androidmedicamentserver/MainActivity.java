package com.example.kirito.androidmedicamentserver;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    Button btnSigIn;
    TextView txtSlogan;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSigIn = (Button)findViewById(R.id.btnSignIn);
        txtSlogan = (TextView)findViewById(R.id.txtSlogan);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Nabila.ttf");
        txtSlogan.setTypeface(face);

        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

    }
}
