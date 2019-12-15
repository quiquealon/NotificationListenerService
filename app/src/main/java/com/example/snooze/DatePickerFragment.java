package com.example.snooze;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;
import java.util.List;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private View mView;

    public DatePickerFragment(View view) {
        mView = view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datepicker = new DatePickerDialog(getActivity(), this, year, month, day);
        datepicker.getDatePicker().setMinDate(c.getTime().getTime());
        return datepicker;
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        String date = day+"/"+(month+1)+"/"+year;

        Intent intent = new  Intent("envioDefecha");
        intent.putExtra("Date",date);


        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }


}



