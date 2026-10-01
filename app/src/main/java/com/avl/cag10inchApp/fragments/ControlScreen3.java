package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ControlScreen3ViewModel;

public class ControlScreen3 extends Fragment {

    private ControlScreen3ViewModel mViewModel;

    public static ControlScreen3 newInstance() {
        return new ControlScreen3();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_screen3, container, false);
    }

}