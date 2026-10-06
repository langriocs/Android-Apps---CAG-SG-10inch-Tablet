package com.avl.cag10inchApp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.repository.DeviceConnectionState;

import java.util.ArrayList;
import java.util.List;

public class DisplayOutputAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<DisplayOutputItem> displayOutputItems;
    private IDisplayOutputListener listener;
    private int selectedDeviceId = -1;

    public DisplayOutputAdapter(IDisplayOutputListener listener) {
        this.displayOutputItems = new ArrayList<>();
        this.listener = listener;
    }

    public void setItems(List<DisplayOutputItem> items) {
        if (items == null) {
            displayOutputItems = new ArrayList<>();
        } else {
            displayOutputItems = new ArrayList<>(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_output, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DisplayOutputItem item = displayOutputItems.get(position);

        holder.txtDeviceType.setText(item.getDisplayName());
        holder.imgItem.setImageResource(item.getImgResId());
        holder.txtItem.setText(item.getDescription());
        holder.getView().setSelected(item.getDeviceId() == selectedDeviceId);

        DeviceConnectionState state = item.getConnectionState();

        if (item.isDeviceStatusVisible()) {
            holder.viewConnectionState.setVisibility(View.VISIBLE);
        } else {
            holder.viewConnectionState.setVisibility(View.GONE);
        }


        holder.viewConnectionState.setSelected(state == DeviceConnectionState.CONNECTED);

        holder.getView().setOnClickListener(view -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            DisplayOutputItem selItem = displayOutputItems.get(currentPosition);
            setSelectedDevice(selItem.getDeviceId());

            if (listener != null) {
                listener.onDisplayOutputItemClick(displayOutputItems.get(currentPosition));
            }

        });

    }

    @Override
    public int getItemCount() {
        return displayOutputItems.size();
    }

    public void setSelectedDevice(int deviceId) {
        int oldPosition = -1;
        int newPosition = -1;

        for (int i = 0; i < displayOutputItems.size(); i++) {

            DisplayOutputItem item = displayOutputItems.get(i);

            if (item.getDeviceId() == selectedDeviceId) {
                oldPosition = i;
            }

            if (item.getDeviceId() == deviceId) {
                newPosition = i;
            }
        }

        selectedDeviceId = deviceId;

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }

        if (newPosition != -1 && newPosition != oldPosition) {
            notifyItemChanged(newPosition);
        }
    }
}
