package com.develop.childtracking.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.develop.childtracking.Adapter.ChildAdapter;
import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentChildsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvChilds;
    private TextView tvNoChilds;
    private FloatingActionButton ftAddChild;

    private List<Child> childList;
    private ChildAdapter childAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_childs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Childs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvChilds = findViewById(R.id.rv_childs);
        tvNoChilds = findViewById(R.id.tv_no_childs);
        ftAddChild = findViewById(R.id.ft_add_child);

        rvChilds.setHasFixedSize(true);
        rvChilds.setLayoutManager(new LinearLayoutManager(this));
        childList = new ArrayList<>();

        ftAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentChildsActivity.this, ChildRegisterActivity.class)
                        .putExtra("parentId", "" + FirebaseAuth.getInstance().getCurrentUser().getUid()));
            }
        });

        displayMyChilds();

    }

    private void displayMyChilds() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Child child = dataSnapshot1.getValue(Child.class);
                    if (child!=null){
                        Log.e("childList", child.getParentId() + "//" + firebaseUser.getUid());

                        if (child.getParentId().equals(firebaseUser.getUid())) {
                            childList.add(child);
                        }
                    }else {
                        Log.e("childList", "null child");

                    }
                }
                if (childList.isEmpty()) {
                    tvNoChilds.setVisibility(View.VISIBLE);
                }
                Log.e("childList", childList.size() + "");
                childAdapter = new ChildAdapter(ParentChildsActivity.this, childList);
                rvChilds.setAdapter(childAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
