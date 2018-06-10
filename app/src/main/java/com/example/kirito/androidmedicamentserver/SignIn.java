package com.example.kirito.androidmedicamentserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kirito.androidmedicamentserver.Common.Common;
import com.example.kirito.androidmedicamentserver.Model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    EditText edtPhone, edtPassword;
    Button btnSigIn;

    //Base de datos FIREBASE
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnSigIn = (Button) findViewById(R.id.btnSignIn);

        //Iniciamos FIREBASE
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");
        btnSigIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtPhone.getText().toString(), edtPassword.getText().toString());
            }
        });
    }

    private void signInUser(String phone, String password) {
        final ProgressDialog mDialog =  new ProgressDialog(SignIn.this);
        mDialog.setMessage("Espere por favor . . . ");
        mDialog.show();

        final String localPhone = phone;
        final  String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(localPhone).exists()){
                    mDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if(Boolean.parseBoolean(user.getIsStaff())) // si IsStaff(Es personal) == tru
                    {
                        if(user.getPassword().equals(localPassword)){
                            Intent login = new Intent(SignIn.this, Home.class);
                            Common.currentUser = user;
                            startActivity(login);
                            finish();
                        }else
                            Toast.makeText(SignIn.this, "Password Incorrecto", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(SignIn.this, "Ingrese con una cuenta de Administrador", Toast.LENGTH_SHORT).show();
                }else {
                    mDialog.dismiss();
                    Toast.makeText(SignIn.this, "El usuario no existe en la Base de Datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
