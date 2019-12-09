package com.develop.childtracking.ui.ParentChilds;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.develop.childtracking.ui.ChildRegister.ChildRegisterActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ParentChildsView extends AppCompatActivity {

    private TextView tvNoChilds;
    private FloatingActionButton ftAddChild;

    private List<Child> childList;
    private ParentChildsAdapter childAdapter;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_childs);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Your Childs");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        initialViews();

        ftAddChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentChildsView.this, ChildRegisterActivity.class)
                        .putExtra("parentId", "" + firebaseUser.getUid()));
            }
        });

        setupRecyclerChilds();

        setupViewModel();


    }

    private void setupViewModel() {
        ParentChildsViewModel parentChildsViewModel = ViewModelProviders.of(this).get(ParentChildsViewModel.class);
        parentChildsViewModel.displayMyChilds(firebaseUser);
        parentChildsViewModel.mutableLiveData.observe(this, new Observer<List<Child>>() {
            @Override
            public void onChanged(List<Child> childList) {
                childAdapter.setChildList(childList);
                if (childList.size() == 0) {
                    tvNoChilds.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void setupRecyclerChilds() {
       RecyclerView  rvChilds = findViewById(R.id.rv_childs);
        childAdapter = new ParentChildsAdapter();
        rvChilds.setHasFixedSize(true);
        rvChilds.setLayoutManager(new LinearLayoutManager(this));
        rvChilds.setAdapter(childAdapter);

    }

    private void initialViews() {
        childList = new ArrayList<>();
        tvNoChilds = findViewById(R.id.tv_no_childs);
        ftAddChild = findViewById(R.id.ft_add_child);
    }
}