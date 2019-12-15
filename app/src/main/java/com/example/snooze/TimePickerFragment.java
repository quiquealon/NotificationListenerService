package com.example.snooze;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private View mView;
    private String mId;

    public  TimePickerFragment(View view, String id) {
        mView = view;
        mId = id;

    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker


        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePicker = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));


        // Create a new instance of TimePickerDialog and return it
        return timePicker;
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        String time = hourOfDay+":"+minute+":00";

        Intent intent = new  Intent("envioDehora");
        intent.putExtra("Time",time);
        intent.putExtra("ID",mId);

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);

    }

}
