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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    // Firebase authentication object
    FirebaseAuth mAuth;
    // TextView to display the registration prompt
    TextView textView;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            // If user is already logged in, redirect to the Routine activity
            Intent intent = new Intent(getApplicationContext(), Routine.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the activity
        setContentView(R.layout.login);

        // Initialize UI elements
        TextView editEmail = (TextView) findViewById(R.id.email);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView editPassword =
                (TextView) findViewById(R.id.Password);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) MaterialButton logBtn =
                (MaterialButton) findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        textView = findViewById(R.id.registerNow);

        // OnClickListener for the registration prompt
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When clicked, redirect to the Registration activity
                Intent intent = new Intent(getApplicationContext(), Registeration.class);
                startActivity(intent);
                finish();
            }
        });


        // OnClickListener for the login button
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,password ;
                email=String.valueOf(editEmail.getText());
                password=String.valueOf(editPassword.getText());
                // Check if the email or password fields are empty
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(Login.this,"Enter Email",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Login.this,"Enter Password",Toast.LENGTH_SHORT).show();
                    return;
                }
                // Attempt to sign in the user
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),Routine.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}