package com.avl.cag10inchApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplaySourceItem;

import java.util.List;

public class DisplaySourceAdapter extends RecyclerView.Adapter<ItemSourceHolder> {

    private List<DisplaySourceItem> sourceItemList;
    private IDisplaySourceListener listener;
    private int selectedDevicePort = -1;

    public DisplaySourceAdapter(List<DisplaySourceItem> sourceItemList, IDisplaySourceListener listener) {
        this.sourceItemList = sourceItemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemSourceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display_source, parent, false);
        return new ItemSourceHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemSourceHolder holder, int position) {

        DisplaySourceItem item = sourceItemList.get(position);
        holder.txtItem.setText(item.getDisplayName());
        holder.imgItem.setImageResource(item.getImgResId());

        holder.getView().setSelected(
                item.getPortNumber() == selectedDevicePort
        );

        holder.getView().setOnClickListener(view -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            DisplaySourceItem selItem = sourceItemList.get(adapterPosition);
            setSelectedDevice(selItem.getPortNumber());
            if (listener != null) {
                this.listener.onDisplaySourceItemClick(selItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sourceItemList.size();
    }

    public void setSelectedDevice(int portNumber) {
        int oldPosition = -1;
        int newPosition = -1;

        for (int i = 0; i < sourceItemList.size(); i++) {

            DisplaySourceItem item = sourceItemList.get(i);

            if (item.getPortNumber() == selectedDevicePort) {
                oldPosition = i;
            }

            if (item.getPortNumber() == portNumber) {
                newPosition = i;
            }
        }

        selectedDevicePort = portNumber;

        if (oldPosition != -1) {
            notifyItemChanged(oldPosition);
        }

        if (newPosition != -1 && newPosition != oldPosition) {
            notifyItemChanged(newPosition);
        }
    }
}
