package com.avl.cag10inchApp.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;

public class ItemControlHolder extends RecyclerView.ViewHolder {
    SwitchCompat controlSwitch;
    TextView txtItem;
    View _itemView;

    public ItemControlHolder(@NonNull View itemView) {
        super(itemView);
        _itemView = itemView;
        controlSwitch = itemView.findViewById(R.id.control_switch);
        txtItem = itemView.findViewById(R.id.txt_control_switch);
    }

    public View getView() { return _itemView; }
}
