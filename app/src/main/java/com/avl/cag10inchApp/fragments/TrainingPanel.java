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

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPChannel;
import com.avl.cag10inchApp.viewmodel.BriefingPanelViewModel;
import com.avl.cag10inchApp.viewmodel.ControlScreen2ViewModel;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.avl.cag10inchApp.viewmodel.TrainingPanelViewModel;

import java.util.ArrayList;
import java.util.List;

public class TrainingPanel extends Fragment {

    private TrainingPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private TextView tvRoomName;
    private DisplaySourceItem sourceUSB;
    private DisplaySourceItem sourceWireless;
    private DisplaySourceItem sourceMicrosoft;
    private DisplaySourceItem selectedSource;
    private View btnSourceUsbC;
    private View btnSourceWireless;
    private View btnMicrosoft;
    private List<DisplayOutputItem> displayOutputItems;


    public static TrainingPanel newInstance() {
        return new TrainingPanel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(TrainingPanelViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_training_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRoomName = view.findViewById(R.id.txtRoomName);
        View btnHome = view.findViewById(R.id.btn_home);

        btnHome.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_trainingPanel_to_splashScreen1);
        });

        setupSourceDisplay(view);
        setupAudioControl(view);
        observerViewModel();
        fetchControlDevicesData();
    }

    private void setupSourceDisplay(View v) {

        sourceUSB = new DisplaySourceItem("USB-C", 1, 0, true);
        sourceWireless = new DisplaySourceItem("USB", 4, 0, false);
        sourceMicrosoft = new DisplaySourceItem("Microsoft Teams", 6, 0, false);

        btnSourceUsbC = v.findViewById(R.id.btn_hdmi_direct);
        btnSourceWireless = v.findViewById(R.id.btn_wireless_share);
        btnMicrosoft = v.findViewById(R.id.btn_microsoft_team);

        btnSourceUsbC.setOnClickListener(view -> {
            selectedSource = sourceUSB;
            setSelectedSource(true, false, false);

            // route
            mViewModel.setSelectedSource(selectedSource);
            mViewModel.routeHDMIDirectToLEDWall();

        });
        btnSourceWireless.setOnClickListener(view -> {
            selectedSource = sourceWireless;
            setSelectedSource(false, true, false);

        });
        btnMicrosoft.setOnClickListener(view -> {
            selectedSource = sourceMicrosoft;
            setSelectedSource(false, false, true);
        });
    }

    private void setSelectedSource(boolean isUSBCSelected,
                                   boolean isWirelessSelected,
                                   boolean isMicrosoft) {
        btnSourceUsbC.setSelected(isUSBCSelected);
        btnSourceWireless.setSelected(isWirelessSelected);
        btnMicrosoft.setSelected(isMicrosoft);

    }

    private void setupAudioControl(View v) {
        View btnMute = v.findViewById(R.id.btnMute);
        View btnVolDown = v.findViewById(R.id.imgVolDown);
        View btnVolUp = v.findViewById(R.id.imgVolUp);
    }
    private void observerViewModel() {

    }

    private void fetchControlDevicesData() {
        mShareModel.getControlRoomDevices().observe(getViewLifecycleOwner(), controlRoomDevices -> {

            if (controlRoomDevices != null) {
                tvRoomName.setText(controlRoomDevices.controlDevice.getRoomName());

                DSPChannel channel = DSPChannel.CH_1; //fetchChannelByRoomId(controlRoomDevices.controlDevice.getId());

                if (displayOutputItems != null) {
                    displayOutputItems.clear();
                    displayOutputItems = null;
                }

                displayOutputItems = new ArrayList<>();

                if (controlRoomDevices.roomDevices != null) {
                    for(RoomDevice roomDevice : controlRoomDevices.roomDevices) {
                        DisplayOutputItem displayOutputItem = new DisplayOutputItem(roomDevice);
                        displayOutputItem.setDeviceStatusVisible(true);
                        displayOutputItems.add(displayOutputItem);
                    }

                    mViewModel.connectAllDevices(displayOutputItems);
                }
            }
        });
    }
}