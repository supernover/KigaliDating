package com.ReallyDatingSite.KigaliRwanda.Settings;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ReallyDatingSite.KigaliRwanda.Extra.DateClass;
import com.ReallyDatingSite.KigaliRwanda.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

public class AccountActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String currentUser;

    Toolbar toolbarAccountUserToolbar;

    LinearLayout linearLayoutAccountUserUsername;
    LinearLayout linearLayoutAccountUserBirthday;
    LinearLayout linearLayoutAccountUserGender;
    LinearLayout linearLayoutAccountUserCity;
    LinearLayout linearLayoutAccountUserState;
    LinearLayout linearLayoutAccountUserCountry;
    LinearLayout linearLayoutAccountUserEmail;
    LinearLayout linearLayoutAccountUserMobile;
    LinearLayout linearLayoutAccountUserPassword;

    TextView textViewAccountUserUsername;
    TextView textViewAccountUserBirthday;
    TextView textViewAccountUserGender;
    TextView textViewAccountUserCity;
    TextView textViewAccountUserState;
    TextView textViewAccountUserCountry;
    TextView textViewAccountUserEmail;
    TextView textViewAccountUserMobile;


    String[] string_array_user_gender;

    String user_email;
    String user_epass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUser = firebaseUser.getUid();

        linearLayoutAccountUserUsername = findViewById(R.id.linearLayoutAccountUserUsername);
        linearLayoutAccountUserBirthday = findViewById(R.id.linearLayoutAccountUserBirthday);
        linearLayoutAccountUserGender = findViewById(R.id.linearLayoutAccountUserGender);
        linearLayoutAccountUserCity = findViewById(R.id.linearLayoutAccountUserCity);
        linearLayoutAccountUserState = findViewById(R.id.linearLayoutAccountUserState);
        linearLayoutAccountUserCountry = findViewById(R.id.linearLayoutAccountUserCountry);
        linearLayoutAccountUserEmail = findViewById(R.id.linearLayoutAccountUserEmail);
        linearLayoutAccountUserMobile = findViewById(R.id.linearLayoutAccountUserMobile);
        linearLayoutAccountUserPassword = findViewById(R.id.linearLayoutAccountUserPassword);

        textViewAccountUserUsername = findViewById(R.id.textViewAccountUserUsername);
        textViewAccountUserBirthday = findViewById(R.id.textViewAccountUserBirthday);
        textViewAccountUserGender = findViewById(R.id.textViewAccountUserGender);
        textViewAccountUserCity = findViewById(R.id.textViewAccountUserCity);
        textViewAccountUserState = findViewById(R.id.textViewAccountUserState);
        textViewAccountUserCountry = findViewById(R.id.textViewAccountUserCountry);
        textViewAccountUserEmail = findViewById(R.id.textViewAccountUserEmail);
        textViewAccountUserMobile = findViewById(R.id.textViewAccountUserMobile);

        string_array_user_gender = getResources().getStringArray(R.array.string_array_user_gender);

        toolbarAccountUserToolbar = findViewById(R.id.toolbarAccountUserToolbar);
        setSupportActionBar(toolbarAccountUserToolbar);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarAccountUserToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        linearLayoutAccountUserUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialogInput(textViewAccountUserUsername,
                        "user_name", "First Name", 1);
            }
        });

        linearLayoutAccountUserGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileDialogRadio(string_array_user_gender, textViewAccountUserGender,
                        "user_gender", "Select your Gender");
            }
        });

        linearLayoutAccountUserBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DateClass();
                datePicker.show(getSupportFragmentManager(), "Date Picker");

            }
        });


        linearLayoutAccountUserMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ProfileDialogInput(textViewAccountUserMobile,
                        "user_mobile", "Mobile Number", 1);
            }
        });

        linearLayoutAccountUserPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AccountActivity.this, PasswordActivity.class);
                intent.putExtra("user_email", user_email);
                intent.putExtra("user_epass", user_epass);
                startActivity(intent);
            }
        });

    }


    /* access modifiers changed from: private */
    public void ProfileDialogRadio(final String[] strArr, final TextView textView, final String str, String str2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) str2);
        builder.setSingleChoiceItems((CharSequence[]) strArr, new ArrayList(Arrays.asList(strArr)).indexOf(textView.getText().toString()), (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setPositiveButton((CharSequence) "Save", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                int checkedItemPosition = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                textView.setText(strArr[checkedItemPosition]);
                AccountActivity.this.ProfileUpdate(str, strArr[checkedItemPosition]);
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void ProfileDialogInput(final TextView textView, final String str, String str2, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) str2);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_input, (ViewGroup) null);
        builder.setView(inflate);
        final EditText editText = (EditText) inflate.findViewById(R.id.editTextProfileEditInput);
        editText.setMinLines(i);
        editText.setText(textView.getText().toString());
        builder.setPositiveButton((CharSequence) "Save", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                String obj = editText.getText().toString();
                if (!obj.equals("")) {
                    textView.setText(obj);
                    AccountActivity.this.ProfileUpdate(str, obj);
                    return;
                }
                Toast.makeText(AccountActivity.this, "Sorry! Could not save empty fields", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton((CharSequence) "Cancel", (DialogInterface.OnClickListener) null);
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public void ProfileUpdate(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put(str, str2);
        this.firebaseFirestore.collection("users").document(this.currentUser).update(hashMap);
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.firebaseFirestore.collection("users").document(this.currentUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException firebaseFirestoreException) {
                if (documentSnapshot != null) {
                    String string = documentSnapshot.getString("user_name");
                    String string2 = documentSnapshot.getString("user_birthday");
                    String string3 = documentSnapshot.getString("user_birthage");
                    String string4 = documentSnapshot.getString("user_gender");
                    String string5 = documentSnapshot.getString("user_city");
                    String string6 = documentSnapshot.getString("user_state");
                    String string7 = documentSnapshot.getString("user_country");
                    AccountActivity.this.user_email = documentSnapshot.getString("user_email");
                    AccountActivity.this.user_epass = documentSnapshot.getString("user_epass");
                    String string8 = documentSnapshot.getString("user_mobile");
                    AccountActivity.this.textViewAccountUserUsername.setText(string);
                    TextView textView = AccountActivity.this.textViewAccountUserBirthday;
                    textView.setText("(" + string3 + "yrs) " + string2);
                    AccountActivity.this.textViewAccountUserGender.setText(string4);
                    AccountActivity.this.textViewAccountUserCity.setText(string5);
                    AccountActivity.this.textViewAccountUserState.setText(string6);
                    AccountActivity.this.textViewAccountUserCountry.setText(string7);
                    AccountActivity.this.textViewAccountUserEmail.setText(AccountActivity.this.user_email);
                    AccountActivity.this.textViewAccountUserMobile.setText(string8);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.set(1, i);
        instance.set(2, i2);
        instance.set(5, i3);
        String format = new SimpleDateFormat("dd-MM-YYYY").format(instance.getTime());
        if (i > 2000) {
            Toast.makeText(this, "Sorry! you dont meet our user registration minimum age limits policy now. Please register with us after some time. Thank you for trying our app now!", Toast.LENGTH_LONG).show();
        } else {
            AgeUser(format);
        }
    }

    private void AgeUser(String str) {
        String[] split = str.split("-");
        int intValue = Integer.valueOf(split[0]).intValue();
        int intValue2 = Integer.valueOf(split[1]).intValue();
        int intValue3 = Integer.valueOf(split[2]).intValue();
        Calendar instance = Calendar.getInstance();
        Calendar instance2 = Calendar.getInstance();
        instance.set(intValue3, intValue2, intValue);
        int i = instance2.get(1) - instance.get(1);
        if (instance2.get(6) < instance.get(6)) {
            i--;
        }
        String num = new Integer(i).toString();
        this.textViewAccountUserBirthday.setText("(" + num + "yrs) " + str);
        ProfileUpdate("user_birthday", str);
        ProfileUpdate("user_birthage", num);
    }
}
