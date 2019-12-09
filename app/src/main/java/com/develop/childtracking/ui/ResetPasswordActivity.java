package com.develop.childtracking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.develop.childtracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText sendEmail;
    private Button btnReset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendEmail = findViewById(R.id.send_email);
        btnReset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunRestPassword();
            }
        });

    }

    private void FunRestPassword() {
        String email = sendEmail.getText().toString();
        if (email.equals("")) {
            showToast("Please enter your email");
        } else {
            sendEmail.setText("");
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        showToast("Please check your email");
//                        startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    }else {
                        showToast(task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void showToast(String msg){
        Toast.makeText(ResetPasswordActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}
