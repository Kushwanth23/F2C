package com.example.f2c;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG="EmailPassword";

    EditText email,password,conPassword;
    Button register,link_to_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        conPassword = findViewById(R.id.cPassword);

        register = findViewById(R.id.btn_reg);
        link_to_login = findViewById(R.id.btn_login);

        link_to_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

        private void signup(){
            Log.d(TAG, "signUp"+email);
            if(!validateForm()){
                return;
            }
            String em=email.getText().toString();
            String pw=password.getText().toString();

            mAuth.createUserWithEmailAndPassword(em, pw)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            }
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signUpWithEmail:failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Registration Failed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        private boolean validateForm(){
            boolean valid = true;

            String em=email.getText().toString();
            if(TextUtils.isEmpty(em)){
                email.setError("Required.");
                valid=false;
            }
            else {
                email.setError(null);
            }

            String pw = password.getText().toString();
            if (TextUtils.isEmpty(pw)) {
                password.setError("Required.");
                valid=false;
            } else {
                password.setError(null);
            }

            String cpw = conPassword.getText().toString();
            if (TextUtils.isEmpty(cpw)) {
                conPassword.setError("Required.");
                valid=false;
            } else {
                conPassword.setError(null);
            }

            if (!(cpw.equals(pw))) {
                conPassword.setError("Re Enter Password!");
                valid=false;
            } else {
                conPassword.setError(null);
            }

            return valid;
        }

}