package com.example.snooze;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> mTitle = new ArrayList<>();
    private ArrayList<String> mText = new ArrayList<>();
    private ArrayList<String> mTime = new ArrayList<>();
    private ArrayList<String> mDate = new ArrayList<>();
    public ArrayList<Integer> mFavorite = new ArrayList<>();
    private Context mContext;

    //Listener para la imagen de favoritos

    private OnItemClickListener mListener;

    public interface OnItemClickListener{

        void onFavoriteClick(int position);
        void onItemLongClick(int position,View view);

    }

    public void SetOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


    public RecyclerViewAdapter( Context mContext, ArrayList<String> mImage, ArrayList<String> mTitle, ArrayList<String> mText, ArrayList<String> mTime, ArrayList<String> mDate, ArrayList<Integer> mFavorite) {
        this.mImage = mImage;
        this.mTitle = mTitle;
        this.mText = mText;
        this.mTime = mTime;
        this.mDate = mDate;
        this.mFavorite = mFavorite;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        ViewHolder holder =  new ViewHolder(view,mListener);


        Log.i("Holder","onCreateViewHolder invoked");

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Drawable icon = null;
        try {
            icon = mContext.getPackageManager().getApplicationIcon(mImage.get(position));
            holder.image.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(mFavorite.get(position)==0){
            holder.check.setBackgroundResource(R.drawable.starunchecked);
        }
        else if(mFavorite.get(position)==2){
            holder.check.setBackgroundResource(R.drawable.garbage);
        }
        else {
            holder.check.setBackgroundResource(R.drawable.starchecked);
        }


        holder.title.setText(mTitle.get(position));
        holder.text.setText(mText.get(position));
        holder.time.setText(mTime.get(position));
        holder.date.setText(mDate.get(position));

        Log.i(String.valueOf(position),"onBindViewHolder invoked");

   }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageView check;
        TextView title;
        TextView text;
        TextView time;
        TextView date;
        ConstraintLayout parentLayout;

        public ViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            check = itemView.findViewById(R.id.checked);
            title = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.text);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date);
            parentLayout = itemView.findViewById(R.id.parent_layout);

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onFavoriteClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemLongClick(position,view);
                        }
                        return true;
                    }

                    return false;
                }
            });


        }
    }
}
