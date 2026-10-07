package com.avl.cag10inchApp.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    ImageView imgItem;
    TextView txtItem;
    TextView txtDeviceType;
    TextView txtSource;
    View _itemView;
    View viewConnectionState;


    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);
        _itemView = itemView;
        imgItem = itemView.findViewById(R.id.imgDisplay);
        txtItem = itemView.findViewById(R.id.txtDisplay);
        txtDeviceType = itemView.findViewById(R.id.txtDeviceType);
        viewConnectionState = itemView.findViewById(R.id.viewConnectState);
        txtSource = itemView.findViewById(R.id.txtSource);
    }

    public View getView() { return _itemView; }
}
