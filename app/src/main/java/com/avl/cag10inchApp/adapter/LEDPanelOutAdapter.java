package com.avl.cag10inchApp.adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.LEDPanelOutput;

import java.util.ArrayList;
import java.util.List;

public class LEDPanelOutAdapter extends RecyclerView.Adapter<LEDPanelHolder>{

    private List<LEDPanelOutput> ledPanelOutputList;
    private ILEDPanelOutputListener listener;

    public LEDPanelOutAdapter(List<LEDPanelOutput> ledPanelOutputList,
                              ILEDPanelOutputListener listener) {

        this.ledPanelOutputList = ledPanelOutputList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LEDPanelHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LEDPanelHolder holder, int position) {

        LEDPanelOutput item = ledPanelOutputList.get(position);
        holder.txtItem.setText(item.getDesc());
        holder.imgItem.setImageResource(item.getImg());
    }

    @Override
    public int getItemCount() {
        return ledPanelOutputList.size();
    }

    public void setItems(List<LEDPanelOutput> items) {
        if (items == null) {
            ledPanelOutputList = new ArrayList<>();
        } else {
            ledPanelOutputList = new ArrayList<>(items);
        }
        notifyDataSetChanged();
    }
}
