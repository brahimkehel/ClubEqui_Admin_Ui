package com.kehel.controle_v1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter_note extends RecyclerView.Adapter<RecyclerViewAdapter_note.ViewHolder>{
    List<Remarque> remarqueList;
    Context context;
    LayoutInflater inflater;
    DataBaseHelper db;

    RecyclerViewAdapter_note(Context c,List<Remarque> list)
    {
        this.remarqueList=list;
        this.context=c;
        this.inflater=LayoutInflater.from(context);
        this.db = new DataBaseHelper(context);
    }

    @NonNull
    @Override
    public RecyclerViewAdapter_note.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.note_grid_item,parent,false);
        return new ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter_note.ViewHolder holder, int position) {

        holder.date_changement.setText(remarqueList.get(position).getDate_changement().getDayOfWeek().toString().substring(0,3)+" "+remarqueList.get(position).getDate_changement().getDayOfMonth()+"/"+
                remarqueList.get(position).getDate_changement().getMonth().getValue());
        holder.contenue.setText(remarqueList.get(position).getContenue());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Supprimer note");
                builder.setMessage("Voulez vous supprimer cette remarque?");
                builder.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setPositiveButton("OUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            db.deleteRemarque(remarqueList.get(position).getId());
                            remarqueList.remove(remarqueList.get(position));
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, remarqueList.size());
                            Toast.makeText(context,"note supprimer",Toast.LENGTH_LONG).show();
                        }catch (Exception e)
                        {
                            Log.d("wsrong",e.getMessage());
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,Note_c_u.class);
                i.putExtra("code",2);
                i.putExtra("id",remarqueList.get(position).getId());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return remarqueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView date_changement,contenue;
        private CardView card;
        private ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_changement=itemView.findViewById(R.id.date_label);
            contenue=itemView.findViewById(R.id.contenue);
            delete=itemView.findViewById(R.id.remove_imageButton);
            card=itemView.findViewById(R.id.card_view);
        }
    }
}
