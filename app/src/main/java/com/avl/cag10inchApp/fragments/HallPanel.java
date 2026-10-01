package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.HallPanelViewModel;
import com.google.android.material.button.MaterialButton;

public class HallPanel extends Fragment {

    private HallPanelViewModel mViewModel;

    public static HallPanel newInstance() {
        return new HallPanel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hall_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialButton btnSeparateHall = view.findViewById(R.id.btn_separation_hall);
        MaterialButton btnCombineHall = view.findViewById(R.id.btn_combine_hall);
//        layoutSeparateHallOperation = view.findViewById(R.id.layout_);

        btnSeparateHall.setOnClickListener(v->{

        });
    }
}