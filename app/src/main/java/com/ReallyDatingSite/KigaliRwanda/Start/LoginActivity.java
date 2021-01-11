package com.ReallyDatingSite.KigaliRwanda.Start;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ReallyDatingSite.KigaliRwanda.Main.MainActivity;
import com.ReallyDatingSite.KigaliRwanda.R;

public class LoginActivity extends AppCompatActivity {

    private Button btnLoginPageLogin;
    private Button btnLoginPageRegister;
    private Button btnLoginPageReset;
    ProgressDialog dialog;
    /* access modifiers changed from: private */
    public EditText loginEmail;
    /* access modifiers changed from: private */
    public EditText loginPass;
    /* access modifiers changed from: private */
    public FirebaseAuth userAuth;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.login_activity);
        this.userAuth = FirebaseAuth.getInstance();
        this.loginEmail = (EditText) findViewById(R.id.loginEmailText);
        this.loginPass = (EditText) findViewById(R.id.loginPassText);
        this.btnLoginPageLogin = (Button) findViewById(R.id.btnLoginPageLogin);
        this.btnLoginPageRegister = (Button) findViewById(R.id.btnLoginPageRegister);
        this.btnLoginPageReset = (Button) findViewById(R.id.btnLoginPageReset);
        this.dialog = new ProgressDialog(this);
        this.dialog.setMessage("Loading...");
        this.dialog.setCancelable(false);
        this.btnLoginPageLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String obj = LoginActivity.this.loginEmail.getText().toString();
                String obj2 = LoginActivity.this.loginPass.getText().toString();
                if (TextUtils.isEmpty(obj) || TextUtils.isEmpty(obj2)) {
                    Toast.makeText(LoginActivity.this, "Please enter your email and password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                LoginActivity.this.dialog.show();
                LoginActivity.this.userAuth.signInWithEmailAndPassword(obj, obj2).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            LoginActivity.this.sendToMain();
                            LoginActivity.this.dialog.dismiss();
                            return;
                        }
                        String message = task.getException().getMessage();
                        LoginActivity loginActivity = LoginActivity.this;
                        Toast.makeText(loginActivity, "Error : " + message, Toast.LENGTH_LONG).show();
                        LoginActivity.this.dialog.dismiss();
                    }
                });
            }
        });
        this.btnLoginPageRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoginActivity.this.startActivity(intent);
            }
        });
        this.btnLoginPageReset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    /* access modifiers changed from: private */
    public void sendToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
