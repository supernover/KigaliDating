package com.superlover.Tereta.Start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.superlover.Tereta.Legals.PolicyActivity;
import com.superlover.Tereta.Legals.TermsActivity;
import com.superlover.Tereta.Main.MainActivity;
import com.superlover.Tereta.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class StartActivity extends AppCompatActivity {

    ArrayList<String> arrayListFacebook;
    Button buttonStartEmail;
    Button buttonStartFacebook;
    ProgressDialog dialog;
    private FirebaseAuth firebaseAuth;
    /* access modifiers changed from: private */
    public FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    ImageView imageStart;
    private CallbackManager mCallbackManager;
    TextView textViewStartPolicy;
    TextView textViewStartTerms;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(512, 512);
        }
        setContentView((int) R.layout.start_activity);
        this.arrayListFacebook = new ArrayList<>();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
        this.buttonStartFacebook = (Button) findViewById(R.id.buttonStartFacebook);
        this.buttonStartEmail = (Button) findViewById(R.id.buttonStartEmail);
        this.imageStart = (ImageView) findViewById(R.id.imageStart);
        this.textViewStartTerms = (TextView) findViewById(R.id.textViewStartTerms);
        this.textViewStartPolicy = (TextView) findViewById(R.id.textViewStartPolicy);
        this.firebaseFirestore.collection("admin").document("settings").addSnapshotListener(new EventListener<DocumentSnapshot>() {
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException firebaseFirestoreException) {
                if (documentSnapshot != null) {
                    Picasso.get().load(documentSnapshot.getString("start_image")).placeholder((int) R.drawable.gradient).into(StartActivity.this.imageStart);
                }
            }
        });
        this.buttonStartEmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });
        this.textViewStartTerms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.startActivity(new Intent(StartActivity.this, TermsActivity.class));
            }
        });
        this.textViewStartPolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.startActivity(new Intent(StartActivity.this, PolicyActivity.class));
            }
        });
        this.mCallbackManager = CallbackManager.Factory.create();
        this.buttonStartFacebook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StartActivity.this.FacebookSignIn();
                StartActivity startActivity = StartActivity.this;
                startActivity.dialog = new ProgressDialog(startActivity);
                StartActivity.this.dialog.setMessage("Loading...");
                StartActivity.this.dialog.setCancelable(false);
                StartActivity.this.dialog.show();
            }
        });
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.mCallbackManager.onActivityResult(i, i2, intent);
    }

    /* access modifiers changed from: private */
    public void FacebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions((Activity) this, (Collection<String>) Arrays.asList(new String[]{"email", "public_profile"}));
        LoginManager.getInstance().registerCallback(this.mCallbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                StartActivity.this.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            public void onCancel() {
                StartActivity.this.dialog.dismiss();
            }

            public void onError(FacebookException facebookException) {
                StartActivity.this.dialog.dismiss();
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleFacebookAccessToken(AccessToken accessToken) {
        this.firebaseAuth.signInWithCredential(FacebookAuthProvider.getCredential(accessToken.getToken())).addOnCompleteListener((Activity) this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final String uid = task.getResult().getUser().getUid();
                    final String displayName = task.getResult().getUser().getDisplayName();
                    final String email = task.getResult().getUser().getEmail();
                    final String uri = task.getResult().getUser().getPhotoUrl().toString();
                    StartActivity.this.firebaseFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        public void onComplete(Task<DocumentSnapshot> task) {
                            if (!task.getResult().exists()) {
                                StartActivity.this.FirestoreRegister(uid, displayName, email, uri);
                            } else {
                                StartActivity.this.WelcomePage();
                            }
                        }
                    });
                    return;
                }
                StartActivity.this.dialog.dismiss();
                Toast.makeText(StartActivity.this.getApplicationContext(), "User with Email id already exists. Login with email to link this account", Toast.LENGTH_LONG).show();
            }
        });
    }

    /* access modifiers changed from: private */
    public void FirestoreRegister(final String str, String str2, String str3, String str4) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_uid", str);
        hashMap.put("user_email", str3);
        hashMap.put("user_epass", "");
        hashMap.put("user_name", str2);
        hashMap.put("user_gender", "Male");
        hashMap.put("user_birthday", "01/01/1990");
        hashMap.put("user_birthage", "25");
        hashMap.put("user_city", "Mumbai");
        hashMap.put("user_state", "Maharashtra");
        hashMap.put("user_country", "India");
        hashMap.put("user_location", "Bandra");
        hashMap.put("user_thumb", str4);
        hashMap.put("user_image", str4);
        hashMap.put("user_cover", str4);
        hashMap.put("user_status", "offline");
        hashMap.put("user_looking", "Man");
        hashMap.put("user_about", "Hi! Everybody I am newbie here.");
        hashMap.put("user_latitude", "19.075983");
        hashMap.put("user_longitude", "72.877655");
        hashMap.put("user_online", Timestamp.now());
        hashMap.put("user_joined", Timestamp.now());
        hashMap.put("user_verified", "yes");
        hashMap.put("user_facebook", "yes");
        hashMap.put("user_dummy", "yes");
        this.firebaseFirestore.collection("users").document(str).set((Map<String, Object>) hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    StartActivity.this.RemindPage(str);
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void WelcomePage() {
        startActivity(new Intent(this, MainActivity.class));
    }

    /* access modifiers changed from: private */
    public void RemindPage(String str) {
        Intent intent = new Intent(this, RemindActivity.class);
        intent.putExtra("user_uid", str);
        startActivity(intent);
    }
}
