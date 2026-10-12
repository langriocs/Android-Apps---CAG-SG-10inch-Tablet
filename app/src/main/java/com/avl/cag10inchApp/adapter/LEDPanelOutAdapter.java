package com.avl.cag10inchApp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.LEDPanelOutput;

import java.util.ArrayList;
import java.util.List;

public class LEDPanelOutAdapter extends RecyclerView.Adapter<LEDPanelHolder>{

    private List<LEDPanelOutput> ledPanelOutputList;
    private ILEDPanelOutputListener listener;

    public LEDPanelOutAdapter(
                              ILEDPanelOutputListener listener) {

        this.ledPanelOutputList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public LEDPanelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_led_panel, parent, false);
        return new LEDPanelHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LEDPanelHolder holder, int position) {

        LEDPanelOutput item = ledPanelOutputList.get(position);
        holder.txtItem.setText(item.getDesc());
        holder.imgItem.setImageResource(item.getImg());
        holder.getView().setOnClickListener(view -> {
            listener.onLEDOutputItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return ledPanelOutputList.size();
    }

    public void setItems(List<LEDPanelOutput> items) {
        ledPanelOutputList.clear();
        ledPanelOutputList.addAll(items);
        notifyDataSetChanged();
    }
}
