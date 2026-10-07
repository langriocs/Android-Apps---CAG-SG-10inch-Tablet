package com.avl.cag10inchApp.fragments;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avl.cag10inchApp.model.vo.ControlDevice;
import com.avl.cag10inchApp.model.vo.ControlRoomDevices;
import com.avl.cag10inchApp.repository.audio.DSPChannel;
import com.avl.cag10inchApp.repository.tv.TVPowerState;
import com.avl.cag10inchApp.viewmodel.BriefingPanelViewModel;
import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

public class BriefingPanel extends Fragment {

    private BriefingPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private CountDownTimer warmupTimer;
    private int volNum = 32;
    boolean isTVConnected = false;
    boolean isSwitcherConnected = false;
    TVPowerState powerState = TVPowerState.UNKNOWN;
    int source;
    int output;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity()).get(BriefingPanelViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_briefing_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvRoomName = view.findViewById(R.id.txtRoomName);
        ConstraintLayout btnSourceUsbC = view.findViewById(R.id.btnSourceUsbC);
        ConstraintLayout btnSourceWireless = view.findViewById(R.id.btnSourceWireless);
        MaterialButton btnMute = view.findViewById(R.id.btnMute);
        MaterialButton btnPower = view.findViewById(R.id.btnPower);
        TextView txtSystemOnline = view.findViewById(R.id.txtSystemOnline);
        ImageView switchLedIcon = view.findViewById(R.id.imgSystemOnlineLed);
        ImageView tvLedIcon = view.findViewById(R.id.imgDisplayConnectedLed);
        TextView tvLedTVDesc = view.findViewById(R.id.txtDisplayConnected);
        ImageView imgDisplayIndicator = view.findViewById(R.id.imgStatusIndicator);
        TextView txtDisplayStatus = view.findViewById(R.id.txtDisplayStatus);
        SeekBar seekBarVolume = view.findViewById(R.id.seekBarVolume);
        ImageView imgVolDown = view.findViewById(R.id.imgVolDown);
        ImageView imgVolUp = view.findViewById(R.id.imgVolUp);

        btnSourceUsbC.setOnClickListener(v -> {
            fetchSourceHDMI();
            mViewModel.routeInputSourceTo(source, output);

        });

        btnSourceWireless.setOnClickListener(v -> {
            fetchSourceWireless();
            mViewModel.routeInputSourceToWireless(source, output);
        } );

        btnPower.setOnClickListener(v -> {
            mViewModel.turnOffTV();
            performShutdown();
            Navigation.findNavController(v).navigate(R.id.action_briefingPanel_to_splashScreen1);
        });

        btnMute.setOnClickListener(v -> {
            Boolean current = mViewModel.getDSPMuted().getValue();
            mViewModel.changeMute(current == null || !current);
        });

        imgVolDown.setOnClickListener(v -> {
            if (volNum > 0) {
                volNum = seekBarVolume.getProgress();
                volNum--;
                seekBarVolume.setProgress(volNum);
                mViewModel.changeVolume(volNum);
            }
        });

        imgVolUp.setOnClickListener(v -> {
            if (volNum < 100) {
                volNum = seekBarVolume.getProgress();
                volNum++;
                seekBarVolume.setProgress(volNum);
                mViewModel.changeVolume(volNum);
            }
        });

        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                volNum = seekBar.getProgress();

                int mappedValue = 311 + (volNum * 2);
                mViewModel.changeVolume(mappedValue);
            }
        });



        // Warmup UI components
        View layoutWarmup = view.findViewById(R.id.layoutWarmup);
        TextView txtWarmupCountdown = view.findViewById(R.id.txtWarmupCountdown);

        mShareModel.getControlRoomDevices().observe(getViewLifecycleOwner(), controlRoomDevices -> {

            if (controlRoomDevices != null) {
                tvRoomName.setText(controlRoomDevices.controlDevice.getRoomName());

                DSPChannel channel = fetchChannelByRoomId(controlRoomDevices.controlDevice.getId());

                if (controlRoomDevices.roomDevices != null) {
                    controlRoomDevices.roomDevices.forEach(roomDevice -> {
                        if (roomDevice.getDeviceName().equals("Switch")) {
                            mViewModel.connectSwitcher(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
                        } else if (roomDevice.getDeviceName().equals("TV")) {
                            mViewModel.connectTV(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
                        } else if (roomDevice.getDeviceName().equals("DSP")) {
                            mViewModel.connectDSP(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort(), channel);
                        }
                    });
                }
            }
        });

        mViewModel.getTvPowerState().observe(getViewLifecycleOwner(), state -> {
        });

        mViewModel.getIsUsbCSelected().observe(getViewLifecycleOwner(), isSelected -> {
            btnSourceUsbC.setBackgroundResource(isSelected ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
        });

        mViewModel.getIsWirelessSelected().observe(getViewLifecycleOwner(), isSelected -> {
            btnSourceWireless.setBackgroundResource(isSelected ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
        });

        mViewModel.getTVMuted().observe(getViewLifecycleOwner(), isMuted -> {
            btnMute.setBackgroundResource(isMuted ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
            btnMute.setIconResource(isMuted ? R.drawable.ic_volume_down : R.drawable.ic_volume_mute );
        });

        mViewModel.getTVVolume().observe(getViewLifecycleOwner(), volume -> {
            if (volume != null) {
                seekBarVolume.setProgress(volume);
            }
        });

        mViewModel.getDSPMuted().observe(getViewLifecycleOwner(), isMuted -> {
            btnMute.setBackgroundResource(isMuted ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
            btnMute.setIconResource(isMuted ? R.drawable.ic_volume_down : R.drawable.ic_volume_mute );
        });

        mViewModel.getDSPVolume().observe(getViewLifecycleOwner(), volume -> {

        });

        mViewModel.getIsDSPConnected().observe(getViewLifecycleOwner(), isConnected -> {

        });

        mViewModel.getIsSystemInitialized().observe(getViewLifecycleOwner(), isInitialized -> {
            if (!isInitialized) {
                startWarmup(layoutWarmup, txtWarmupCountdown);
            } else {
                layoutWarmup.setVisibility(View.GONE);
            }
        });

        mViewModel.getIsSwitcherConnected().observe(getViewLifecycleOwner(), isConnected -> {
            setLEDConnectionStatus(switchLedIcon,  isConnected);
            String statDesc = "System is " + (isConnected ? "online" : "offline");
            txtSystemOnline.setText(statDesc);
            isSwitcherConnected = isConnected;
        });

        mViewModel.getIsTVConnected().observe(getViewLifecycleOwner(), isConnected -> {
            setLEDConnectionStatus(tvLedIcon, isConnected);
            String statDesc = "Display " + (isConnected ? "connected" : "disconnected");
            tvLedTVDesc.setText(statDesc);
            isTVConnected = isConnected;

            // turn on the tv
            if (isConnected) {
                mViewModel.turnOnTV();
            } else {
                // disable and set the display to off

            }

        });
    }

    private DSPChannel fetchChannelByRoomId(int roomId) {
        if (roomId == 12) {
            return DSPChannel.CH_1;
        }
        if (roomId == 13) {
            return DSPChannel.CH_2;
        }

        return DSPChannel.CH_1;
    }

    private void fetchSourceHDMI() {
        ControlDevice controlDevice = mShareModel.getSelectedControlDevice();

        if (controlDevice.getId() == 12) { // Briefing Rm 1
            source = 1;
            output = 1;
        }
        if (controlDevice.getId() == 13) { // Briefing Rm 2
            source = 3;
            output = 2;
        }

    }
    private void fetchSourceWireless() {
        ControlDevice controlDevice = mShareModel.getSelectedControlDevice();

        if (controlDevice.getId() == 12) { // Briefing Rm 1
            source = 2;
            output = 1;
        }
        if (controlDevice.getId() == 13) { // Briefing Rm 2
            source = 4;
            output = 2;
        }
    }

    private void showAlert() {
        CustomAlertDialog alertScreenDialog = new CustomAlertDialog();
        alertScreenDialog.setTitle("Changi Airport Group");
        alertScreenDialog.setMessage("Device IP Address not found! Please contact the admin.");
        alertScreenDialog.show(getParentFragmentManager(), "alert dialog");
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

    private void setLEDConnectionStatus(ImageView ledIcon, boolean status) {
//        int color = ContextCompat.getColor(requireContext(), R.color.led_off);
        int color;

        if(status) {
            color = ContextCompat.getColor(requireContext(), R.color.led_connected);
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.led_disconnected);
        }

        ledIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    private void setLEDDisplayStatus(ImageView ledIcon, TextView txtDisplayStatus, TVPowerState status) {
        Drawable color = null;
        String desc = "";
        if(status == TVPowerState.ON) {
            color = ContextCompat.getDrawable(requireContext(), R.drawable.ic_led);
            desc = "Display is ON";
        }
        if (status == TVPowerState.OFF) {
            color = ContextCompat.getDrawable(requireContext(), R.drawable.ic_led_red);
            desc = "Display is OFF";
        }

        if (color == null) {
            return;
        }

        ledIcon.setImageDrawable(color);
        txtDisplayStatus.setText(desc);
    }

    private void performShutdown() {
        if (warmupTimer != null) {
            warmupTimer.cancel();
        }

        // Execute the shutdown sequence in the ViewModel (includes recursive TV checks)

        // Final reset logic
        mViewModel.setSystemInitialized(false);
    }


}