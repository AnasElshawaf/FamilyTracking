package com.develop.childtracking.ui.ResetPassword;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.develop.childtracking.R;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ResetPasswordView extends AppCompatActivity {

    private MaterialEditText email;
    private Button btnReset;
    private String txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset password");

        initialViews();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunRestPassword();
            }
        });

    }

    private void initialViews() {
        email = findViewById(R.id.send_email);
        btnReset = findViewById(R.id.btn_reset);
    }

    private void FunRestPassword() {
        if (validateInput()) {
            email.setText("");
            setupViewModel();
        }
    }

    private boolean validateInput() {
        txt_email = email.getText().toString();
        if (TextUtils.isEmpty(txt_email)) {
            showToast("Please enter you Email");
            return false;
        }
        return true;
    }

    private void setupViewModel() {
        ResetPasswordViewModel resetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel.class);
        resetPasswordViewModel.FunRestPassword(this, txt_email);
        resetPasswordViewModel.mutableLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Successful")) {
                    showToast("Please check your Email");
                    finish();
                } else {
                    showToast(s);
                }
            }
        });

    }

    private void showToast(String msg) {
        Toast.makeText(ResetPasswordView.this, msg, Toast.LENGTH_SHORT).show();

    }
}
