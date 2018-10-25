package com.demo.mdb.spring2017finalassessment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private TextView emailText;
    private TextView passText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailText = (TextView) findViewById(R.id.email);
        passText = (TextView) findViewById(R.id.password);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.register).setOnClickListener(this);
        /* TODO Part 1
         * Implement login. When the login button is pressed, attempt to login the user, and if
         * that is successful, go to the TabbedActivity. If the register button is pressed, go to
         * the RegisterActivity. Check the layout files for the IDs of the views used in this part
         */

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // user exists, move to tabbed activity screen
            Intent i = new Intent(getApplicationContext(), TabbedActivity.class);
            startActivity(i);
        }

    }

    private void signIn(String email, String pass) {
        // check to make sure email and pass are valid first
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // transition to next page
                    Intent i = new Intent(getApplicationContext(), TabbedActivity.class);
                    startActivity(i);
                } else {
                    // display toast error
                    Toast.makeText(getApplicationContext(), "Incorrect Username or Password", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                break;
            case R.id.login:
                if (Utils.isValid(getApplicationContext(), emailText.getText().toString(), passText.getText().toString())) {
                    signIn(emailText.getText().toString(), passText.getText().toString());
                }
        }
    }
}
