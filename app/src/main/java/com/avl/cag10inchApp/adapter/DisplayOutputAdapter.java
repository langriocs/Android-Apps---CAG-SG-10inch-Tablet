package com.avl.cag10inchApp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayOutputItem;

import java.util.ArrayList;
import java.util.List;

public class DisplayOutputAdapter extends RecyclerView.Adapter<ItemViewHolder> {

    private List<DisplayOutputItem> displayOutputItems = new ArrayList<>();
    private IDisplayOutputListener listener;

    public DisplayOutputAdapter(List<DisplayOutputItem> displayOutputItems, IDisplayOutputListener listener) {
        this.displayOutputItems = displayOutputItems;
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

        if (item.isPowerOn()) {
            holder.txtItem.setTextColor(Color.RED);
//            holder.getView().setBackgroundResource(R.drawable.bg_rounded_card_selected);
        } else {
//            holder.getView().setBackgroundResource(R.drawable.bg_rounded_card);
            holder.txtItem.setTextColor(Color.WHITE);
        }

        holder.txtDeviceType.setText(item.getSource());
        holder.imgItem.setImageResource(item.getImgResId());
        holder.txtItem.setText(item.getDisplayName());
        holder.getView().setOnClickListener(view -> {
            int currentPosition = holder.getBindingAdapterPosition();

            if (currentPosition == RecyclerView.NO_POSITION) {
                return;
            }

            DisplayOutputItem clickedItem =  displayOutputItems.get(currentPosition);

            if (listener != null) {
                listener.onDisplayOutputItemClick(clickedItem);
            }

        });

    }

    @Override
    public int getItemCount() {
        return displayOutputItems.size();
    }

    public void updateSource(int deviceId, String source) {

        for (int i = 0; i < displayOutputItems.size(); i++) {

            DisplayOutputItem item = displayOutputItems.get(i);

            if (item.getDeviceId() == deviceId) {

                item.setSource(source);

                notifyItemChanged(i);

                break;
            }
        }
    }
}
