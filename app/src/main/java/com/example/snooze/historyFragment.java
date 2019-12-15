package com.example.snooze;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.snooze.Data.DatabaseHelper;

import java.text.CollationElementIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclasNotificationServices.
 */
public class historyFragment extends Fragment {

    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    private static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS";


    private DataInputChangeBroadcastReceiver dataInputChangeBroadcastReceiver;
    private AlertDialog enableNotificationListenerAlertDialog;


    private DataInputDate dataInputDate;
    private DataInputTime dataInputTime;


    final String SHARED_PREFS = "Notification";


    private View mView;

    //vars recycle
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mTitle = new ArrayList<>();
    private ArrayList<String> mText = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    private ArrayList<Integer> mFavorite = new ArrayList<>();
    private ArrayList<String> mId = new ArrayList<>();


    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerViewAdapter adapter;

    //database

    DatabaseHelper myDb;


    public historyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_history, container, false);


        // Si el usuario no activó el servicio de escucha de notificaciones, le pedimos que lo haga

        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog();
            enableNotificationListenerAlertDialog.show();
        }

        // Finalmente registramos un receptor para informarle a MainActivity cuando se ha recibido una notificación

        dataInputChangeBroadcastReceiver = new DataInputChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.snooze");
        Objects.requireNonNull(getActivity()).registerReceiver(dataInputChangeBroadcastReceiver, intentFilter);

        // broadcast de la fecha

        dataInputDate = new DataInputDate();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataInputDate,new IntentFilter("envioDefecha"));


        // broadcast de tiempo

        dataInputTime = new DataInputTime();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(dataInputTime,new IntentFilter("envioDehora"));




        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initRecyclerView(root);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        // Inicializar la base de datos

        myDb = new DatabaseHelper(getActivity());

        // Cuando inicializa traer la informacion

        initRecyclerView(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Objects.requireNonNull(getActivity()).unregisterReceiver(dataInputChangeBroadcastReceiver);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataInputDate);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(dataInputTime);


    }

    private void updateInterceptedNotification(int notificationCode, String pack, String title, String text, String time, String date) {

        boolean isInserted = myDb.insertData(pack, title, text, time, date, 0);

        if (isInserted)
            Toast.makeText(getActivity(), "Data inserted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getActivity(), "Data not inserted", Toast.LENGTH_SHORT).show();

       


    }


    public class DataInputChangeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int receivedNotificationCode = intent.getIntExtra("Codigo de la notificacion", -1);
            String receivedTitle = intent.getStringExtra("Titulo de la notificacion");
            String receivedText = intent.getStringExtra("Texto de la notificacion");
            String sendApp = intent.getStringExtra("Aplicacion envia");

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

            String time = formatTime.format(calendar.getTime());
            String date = formatDate.format(calendar.getTime());


            updateInterceptedNotification(receivedNotificationCode, sendApp, receivedTitle, receivedText, time, date);

        }
    }


    private void updateInterceptedDates() {

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        String receivedTime = sharedPreferences.getString("Time","");
        String receivedId = sharedPreferences.getString("ID","-1");
        String receivedDate = sharedPreferences.getString("Date","");

        editor.clear();
        editor.apply();

        String[] partsTime = receivedTime.split(":");
        String hora = partsTime[0];
        String minuto = partsTime[1];



        String[] partsDate = receivedDate.split("/");
        String dia = partsDate[0];
        String mes = partsDate[1];
        String ano = partsDate[2];


        String id=null,image = null,title = null,text=null;
        Integer favorite=null;

        String idBorrado=null;

        Cursor cursor = myDb.getNotificationHistory(receivedId);


        if(cursor != null && cursor.moveToFirst()){
            cursor.moveToFirst();
            id=cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID));
            image=cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACK));
            title=cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
            text=cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT));
            favorite=2;
            cursor.close();
        }


        boolean isInserted = myDb.insertSnooze(image,title,text,receivedTime,receivedDate,favorite,id);




        if (isInserted)
        {
            Toast.makeText(getActivity(), "Data inserted", Toast.LENGTH_SHORT).show();
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hora));
            c.set(Calendar.MINUTE,Integer.parseInt(minuto));
            c.set(Calendar.SECOND,0);
            c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dia));
            c.set(Calendar.MONTH, Integer.parseInt(mes)-1);
            c.set(Calendar.YEAR, Integer.parseInt(ano));


