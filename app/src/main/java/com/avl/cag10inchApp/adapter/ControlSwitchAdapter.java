package com.avl.cag10inchApp.adapter;

import static android.view.View.GONE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.ControlSwitchItem;

import java.util.List;

public class ControlSwitchAdapter extends RecyclerView.Adapter<ItemControlHolder> {

    private List<ControlSwitchItem> controlSwitchItems;
    private IControlSwitchListener listener;

    public ControlSwitchAdapter(List<ControlSwitchItem> controlSwitchItems, IControlSwitchListener listener) {
        this.controlSwitchItems = controlSwitchItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemControlHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_control_switch, parent, false);
        return new ItemControlHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemControlHolder holder, int position) {

        ControlSwitchItem item = controlSwitchItems.get(position);
        if (item.getOutportNumber() == 0) {
            holder.itemView.setVisibility(GONE);
            return;
        }
        holder.txtItem.setText(item.getDisplayName());
        holder.controlSwitch.setChecked(item.getState());
        holder.controlSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            item.setState(b);
            this.listener.onChangeSwitch(item);
        });
    }

    @Override
    public int getItemCount() {
        return controlSwitchItems.size();
    }
}
