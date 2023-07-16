package com.example.mygymy.Signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mygymy.R;
import com.example.mygymy.Routine.Routine;
import com.example.mygymy.model.UserProfileData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Registeration extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextView textView;

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Routine.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        TextView editEmail = (TextView) findViewById(R.id.email);
        TextView editPassword = (TextView) findViewById(R.id.password);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton regBtn = (MaterialButton) findViewById(R.id.regButton);
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.loginNow);

        // Setting an onClickListener for the textView, which will start the Login activity when clicked
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        // Setting an onClickListener for the register button
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password;
                email=String.valueOf(editEmail.getText());
                password=String.valueOf(editPassword.getText());
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Registeration.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Registeration.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                // Creating a new user with email and password using FirebaseAuth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Registeration.this, "Account Created.",
                                            Toast.LENGTH_SHORT).show();
                                    //open the login page as soon as you register
                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                    startActivity(intent);
                                    finish();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    CollectionReference userProfileRef = db.collection("UserProfile");
                                    Date date = new Date();

                                    // Adding the user profile data to Firestore
                                    UserProfileData userProfileData = new UserProfileData(email, "", 0,date, 0);


                                    userProfileRef.document(user.getUid()).set(userProfileData);


                                } else {
                                    // If sign in fails, display a message to the user.

                                    Toast.makeText(Registeration.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });

    }
}
