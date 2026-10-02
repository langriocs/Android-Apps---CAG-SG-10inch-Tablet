package com.avl.cag10inchApp.fragments;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avl.cag10inchApp.viewmodel.BriefingPanelViewModel;
import com.avl.cagApp.R;

public class BriefingPanel extends Fragment {

    private BriefingPanelViewModel mViewModel;

    public static BriefingPanel newInstance() {
        return new BriefingPanel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_briefing_panel, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(BriefingPanelViewModel.class);
        // TODO: Use the ViewModel
    }

}