package com.develop.childtracking.ui.ParentLogin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

public class ParentLoginViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void loginAsParent(Context context, String txt_email, String txt_password) {
        firebaseAuth.signInWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            isUserParent(firebaseAuth.getCurrentUser());
                        } else {
                            mutableLiveData.setValue(context.getResources().getString(R.string.failed_tag));
                        }
                    }
                });
    }

    public void registerAsParent(Context context, String txt_email, String txt_password) {
        firebaseAuth.createUserWithEmailAndPassword(txt_email, txt_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = firebaseAuth.getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Parents").child(userId);
                            reference.setValue(true);
                            mutableLiveData.setValue(context.getResources().getString(R.string.success_tag));
                        } else {
                            mutableLiveData.setValue(context.getResources().getString(R.string.failed_tag));
                        }
                    }
                });
    }

    public void isUserParent(FirebaseUser firebaseUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Parents");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if (dataSnapshot1 != null) {
                        Log.e("userKey", dataSnapshot1.getKey());
                        if (dataSnapshot1.getKey().equals(firebaseUser.getUid())) {
                            mutableLiveData.setValue("Successful");
                        } else {
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
