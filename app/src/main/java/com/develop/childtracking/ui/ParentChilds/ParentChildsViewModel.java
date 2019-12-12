package com.develop.childtracking.ui.ParentChilds;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.develop.childtracking.Model.Child;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ParentChildsViewModel extends ViewModel {

    MutableLiveData<List<Child>> mutableLiveData = new MutableLiveData<>();
    List<Child> childList = new ArrayList<>();

    public void displayMyChilds(FirebaseUser firebaseUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                childList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Child child = dataSnapshot1.getValue(Child.class);
                    if (child != null || firebaseUser!=null) {

                        if (child.getParentId().equals(firebaseUser.getUid())) {
                            childList.add(child);
                        }
                    } else {
                        Log.e("childList", "null child");
                    }
                }
                mutableLiveData.setValue(childList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
