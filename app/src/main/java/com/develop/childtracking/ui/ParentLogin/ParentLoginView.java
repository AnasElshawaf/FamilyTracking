package com.develop.childtracking.ui.ParentLogin;

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
import com.develop.childtracking.ui.ParentChilds.ParentChildsView;
import com.develop.childtracking.ui.ResetPassword.ResetPasswordView;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ParentLoginView extends AppCompatActivity {

    private MaterialEditText email;
    private MaterialEditText password;
    private Button btnLogin, btRegister;
    private TextView txtForgetpassword;

    private String txt_email;
    private String txt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialViews();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) setupViewModel("login");
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInput()) setupViewModel("register");
            }
        });

        txtForgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentLoginView.this, ResetPasswordView.class));
            }
        });
    }

    private void initialViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btn_login);
        btRegister = findViewById(R.id.btn_register);
        txtForgetpassword = findViewById(R.id.txt_forgetpassword);
    }


    private void setupViewModel(String type) {
        ParentLoginViewModel parentLoginViewModel = ViewModelProviders.of(this).get(ParentLoginViewModel.class);
        if (type.equals("login")) {
            parentLoginViewModel.loginAsParent(ParentLoginView.this, txt_email, txt_password);
        } else if (type.equals("register")) {
            parentLoginViewModel.registerAsParent(ParentLoginView.this, txt_email, txt_password);
        }
        parentLoginViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "Successful":
                        Intent intent = new Intent(ParentLoginView.this, ParentChildsView.class);
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
        Toast.makeText(ParentLoginView.this, msg, Toast.LENGTH_SHORT).show();
    }


}
