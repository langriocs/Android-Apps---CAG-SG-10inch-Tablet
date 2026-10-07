package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ControlScreen2ViewModel;

public class TrainingPanel extends Fragment {

    private ControlScreen2ViewModel mViewModel;

    public static TrainingPanel newInstance() {
        return new TrainingPanel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_screen2, container, false);
    }


}