package com.example.kirito.androidmedicamentserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirito.androidmedicamentserver.Common.Common;
import com.example.kirito.androidmedicamentserver.Interface.ItemClickListener;
import com.example.kirito.androidmedicamentserver.R;

public class MedicamentViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener
{
    public TextView medicament_name;
    public ImageView medicament_image;

    private ItemClickListener itemClickListener;

    public MedicamentViewHolder(View itemView) {
        super(itemView);

        medicament_name = (TextView)itemView.findViewById(R.id.medicament_name);
        medicament_image = (ImageView)itemView.findViewById(R.id.medicament_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Seleccione la Accion");

        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
    }
}
