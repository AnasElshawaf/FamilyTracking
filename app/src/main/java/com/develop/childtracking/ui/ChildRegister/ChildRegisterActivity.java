package com.develop.childtracking.ui.ChildRegister;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.develop.childtracking.Model.SafeArea;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.ChildMap.ChildMapActivity;
import com.develop.childtracking.ui.SafeArea.SafeAreaView;
import com.develop.childtracking.utils.App_SharedPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class ChildRegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private MaterialEditText name;
    private MaterialEditText email;
    private MaterialEditText password;
    private Button btnRegister;
    private FirebaseAuth firebaseAuth;
    private String parentId;

    private Button btnSetSafeArea;
    private SafeArea safeArea;

    private String txt_Email;
    private String txt_password;
    private String txt_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_register);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Child");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().getExtras() != null) {
            parentId = getIntent().getExtras().getString("parentId");
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnRegister = findViewById(R.id.btn_register);
        btnSetSafeArea = findViewById(R.id.btn_set_safe_area);


        firebaseAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerAsChild();
            }
        });

        btnSetSafeArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(ChildRegisterActivity.this, SafeAreaView.class), 2);
            }
        });

    }

    private void registerAsChild() {
        if (validateInput()) {
            firebaseAuth.createUserWithEmailAndPassword(txt_Email, txt_password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = firebaseAuth.getCurrentUser().getUid();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child("Childs").child(userId);
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("id", userId);
                                hashMap.put("parentId", parentId);
                                hashMap.put("username", txt_name);
                                hashMap.put("imageUrl", "default");
                                hashMap.put("safeLat", String.valueOf(safeArea.getLat()));
                                hashMap.put("safeLang", String.valueOf(safeArea.getLang()));
                                hashMap.put("safeRadius", String.valueOf(safeArea.getRadius()));
                                hashMap.put("parentFcmToken", App_SharedPreferences.Get_Token(ChildRegisterActivity.this));

                                databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(ChildRegisterActivity.this, ChildMapActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                showMessage("You can't register with this email or password");
                            }
                        }
                    });
        }
    }

    private void showMessage(String msg) {
        Toast.makeText(ChildRegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            safeArea = data.getParcelableExtra("SAFE_AREA");
        }
    }

    boolean validateInput() {
        txt_Email = email.getText().toString();
        txt_password = password.getText().toString();
        txt_name = name.getText().toString();

        if (TextUtils.isEmpty(txt_Email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_name)) {
            showMessage("All fileds Required");
            return false;
        } else if (txt_password.length() < 6) {
            showMessage("Password must be at least 6 character");
            return false;

        } else if (safeArea == null) {
            showMessage(" Please set safe area ");
            return false;

        }
        return true;
    }
}