;
            startAlarm(c,title,text,id);

        }


        else
            Toast.makeText(getActivity(), "Data not inserted", Toast.LENGTH_SHORT).show();



    }

    private void startAlarm(Calendar c,String title, String text, String id){

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(),AlertReceiver.class);
        intent.putExtra("Title",title);
        intent.putExtra("Text",text);
        intent.putExtra("ID",id);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),Integer.parseInt(id),intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),pendingIntent);

    }



    public class DataInputDate extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedDate = intent.getStringExtra("Date");


            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Date",receivedDate);
            editor.apply();


            updateInterceptedDates();

        }


    }





    public class DataInputTime extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedTime = intent.getStringExtra("Time");
            String receivedId = intent.getStringExtra("ID");


            SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putString("Time",receivedTime);
            editor.putString("ID",receivedId);
            editor.apply();

            DialogFragment datePickerFragment = new DatePickerFragment(mView);
            datePickerFragment.show(getFragmentManager(), "date picker");





        }
    }



    private AlertDialog buildNotificationServiceAlertDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
                    }
                });
        alertDialogBuilder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finish();
                        System.exit(0);
                    }
                });
        return (alertDialogBuilder.create());
    }


    private boolean isNotificationServiceEnabled() {
        String pkgName = getActivity().getPackageName();
        final String flat = Settings.Secure.getString(getActivity().getContentResolver(),
                ENABLED_NOTIFICATION_LISTENERS);
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (int i = 0; i < names.length; i++) {
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.getPackageName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void initRecyclerView(final View root) {


        // Limpiar listas

        mImage.clear();
        mTitle.clear();
        mText.clear();
        mTime.clear();
        mDate.clear();
        mFavorite.clear();
        mId.clear();


        //Obtener data de la db

        Cursor cursor = myDb.getHistoryData();

        if (cursor.getCount() != 0) {
            if (cursor.moveToFirst()) {
                do {
                    mId.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)));
                    mImage.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACK)));
                    mTitle.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)));
                    mText.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT)));
                    mTime.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
                    mDate.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE)));
                    mFavorite.add(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FAVORITE)));


                } while (cursor.moveToNext());
            }
        }

        cursor.close();

        // Mostrar las notificaciones mas recientes primero


        Collections.reverse(mId);
        Collections.reverse(mImage);
        Collections.reverse(mTitle);
        Collections.reverse(mText);
        Collections.reverse(mTime);
        Collections.reverse(mDate);
        Collections.reverse(mFavorite);


        RecyclerView recyclerView = root.findViewById(R.id.recycler_history);
        adapter = new RecyclerViewAdapter(getActivity(), mImage, mTitle, mText, mTime, mDate, mFavorite);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        adapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {


            @Override
            public void onFavoriteClick(int position) {

                String id = mId.get(position);
                int check = mFavorite.get(position);

                //Modificacion local

                if (check == 0)
                    mFavorite.set(position, 1);
                else
                    mFavorite.set(position, 0);


                // Modificacion en base de datos

                boolean isInserted = myDb.updateData(id, check);

                if (isInserted)
                    Toast.makeText(getActivity(), "Data updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Data not updated", Toast.LENGTH_SHORT).show();


                adapter.notifyItemChanged(position);

            }

            @Override
            public void onItemLongClick(int position, View view) {

                String id = mId.get(position);
                DialogFragment timepicker = new TimePickerFragment(view,id);
                timepicker.show(getFragmentManager(), "time picker");

                mView = view;



            }
        });

    }

}



