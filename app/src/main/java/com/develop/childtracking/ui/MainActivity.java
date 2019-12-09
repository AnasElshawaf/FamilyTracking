package com.develop.childtracking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button btLoginParent;
    private Button btLoginChild;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String userType="none";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    checkUserType(user);

                }
            }

        };
        btLoginParent = findViewById(R.id.bt_login_parent);
        btLoginChild = findViewById(R.id.bt_login_child);

        btLoginChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("child")) {
                    startActivity(new Intent(MainActivity.this, ChildMapActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, ChildLoginActivity.class));
                }
            }
        });

        btLoginParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("parent")) {
                    startActivity(new Intent(MainActivity.this, ParentChildsActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(MainActivity.this, ParentLoginActivity.class));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    private void checkUserType(final FirebaseUser user) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Child child = dataSnapshot1.getValue(Child.class);
                    if (child.getId().equals(user.getUid())) {
                        userType = "child";
                    } else {
                        userType = "parent";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
