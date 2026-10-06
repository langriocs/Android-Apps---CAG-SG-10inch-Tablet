package com.avl.cag10inchApp.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;


public class ItemSourceHolder extends RecyclerView.ViewHolder {

    ImageView imgItem = itemView.findViewById(R.id.imgDisplay);
    TextView txtItem = itemView.findViewById(R.id.txtDisplay);
    View _itemView;

    public ItemSourceHolder(@NonNull View itemView) {
        super(itemView);
        _itemView = itemView;
        imgItem = itemView.findViewById(R.id.imgDisplay);
        txtItem = itemView.findViewById(R.id.txtDisplay);

    }

    public View getView() { return _itemView; }
}
