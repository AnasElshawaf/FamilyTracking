package com.develop.childtracking.ui.ResetPassword;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.develop.childtracking.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordViewModel extends ViewModel {

    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void FunRestPassword(Context context, String txt_email) {
        firebaseAuth.sendPasswordResetEmail(txt_email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mutableLiveData.setValue(context.getResources().getString(R.string.success_tag));
                } else {
                    mutableLiveData.setValue(task.getException().getMessage());
                }
            }
        });
    }
}
