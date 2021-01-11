package com.ReallyDatingSite.KigaliRwanda.Extra;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.annotations.Nullable;
import com.ReallyDatingSite.KigaliRwanda.R;

import java.util.Calendar;

import io.reactivex.annotations.NonNull;

public class DateClass extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),
                R.style.DialogTheme,
                (DatePickerDialog.OnDateSetListener) getActivity(),
                year, month, day);
    }
}
