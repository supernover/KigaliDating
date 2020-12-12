package com.superlover.Tereta.Start;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.superlover.Tereta.R;

public class ResetActivity extends AppCompatActivity {
    private Button buttonResetLogin;
    private Button buttonResetRegister;
    private Button buttonResetReset;
    ProgressDialog dialog;
    /* access modifiers changed from: private */
    public EditText editTextResetEmail;
    /* access modifiers changed from: private */
    public FirebaseAuth firebaseAuth;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.reset_activity);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.editTextResetEmail = (EditText) findViewById(R.id.editTextResetEmail);
        this.buttonResetReset = (Button) findViewById(R.id.buttonResetReset);
        this.buttonResetLogin = (Button) findViewById(R.id.buttonResetLogin);
        this.buttonResetRegister = (Button) findViewById(R.id.buttonResetRegister);
        this.buttonResetReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = ResetActivity.this.editTextResetEmail.getText().toString();
                if (!TextUtils.isEmpty(obj)) {
                    ResetActivity.this.firebaseAuth.sendPasswordResetEmail(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetActivity.this, "Email send succesfully! Please check your email.", Toast.LENGTH_SHORT).show();
                                ResetActivity.this.editTextResetEmail.setText("");
                                return;
                            }
                            Toast.makeText(ResetActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ResetActivity.this, "Please enter your registered email to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });
        this.buttonResetLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ResetActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ResetActivity.this.startActivity(intent);
            }
        });
        this.buttonResetRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ResetActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ResetActivity.this.startActivity(intent);
            }
        });
    }
}
