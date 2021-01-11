package com.ReallyDatingSite.KigaliRwanda.Settings;


import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ReallyDatingSite.KigaliRwanda.R;

public class EmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_activity);



    }

    @Override
    protected void onStart() {
        super.onStart();

        Toast.makeText(this, "email", Toast.LENGTH_SHORT).show();


    }
}
