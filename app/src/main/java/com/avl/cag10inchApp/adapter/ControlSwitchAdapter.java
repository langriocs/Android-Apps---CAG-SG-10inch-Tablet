package com.avl.cag10inchApp.adapter;

import static android.view.View.GONE;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.ControlSwitchItem;
import com.avl.cag10inchApp.model.DisplayOutputItem;

import java.util.ArrayList;
import java.util.List;

public class ControlSwitchAdapter extends RecyclerView.Adapter<ItemControlHolder> {

    private List<DisplayOutputItem> controlSwitchItems;
    private IControlSwitchListener listener;

    public ControlSwitchAdapter(IControlSwitchListener listener) {
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

        DisplayOutputItem item = controlSwitchItems.get(position);

        holder.controlSwitch.setOnCheckedChangeListener(null);
        holder.txtItem.setText(item.getDescription());
        holder.controlSwitch.setChecked(item.isTurnOn());
        holder.controlSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!compoundButton.isPressed()) {
                return;
            }

            if (item.isTurnOn() == b) {
                return;
            }

            item.setTurnOn(b);
            this.listener.onChangeSwitch(item);
        });
    }

    @Override
    public int getItemCount() {
        return controlSwitchItems.size();
    }

    public void setItems(List<DisplayOutputItem> items) {
        if (items == null) {
            controlSwitchItems = new ArrayList<>();
        } else {
            controlSwitchItems = new ArrayList<>(items);
        }
        notifyDataSetChanged();
    }
}
