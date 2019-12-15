package com.example.snooze;


import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.snooze.Data.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class starredFragment extends Fragment {

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



    public starredFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_starred,container,false);

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayoutStarred);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                initStarredRecyclerView(root);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);


            }
        });


        // Inicializar la base de datos

        myDb = new DatabaseHelper(getActivity());

        initStarredRecyclerView(root);


        return root;
    }


    private void initStarredRecyclerView(final View root){

        // Limpiar listas

        mImage.clear();
        mTitle.clear();
        mText.clear();
        mTime.clear();
        mDate.clear();
        mFavorite.clear();
        mId.clear();



        //Obtener data de la db

        Cursor cursor = myDb.getStarredData();


        if(cursor.getCount() != 0){
            if(cursor.moveToFirst()){
                do{
                    mId.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper._ID)));
                    mImage.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.PACK)));
                    mTitle.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE)));
                    mText.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TEXT)));
                    mTime.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.TIME)));
                    mDate.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DATE)));
                    mFavorite.add(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FAVORITE)));




                }while (cursor.moveToNext());
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

        RecyclerView recyclerView = root.findViewById(R.id.recycler_starred);
        adapter = new RecyclerViewAdapter(getActivity(),mImage,mTitle,mText,mTime,mDate,mFavorite);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onFavoriteClick(int position) {

                String id = mId.get(position);
                int check = mFavorite.get(position);

                //Modificacion local

                mImage.remove(position);
                mTitle.remove(position);
                mText.remove(position);
                mTime.remove(position);
                mDate.remove(position);
                mFavorite.remove(position);

                // Modificacion en base de datos

                boolean isInserted = myDb.updateData(id,check);

                if(isInserted)
                    Toast.makeText(getActivity(),"Data updated",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(),"Data not updated",Toast.LENGTH_SHORT).show();


                adapter.notifyDataSetChanged();

            }

            @Override
            public void onItemLongClick(int position, View view) {

            }
        });





    }

}
