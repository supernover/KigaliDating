package com.superlover.Tereta.Start;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.DialogFragment;

import com.facebook.appevents.UserDataStore;
import com.facebook.internal.ServerProtocol;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.HttpHeaders;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.superlover.Tereta.Extra.DateClass;
import com.superlover.Tereta.Main.MainActivity;
import com.superlover.Tereta.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RemindActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final long FASTEST_INTERVAL = 1000;
    private static final long INTERVAL = 1000;
    AuthCredential authCredential;
    private Button btnRemind;
    private String currentUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    FusedLocationProviderApi fusedLocationProviderApi;
    GoogleApiClient googleApiClient;
    private Location location;
    LocationManager locationManager;
    LocationRequest locationRequest;
    ProgressDialog progressDialog;
    /* access modifiers changed from: private */
    public RadioButton radioButtonRemindGender;
    private RadioGroup radioGroupRemindGender;
    String stringLatitude;
    String stringLongitude;
    String stringLooking;
    String string_city;
    String string_country;
    String string_location;
    String string_state;
    /* access modifiers changed from: private */
    public TextView textViewRemindBirthday;

    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void onConnectionSuspended(int i) {
    }

    /* access modifiers changed from: protected */
    public void stopLocationUpdates() {
    }

    /* access modifiers changed from: protected */
    @SuppressLint("WrongConstant")
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.remind_activity);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
        this.firebaseFirestore = FirebaseFirestore.getInstance();
        this.currentUser = this.firebaseUser.getUid();
        this.btnRemind = (Button) findViewById(R.id.btnRemind);
        this.textViewRemindBirthday = (TextView) findViewById(R.id.textViewRemindBirthday);
        this.radioGroupRemindGender = (RadioGroup) findViewById(R.id.radioGroupRemindGender);
        this.fusedLocationProviderApi = LocationServices.FusedLocationApi;
        this.googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        this.locationManager = (LocationManager) getSystemService(FirebaseAnalytics.Param.LOCATION);
        LocationPremissionCheck();
        GooglePlayServiceCheck();
        GPSLocationServiceCheck();
        GoogleLocationRequest();
        this.textViewRemindBirthday.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                new DateClass().show(RemindActivity.this.getSupportFragmentManager(), "Date Picker");
            }
        });
        this.btnRemind.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (RemindActivity.this.radioButtonRemindGender == null) {
                    Toast.makeText(RemindActivity.this, "Please turn on any GPS or location service to use the app", Toast.LENGTH_SHORT).show();
                } else if (RemindActivity.this.string_city == null || RemindActivity.this.string_city.equals("city") || RemindActivity.this.string_state == null || RemindActivity.this.string_state.equals(ServerProtocol.DIALOG_PARAM_STATE) || RemindActivity.this.string_country == null || RemindActivity.this.string_country.equals(UserDataStore.COUNTRY)) {
                    Toast.makeText(RemindActivity.this, "Please turn on any GPS or location service to use the app", Toast.LENGTH_SHORT).show();
                } else {
                    String charSequence = RemindActivity.this.radioButtonRemindGender.getText().toString();
                    String charSequence2 = RemindActivity.this.textViewRemindBirthday.getText().toString();
                    if (charSequence.equals("Male")) {
                        RemindActivity.this.stringLooking = "Woman";
                    } else {
                        RemindActivity.this.stringLooking = "Man";
                    }
                    if (TextUtils.isEmpty(charSequence) && TextUtils.isEmpty(charSequence2)) {
                        Toast.makeText(RemindActivity.this, "Please Fill in all the details to proceed!", Toast.LENGTH_SHORT).show();
                    } else if (!charSequence2.equals("")) {
                        RemindActivity remindActivity = RemindActivity.this;
                        remindActivity.progressDialog = new ProgressDialog(remindActivity);
                        RemindActivity.this.progressDialog.setTitle("Please Wait");
                        RemindActivity.this.progressDialog.setMessage("Setting Profile...");
                        RemindActivity.this.progressDialog.setCancelable(false);
                        RemindActivity.this.progressDialog.show();
                        RemindActivity.this.ProfileUpdate(charSequence, charSequence2);
                    } else {
                        Toast.makeText(RemindActivity.this, "Please Fill in all the details to proceed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void radioButtonRemindGender(View view) {
        this.radioButtonRemindGender = (RadioButton) findViewById(this.radioGroupRemindGender.getCheckedRadioButtonId());
    }

    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Calendar instance = Calendar.getInstance();
        instance.set(1, i);
        instance.set(2, i2);
        instance.set(5, i3);
        String format = new SimpleDateFormat("dd-MM-yyyy").format(instance.getTime());
        if (i > 2000) {
            Toast.makeText(this, "Sorry! you dont meet our user registration minimum age limits policy now. Please register with us after some time. Thank you for trying our app now!", Toast.LENGTH_LONG).show();
            this.textViewRemindBirthday.setText("");
            return;
        }
        this.textViewRemindBirthday.setText(format);
    }

    private String AgeUser(String str) {
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
        return new Integer(i).toString();
    }

    /* access modifiers changed from: private */
    public void ProfileUpdate(String str, String str2) {
        HashMap hashMap = new HashMap();
        hashMap.put("user_gender", str);
        hashMap.put("user_birthday", str2);
        hashMap.put("user_birthage", AgeUser(str2));
        hashMap.put("user_city", this.string_city);
        hashMap.put("user_state", this.string_state);
        hashMap.put("user_country", this.string_country);
        hashMap.put("user_location", this.string_location);
        hashMap.put("user_looking", this.stringLooking);
        hashMap.put("user_latitude", this.stringLatitude);
        hashMap.put("user_longitude", this.stringLongitude);
        hashMap.put("user_dummy", "no");
        this.firebaseFirestore.collection("users").document(this.currentUser).update(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    RemindActivity.this.startActivity(new Intent(RemindActivity.this, MainActivity.class));
                    RemindActivity.this.finish();
                    RemindActivity.this.progressDialog.dismiss();
                    return;
                }
                RemindActivity.this.progressDialog.dismiss();
            }
        });
    }

    /* access modifiers changed from: private */
    public void LocationPremissionCheck() {
        new Permissions.Options().setRationaleDialogTitle("Location Permission").setSettingsDialogTitle(HttpHeaders.WARNING);
        Permissions.check((Context) this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, (String) null, (Permissions.Options) null, (PermissionHandler) new PermissionHandler() {
            public void onGranted() {
            }

            public void onDenied(Context context, ArrayList<String> arrayList) {
                super.onDenied(context, arrayList);
                RemindActivity.this.LocationPremissionCheck();
            }
        });
    }

    public boolean GooglePlayServiceCheck() {
        GoogleApiAvailability instance = GoogleApiAvailability.getInstance();
        int isGooglePlayServicesAvailable = instance.isGooglePlayServicesAvailable(this);
        if (isGooglePlayServicesAvailable == 0) {
            return true;
        }
        if (!instance.isUserResolvableError(isGooglePlayServicesAvailable)) {
            return false;
        }
        instance.getErrorDialog(this, isGooglePlayServicesAvailable, 2405).show();
        return false;
    }

    private void GPSLocationServiceCheck() {
        if (!this.locationManager.isProviderEnabled("gps")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage((CharSequence) "Your GPS seems to be disabled, enable it to use the app?").setCancelable(false).setPositiveButton((CharSequence) "Yes", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemindActivity.this.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                }
            }).setNegativeButton((CharSequence) "No", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    Intent intent = new Intent(RemindActivity.this, StartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    RemindActivity.this.startActivity(intent);
                    RemindActivity.this.finish();
                }
            });
            builder.create().show();
        }
    }

    /* access modifiers changed from: protected */
    public void GoogleLocationRequest() {
        this.locationRequest = new LocationRequest();
        this.locationRequest.setInterval(1000);
        this.locationRequest.setFastestInterval(1000);
        this.locationRequest.setPriority(100);
    }

    private void GoogleLocationRetreive(Double d, Double d2) {
        try {
            List<Address> fromLocation = new Geocoder(this, Locale.getDefault()).getFromLocation(d.doubleValue(), d2.doubleValue(), 1);
            if (fromLocation != null && fromLocation.size() > 0) {
                this.string_city = fromLocation.get(0).getLocality();
                this.string_state = fromLocation.get(0).getAdminArea();
                this.string_country = fromLocation.get(0).getCountryName();
                this.string_location = fromLocation.get(0).getAddressLine(0);
                if (this.string_country == null) {
                    if (this.string_state != null) {
                        this.string_country = this.string_state;
                    } else if (this.string_city != null) {
                        this.string_country = this.string_city;
                    } else {
                        this.string_country = "unknown";
                    }
                }
                if (this.string_city == null) {
                    if (this.string_state != null) {
                        this.string_city = this.string_state;
                    } else {
                        this.string_city = this.string_country;
                    }
                }
                if (this.string_state == null) {
                    if (this.string_city != null) {
                        this.string_state = this.string_city;
                    } else {
                        this.string_state = this.string_country;
                    }
                }
                if (this.string_location == null) {
                    this.string_location = "unknown";
                }
            }
            this.stringLatitude = d.toString();
            this.stringLongitude = d2.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Location unable to detect", Toast.LENGTH_SHORT).show();
        }
    }

    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            this.location = LocationServices.FusedLocationApi.getLastLocation(this.googleApiClient);
            LocationRecheck(this.location);
            startLocationUpdates();
        }
    }

    /* access modifiers changed from: protected */
    public void startLocationUpdates() {
        if (!(ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") == 0)) {
            Toast.makeText(this, "Please provide location permission to use the app!", Toast.LENGTH_SHORT).show();
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(this.googleApiClient, this.locationRequest, (LocationListener) this);
    }

    public void onLocationChanged(Location location2) {
        if (location2 != null) {
            LocationRecheck(location2);
        }
    }

    private void LocationRecheck(Location location2) {
        if (location2 == null) {
            Toast.makeText(this, "Please turn on any GPS or location service to use the app", Toast.LENGTH_SHORT).show();
        } else if (location2.getLatitude() == 0.0d || location2.getLongitude() == 0.0d) {
            Toast.makeText(this, "Please turn on any GPS or location service to use the app", Toast.LENGTH_SHORT).show();
        } else {
            GoogleLocationRetreive(Double.valueOf(location2.getLatitude()), Double.valueOf(location2.getLongitude()));
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        this.googleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        this.googleApiClient.disconnect();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    public void onResume() {
        super.onResume();
        if (this.googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
