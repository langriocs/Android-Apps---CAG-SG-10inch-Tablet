package com.avl.cag10inchApp.fragments;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avl.cag10inchApp.viewmodel.ControlScreen1ViewModel;
import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

public class ControlScreen1 extends Fragment {

    private ControlScreen1ViewModel mViewModel;
    private ShareViewModel mShareModel;
    private TextView tvRoomName;
    private CountDownTimer warmupTimer;
    private boolean isFrontSwitchConnected = false;
    private boolean isFrontTVConnected = false;
    private boolean isSideSwitchConnected = false;
    private boolean isSideTVConnected = false;


    public static ControlScreen1 newInstance() {
        return new ControlScreen1();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ControlScreen1ViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_control_screen1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        tvRoomName = view.findViewById(R.id.room_name_tv);
        MaterialButton btnUSB1 = view.findViewById(R.id.btn_usb_1);
        MaterialButton btnUSB2 = view.findViewById(R.id.btn_usb_2);
        MaterialButton btnWIFI1 = view.findViewById(R.id.btn_wifi_1);
        MaterialButton btnWIFI2 = view.findViewById(R.id.btn_wifi_2);
        MaterialButton btnPowerOffFront = view.findViewById(R.id.btn_power_off_front);
        MaterialButton btnPowerOnFront = view.findViewById(R.id.btn_power_on_front);
        MaterialButton btnPowerOffSide = view.findViewById(R.id.btn_power_off_side);
        MaterialButton btnPowerOnSide = view.findViewById(R.id.btn_power_on_side);
        MaterialButton btnExit = view.findViewById(R.id.btn_exit);

        ImageView switchFrontLedIcon = view.findViewById(R.id.switchFrontLedIcon);
        ImageView tvFrontLedIcon = view.findViewById(R.id.tvFrontLedIcon);
        TextView switchFrontLedDesc = view.findViewById(R.id.switchFrontLedDesc_tv);
        TextView tvFrontLedDesc = view.findViewById(R.id.tvFrontLedDesc_tv);

        ImageView switchSideLedIcon = view.findViewById(R.id.switchSideLedIcon);
        ImageView tvSideLedIcon = view.findViewById(R.id.tvSideLedIcon);
        TextView switchSideLedDesc = view.findViewById(R.id.switchSideLedDesc_tv);
        TextView tvSideLedDesc = view.findViewById(R.id.tvSideLedDesc_tv);


        btnUSB1.setOnClickListener(v -> {

        });
        btnUSB2.setOnClickListener(v -> {

        });
        btnWIFI1.setOnClickListener(v -> {

        });
        btnWIFI2.setOnClickListener(v -> {

        });
        btnPowerOffFront.setOnClickListener(v -> {

        });
        btnPowerOnFront.setOnClickListener(v -> {

        });
        btnPowerOffSide.setOnClickListener(v -> {

        });
        btnPowerOnSide.setOnClickListener(v -> {

        });
        btnExit.setOnClickListener(v -> {
            performShutdown();
            Navigation.findNavController(v).navigate(R.id.action_controlScreen1_to_splashScreen1);
        });

        // Warmup UI components
        View layoutWarmup = view.findViewById(R.id.layoutWarmup);
        TextView txtWarmupCountdown = view.findViewById(R.id.txtWarmupCountdown);

        tvRoomName.setText(mShareModel.getSelectedControlDevice().getRoomName());

        mViewModel.getIsSystemInitialized().observe(getViewLifecycleOwner(), isInitialized -> {
            if (!isInitialized) {
                startWarmup(layoutWarmup, txtWarmupCountdown);
            } else {
                layoutWarmup.setVisibility(View.GONE);
            }
        });

//        mViewModel.getIsSwitcherFrontConnected().observe(getViewLifecycleOwner(), isConnected -> {
//            setLEDConnectionStatus(switchFrontLedIcon, switchFrontLedDesc, isConnected);
//            String statDesc = "Front Switch is " + (isConnected ? "online" : "offline");
//            switchFrontLedDesc.setText(statDesc);
//            isFrontSwitchConnected = isConnected;
//        });
//
//        mViewModel.getIsSwitcherSideConnected().observe(getViewLifecycleOwner(), isConnected -> {
//            setLEDConnectionStatus(switchSideLedIcon, switchSideLedDesc, isConnected);
//            String statDesc = "Side Switch is " + (isConnected ? "online" : "offline");
//            switchSideLedDesc.setText(statDesc);
//            isSideSwitchConnected = isConnected;
//        });
//
//        mViewModel.getIsTVFrontConnected().observe(getViewLifecycleOwner(), isConnected -> {
//            setLEDConnectionStatus(tvFrontLedIcon, tvFrontLedDesc, isConnected);
//            String statDesc = "Front TV is " + (isConnected ? "online" : "offline");
//            tvFrontLedDesc.setText(statDesc);
//            isFrontTVConnected = isConnected;
//        });
//
//        mViewModel.getIsTVSideConnected().observe(getViewLifecycleOwner(), isConnected -> {
//            setLEDConnectionStatus(tvSideLedIcon, tvSideLedDesc, isConnected);
//            String statDesc = "Side TV is " + (isConnected ? "online" : "offline");
//            tvSideLedDesc.setText(statDesc);
//            isSideTVConnected = isConnected;
//        });

        mViewModel.getDeviceConnectionStates().observe(getViewLifecycleOwner(), statesMap -> {
            if (statesMap == null)  return;
            boolean isConnected = false;
            if (statesMap.containsKey("192.168.1.31")) {
                isConnected = statesMap.get("192.168.1.31");
                setLEDConnectionStatus(switchFrontLedIcon, switchFrontLedDesc, isConnected);
            }
            if (statesMap.containsKey("192.168.1.32")) {
                setLEDConnectionStatus(tvFrontLedIcon, tvFrontLedDesc, statesMap.get("192.168.1.32"));
            }
            if (statesMap.containsKey("192.168.1.33")) {
                setLEDConnectionStatus(switchSideLedIcon, switchSideLedDesc, statesMap.get("192.168.1.32"));
            }
            if (statesMap.containsKey("192.168.1.34")) {
                setLEDConnectionStatus(tvSideLedIcon, tvSideLedDesc, statesMap.get("192.168.1.34"));
            }
        });

        mViewModel.establishConnection(mShareModel.getSelectedRoomDevices());
    }

    private void startWarmup(View layoutWarmup, TextView txtWarmupCountdown) {
        layoutWarmup.setVisibility(View.VISIBLE);

        if (warmupTimer != null) {
            warmupTimer.cancel();
        }

        warmupTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String secUntilFinished = (millisUntilFinished / 1000) + "s";
                txtWarmupCountdown.setText(secUntilFinished);
            }

            @Override
            public void onFinish() {
                mViewModel.setSystemInitialized(true);
            }
        }.start();
    }

    private void performShutdown() {
        if (warmupTimer != null) {
            warmupTimer.cancel();
        }

        // Execute the shutdown sequence in the ViewModel (includes recursive TV checks)

        // Final reset logic
        mViewModel.setSystemInitialized(false);
    }

    private void setLEDConnectionStatus(ImageView ledIcon, TextView tvDesc, boolean status) {
        int color;

        if(status) {
            color = ContextCompat.getColor(requireContext(), R.color.led_connected);
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.led_disconnected);
        }

        ledIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}