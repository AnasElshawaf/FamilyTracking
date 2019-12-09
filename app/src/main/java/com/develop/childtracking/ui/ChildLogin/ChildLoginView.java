package com.develop.childtracking.ui.ChildLogin;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.develop.childtracking.R;
import com.develop.childtracking.ui.ChildMap.ChildMapActivity;
import com.develop.childtracking.ui.ResetPassword.ResetPasswordView;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ChildLoginView extends AppCompatActivity {

    private MaterialEditText email;
    private MaterialEditText password;
    private TextView txtForgetpassword;

    private String txt_email;
    private String txt_password;

    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intialViews();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDataInput();
            }
        });

        txtForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChildLoginView.this, ResetPasswordView.class));
            }
        });


    }

    private void intialViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        txtForgetpassword = findViewById(R.id.txt_forgetpassword);
    }

    private void setupViewModel() {
        ChildLoginViewModel parentChildsViewModel = ViewModelProviders.of(this).get(ChildLoginViewModel.class);
        parentChildsViewModel.loginAsChild(ChildLoginView.this, txt_email, txt_password);
        parentChildsViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "Successful":
                        Intent intent = new Intent(ChildLoginView.this, ChildMapActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        break;

                    case "Failed":
                        showMessage("Authentication failed");

                        break;
                }
            }
        });

    }

    private void checkDataInput() {
        if (validateInput()) {
            setupViewModel();
        }
    }

    private boolean validateInput() {
        txt_email = email.getText().toString();
        txt_password = password.getText().toString();
        if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
            showMessage("All fileds are required");
            return false;
        }
        return true;
    }

    private void showMessage(String msg) {
        Toast.makeText(ChildLoginView.this, msg, Toast.LENGTH_SHORT).show();
    }


}
