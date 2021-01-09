package com.allam9072.sms_firebase;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private String code, verificationCodeBySystem;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.GONE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            errorMessageTV.setText(e.getMessage());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }
    };
    private ProgressBar progressBar;
    TextView errorMessageTV;
    EditText editTextCode, editTextPhone;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progress);
        errorMessageTV = findViewById(R.id.errorMessageTV);
        mAuth = FirebaseAuth.getInstance();
        editTextCode = findViewById(R.id.editText);
        editTextPhone = findViewById(R.id.editTextPhone);
        String editTextString = editTextCode.getText().toString();

        btnSend();
        btnVerify(editTextString);


    }

    private void btnVerify(String editTextString) {
        findViewById(R.id.verifyBTN).setOnClickListener(v ->
        {
            if (editTextString.equals(verificationCodeBySystem)) {
                errorMessageTV.setText("DONE");
            } else {
                errorMessageTV.setText("NOT");
            }
        });
    }

    private void btnSend() {
        findViewById(R.id.sendCodeBTN).setOnClickListener(v -> {
            progressBar.setVisibility(View.GONE);
            phone = editTextPhone.getText().toString();
            sendVerificationCodeToUser(phone);
            errorMessageTV.setText(phone);
        });
    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+20" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
        signInByCredentials(credential);
    }

    private void signInByCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    errorMessageTV.setText("SuccessFul");

                } else {
                    errorMessageTV.setText(task.getException().getMessage());
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}