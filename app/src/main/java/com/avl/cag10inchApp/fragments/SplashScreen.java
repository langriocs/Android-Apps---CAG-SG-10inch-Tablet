package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avl.cag10inchApp.AppConstant;
import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.libs.MyLibUtil;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

public class SplashScreen extends Fragment {

    private TextView tvRoomName;
    private MaterialButton btnPressStart;
    private boolean isFourPanel = false;
    private int controlDeviceUI;

    public static SplashScreen newInstance() {
        return new SplashScreen();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRoomName = view.findViewById(R.id.room_name_tv);

        btnPressStart = view.findViewById(R.id.btn_press_start);

        if (btnPressStart == null) {
            return;
        }

        btnPressStart.setOnClickListener(v -> {
            proceedToNextScreen(v);
        });

        view.setOnClickListener(v -> {
            proceedToNextScreen(v);
        });

        ShareViewModel shareViewModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
        shareViewModel.getControlRoomDevices().observe(getViewLifecycleOwner(), controlRoomDevice -> {
            if (controlRoomDevice != null) {
                shareViewModel.setSelectedControlDevice(controlRoomDevice.controlDevice);
                shareViewModel.setSelectedRoomDevices(controlRoomDevice.roomDevices);
                tvRoomName.setText(controlRoomDevice.controlDevice.getRoomName());
                controlDeviceUI = controlRoomDevice.controlDevice.getDeviceUI();
            } else {
                showAlert("Device IP Address not found! Please contact the admin.");
            }
        });

//        final String ipAddress = "192.168.1.110"; // CAG OPS
//        final String ipAddress = "192.168.1.131"; // Master
//        final String ipAddress = "192.168.1.130"; // Briefing
//        final String ipAddress = "192.168.1.100"; // Training
        final String ipAddress = MyLibUtil.getIPAddress(true);
        shareViewModel.fetchControlDeviceByIpAddress(ipAddress);
    }

    private void proceedToNextScreen(View v) {
        if (controlDeviceUI == AppConstant.UI_0) {
            return;
        }

        if (controlDeviceUI == AppConstant.UI_1) {
            Navigation.findNavController(v).navigate(R.id.action_splashScreen1_to_briefingPanel);
        }
        if (controlDeviceUI == AppConstant.UI_2) {
            Navigation.findNavController(v).navigate(R.id.action_splashScreen1_to_cagopsPanel);
        }
        if (controlDeviceUI == AppConstant.UI_3) {
            Navigation.findNavController(v).navigate(R.id.action_splashScreen1_to_trainingPanel);
        }
        if (controlDeviceUI == AppConstant.UI_4) {
            Navigation.findNavController(v).navigate(R.id.action_splashScreen1_to_masterPanel);
        }

    }

    private void showAlert(String message ) {
        CustomAlertDialog alertScreenDialog = new CustomAlertDialog();
        alertScreenDialog.setTitle("Changi Airport Group");
        alertScreenDialog.setMessage(message);
        alertScreenDialog.show(getParentFragmentManager(), "alert dialog");
    }
}