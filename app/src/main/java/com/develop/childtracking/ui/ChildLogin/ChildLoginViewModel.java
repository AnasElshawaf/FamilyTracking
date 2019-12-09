package com.develop.childtracking.ui.ChildLogin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.develop.childtracking.Model.Child;
import com.develop.childtracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChildLoginViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void loginAsChild(Context context, String txt_email, String txt_password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isUserChild(firebaseAuth.getCurrentUser());
                        } else {
                            mutableLiveData.setValue(context.getResources().getString(R.string.failed_tag));
                        }
                    }
                });
    }


    public void isUserChild(FirebaseUser firebaseUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Childs");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Child child = dataSnapshot1.getValue(Child.class);
                    if (child != null) {
                        if (child.getId().equals(firebaseUser.getUid())) {
                            mutableLiveData.setValue("Successful");
                        }else {
                            mutableLiveData.setValue("Failed");

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
