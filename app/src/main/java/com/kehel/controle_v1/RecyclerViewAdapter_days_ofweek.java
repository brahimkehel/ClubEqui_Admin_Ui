package com.kehel.controle_v1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecyclerViewAdapter_days_ofweek extends RecyclerView.Adapter<RecyclerViewAdapter_days_ofweek.ViewHolder> {
private Context context;
private final List<String> day_ofweek= Arrays.asList("MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
        "SUNDAY");
private LayoutInflater inflater;
private int selecteditem;
private IDayofWeekListener dayofWeekListener;

    public RecyclerViewAdapter_days_ofweek(Context context_,IDayofWeekListener dayofWeekListener_){
        this.context=context_;
        this.inflater=LayoutInflater.from(this.context);
        this.selecteditem = 0;
        this.dayofWeekListener=dayofWeekListener_;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.week_days_layout,parent,false);
        return new RecyclerViewAdapter_days_ofweek.ViewHolder(v,dayofWeekListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.day_.setText(day_ofweek.get(position));
        if (selecteditem == position) {
            holder.card_.setCardBackgroundColor(Color.parseColor("#FF92BB"));
            dayofWeekListener.onCardClick(holder.day_.getText().toString());
        }
        else {
            holder.card_.setCardBackgroundColor(Color.WHITE);
        }
        holder.card_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousItem = selecteditem;
                selecteditem = position;

                notifyItemChanged(previousItem);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return day_ofweek.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView day_;
        private CardView card_;
        private IDayofWeekListener dayofWeekListener;
        public ViewHolder(@NonNull View itemView,IDayofWeekListener dayofWeekListener_) {
            super(itemView);
            this.dayofWeekListener=dayofWeekListener_;
            day_=itemView.findViewById(R.id.day_);
            card_=itemView.findViewById(R.id.card_);
        }
    }
}
