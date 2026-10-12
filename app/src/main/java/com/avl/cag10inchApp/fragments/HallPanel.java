package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.adapter.LEDPanelOutAdapter;
import com.avl.cag10inchApp.model.DisplayMode;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.Hall;
import com.avl.cag10inchApp.model.HallOperation;
import com.avl.cag10inchApp.model.LEDPanelOutput;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.audio.Preset;
import com.avl.cag10inchApp.repository.ledwall.LEDPreset;
import com.avl.cag10inchApp.viewmodel.HallPanelView;
import com.avl.cag10inchApp.viewmodel.HallPanelViewModel;
import com.avl.cag10inchApp.viewmodel.NavigationState;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HallPanel extends Fragment {

    private HallPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private View layoutMenu;
    private View layoutDisplayMode;
    private View layoutSource;
    private View layoutCombine;
    private View layoutCombineSource;
    private View bottomNavigation;
    private ConstraintLayout btnDisplay;
    private ConstraintLayout btnSource;
    private ConstraintLayout btnAudio;
    private ConstraintLayout btnCamera;
    private ConstraintLayout btnHome;
    private LinearLayout outputSelectPanel;
    private RecyclerView rcvOutput;
    private boolean isLeftLedSelected;
    private boolean isRightLedSelected;
    private LEDPanelOutAdapter ledOutputAdapter;
    private List<LEDPanelOutput> ledPanelOutputList;

    private View btnUSB1;
    private View btnUSB2;
    private View btnWireless;

    private View btnCombine2USB1;
    private View btnCombine2USB2;
    private View btnCombine2Wireless;


    public static HallPanel newInstance() {
        return new HallPanel();
    }

    private List<DisplayOutputItem> displayOutputItems;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(HallPanelViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hall_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupMenu(view);
        setupLayoutDisplay(view);
        setupDisplayMode(view);
        setupSource(view);
        setupCombineHallDisplayMode(view);
        setupOutputCombine(view);
        fetchControlDevicesData();
        observerViewModel();
    }

    private void setupMenu(View view) {
        MaterialButton btnSeparateHall = view.findViewById(R.id.btn_separation_hall);
        MaterialButton btnCombineHall = view.findViewById(R.id.btn_combine_hall);

        btnSeparateHall.setOnClickListener(v -> {
            switchLayoutDisplay(HallPanelView.HALL_DISPLAY_MODE_SELECT);
            mViewModel.presetRecall(Preset.RECALL_1);
        });

        btnCombineHall.setOnClickListener(v -> {
            switchLayoutDisplay(HallPanelView.HALL_COMBINE_MODE);
            mViewModel.presetRecall(Preset.RECALL_2);
        });
    }

    private void setupDisplayMode(View view) {
        ConstraintLayout btnSingleMode = view.findViewById(R.id.btn_single_mode);
        ConstraintLayout btnDoubleMode = view.findViewById(R.id.btn_double_mode);
        ConstraintLayout btnDirectMode = view.findViewById(R.id.btn_direct_mode);

        btnSingleMode.setOnClickListener(v -> {
            btnSingleMode.setSelected(true);
            btnDoubleMode.setSelected(false);
            btnDirectMode.setSelected(false);
            mViewModel.setSelectedDisplayMode(DisplayMode.SINGLE);
            mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_1);

            setNavigationButtonState(NavigationState.SOURCE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
            mViewModel.showSourceDisplay();
        });

        btnDoubleMode.setOnClickListener(v -> {
            btnSingleMode.setSelected(false);
            btnDoubleMode.setSelected(true);
            btnDirectMode.setSelected(false);

            mViewModel.setSelectedDisplayMode(DisplayMode.DOUBLE);
            mViewModel.setPreset(LEDPreset.PRESET_2, Hall.HALL_1);

            setNavigationButtonState(NavigationState.SOURCE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
            mViewModel.showSourceDisplay();
        });

        btnDirectMode.setOnClickListener(v -> {
            btnSingleMode.setSelected(false);
            btnDoubleMode.setSelected(false);
            btnDirectMode.setSelected(true);
            mViewModel.setSelectedDisplayMode(DisplayMode.DIRECT);
            mViewModel.setPreset(LEDPreset.PRESET_3, Hall.HALL_1);

        });
    }

    private void setupSource(View view) {
        ConstraintLayout btnHall1Usb1 = view.findViewById(R.id.btn_hall1_usb1);
        ConstraintLayout btnHall1Usb2 = view.findViewById(R.id.btn_hall1_usb2);
        ConstraintLayout btnHall1Wireless = view.findViewById(R.id.btn_hall1_wireless);
        outputSelectPanel = view.findViewById(R.id.output_select_panel);
        ConstraintLayout btnHallLeftLedDisplay = view.findViewById(R.id.btn_hall_left_led_display);
        ConstraintLayout btnHallRightLedDisplay = view.findViewById(R.id.btn_hall_right_led_display);

        DisplaySourceItem usb1 = new DisplaySourceItem("USB-C 1", 1, R.drawable.ic_usb_c, false );
        DisplaySourceItem usb2 = new DisplaySourceItem("USB-C 2", 2, R.drawable.ic_usb_c, false );
        DisplaySourceItem wireless = new DisplaySourceItem("Wireless Share", 3, R.drawable.ic_wireless_share, false );


        btnHall1Usb1.setOnClickListener(v -> {
            btnHall1Usb1.setSelected(true);
            btnHall1Usb2.setSelected(false);
            btnHall1Wireless.setSelected(false);
            mViewModel.setSelectedSource(usb1);
            if (mViewModel.getSelectedDisplayMode() == DisplayMode.SINGLE) {
                mViewModel.routeSourceToSingle();
            }

        });
        btnHall1Usb2.setOnClickListener(v -> {
            btnHall1Usb1.setSelected(false);
            btnHall1Usb2.setSelected(true);
            btnHall1Wireless.setSelected(false);
            mViewModel.setSelectedSource(usb2);
            if (mViewModel.getSelectedDisplayMode() == DisplayMode.SINGLE) {
                mViewModel.routeSourceToSingle();
            }

        });
        btnHall1Wireless.setOnClickListener(v -> {
            btnHall1Usb1.setSelected(false);
            btnHall1Usb2.setSelected(false);
            btnHall1Wireless.setSelected(true);
            mViewModel.setSelectedSource(wireless);
            if (mViewModel.getSelectedDisplayMode() == DisplayMode.SINGLE) {
                mViewModel.routeSourceToSingle();
            }
        });

        btnHallLeftLedDisplay.setOnClickListener(v -> {
            btnHallLeftLedDisplay.setSelected(isLeftLedSelected);
            mViewModel.routeSourceToLeft();
        });

        btnHallRightLedDisplay.setOnClickListener(v -> {
            btnHallRightLedDisplay.setSelected(isRightLedSelected);
            mViewModel.routeSourceToRight();
        });

    }

    private void setupLayoutDisplay(View view) {
        layoutCombine = view.findViewById(R.id.hall_combine);
        layoutCombineSource = view.findViewById(R.id.hall_combine_source);
        layoutDisplayMode = view.findViewById(R.id.hall_display_mode);
        layoutMenu = view.findViewById(R.id.hall_menu);
        layoutSource = view.findViewById(R.id.hall_source);

        bottomNavigation = view.findViewById(R.id.bottom_navigation);
        btnDisplay = view.findViewById(R.id.btn_display);
        btnSource = view.findViewById(R.id.btn_source);
        btnAudio = view.findViewById(R.id.btn_audio);
        btnCamera = view.findViewById(R.id.btn_camera);
        btnHome = view.findViewById(R.id.btn_home);

        setNavigationButtonState(NavigationState.DISPLAY_MODE_STATE);

        btnDisplay.setOnClickListener(v -> {
            if (mViewModel.getHallOperation() == HallOperation.COMBINE_HALL) {
                setNavigationButtonState(NavigationState.DISPLAY_MODE_STATE);
                switchLayoutDisplay(HallPanelView.HALL_COMBINE_MODE);
            }
            if (mViewModel.getHallOperation() == HallOperation.SEPARATE_HALL) {
                setNavigationButtonState(NavigationState.DISPLAY_MODE_STATE);
                switchLayoutDisplay(HallPanelView.HALL_DISPLAY_MODE_SELECT);
            }

        });

        btnSource.setOnClickListener(view1 -> {
            if (mViewModel.getHallOperation() == HallOperation.SEPARATE_HALL) {
                if (mViewModel.getSelectedDisplayMode() == null) {
                    return;
                }
                if (mViewModel.getSelectedDisplayMode() == DisplayMode.DIRECT) {
                    return;
                }
                setNavigationButtonState(NavigationState.SOURCE_STATE);
                switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
                mViewModel.showSourceDisplay();
            }

            if (mViewModel.getHallOperation() == HallOperation.COMBINE_HALL) {
                setNavigationButtonState(NavigationState.SOURCE_STATE);
                switchLayoutDisplay(HallPanelView.HALL_COMBINE_SOURCE);
                mViewModel.showSourceCombineDisplay();
            }
        });

        btnAudio.setOnClickListener(view1 -> {
            setNavigationButtonState(NavigationState.AUDIO_STATE);
            btnAudio.setSelected(true);

        });

        btnCamera.setOnClickListener(view1 -> {
            setNavigationButtonState(NavigationState.CAMERA_STATE);
            btnCamera.setSelected(true);
        });

        btnHome.setOnClickListener(view1 -> {
            mViewModel.setPreset(LEDPreset.PRESET_4, Hall.HALL_1);
            btnHome.setSelected(true);
            Navigation.findNavController(view1).navigate(R.id.action_hallPanel_to_splashScreen1);
        });
    }

    private void setNavigationButtonState(NavigationState state) {
        btnDisplay.setSelected(false);
        btnSource.setSelected(false);
        btnAudio.setSelected(false);
        btnCamera.setSelected(false);

        if (state == NavigationState.DISPLAY_MODE_STATE) {
            btnDisplay.setSelected(true);
        }
        if (state == NavigationState.SOURCE_STATE) {
            btnSource.setSelected(true);
        }
        if (state == NavigationState.AUDIO_STATE) {
            btnAudio.setSelected(true);
        }
        if (state == NavigationState.CAMERA_STATE) {
            btnCamera.setSelected(true);
        }

    }

    private void switchLayoutDisplay(HallPanelView hallPanel) {
        if (hallPanel == HallPanelView.HALL_MENU) {
            layoutMenu.setVisibility(View.VISIBLE);
            layoutDisplayMode.setVisibility(View.GONE);
            layoutSource.setVisibility(View.GONE);
            layoutCombine.setVisibility(View.GONE);
            layoutCombineSource.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.GONE);
            mViewModel.setHallOperation(HallOperation.HALL_MENU);
        }

        if (hallPanel == HallPanelView.HALL_DISPLAY_MODE_SELECT) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.VISIBLE);
            layoutSource.setVisibility(View.GONE);
            layoutCombine.setVisibility(View.GONE);
            layoutCombineSource.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.VISIBLE);
            mViewModel.setHallOperation(HallOperation.SEPARATE_HALL);
        }

        if (hallPanel == HallPanelView.HALL_SOURCE_SELECT) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.GONE);
            layoutSource.setVisibility(View.VISIBLE);
            layoutCombine.setVisibility(View.GONE);
            layoutCombineSource.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.VISIBLE);
            mViewModel.setHallOperation(HallOperation.SEPARATE_HALL);
        }

        if (hallPanel == HallPanelView.HALL_COMBINE_MODE) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.GONE);
            layoutSource.setVisibility(View.GONE);
            layoutCombine.setVisibility(View.VISIBLE);
            layoutCombineSource.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.VISIBLE);
            mViewModel.setHallOperation(HallOperation.COMBINE_HALL);
        }

        if (hallPanel == HallPanelView.HALL_COMBINE_SOURCE) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.GONE);
            layoutSource.setVisibility(View.GONE);
            layoutCombine.setVisibility(View.GONE);
            layoutCombineSource.setVisibility(View.VISIBLE);
            bottomNavigation.setVisibility(View.VISIBLE);
            mViewModel.setHallOperation(HallOperation.COMBINE_HALL);
        }
    }

    private void setupCombineHallDisplayMode(View view){
        View btn1SingleMode = view.findViewById(R.id.btn_1_single_mode);
        View btn1DoubleMode = view.findViewById(R.id.btn_1_double_mode);
        View btn1DirectMode = view.findViewById(R.id.btn_1_direct_mode);
        View btn2ASingleMode = view.findViewById(R.id.btn_2A_single_mode);
        View btn2ADoubleMode = view.findViewById(R.id.btn_2A_double_mode);
        View btn2ADirectMode = view.findViewById(R.id.btn_2A_direct_mode);
        View btn2BSingleMode = view.findViewById(R.id.btn_2B_single_mode);
        View btn2BDoubleMode = view.findViewById(R.id.btn_2B_double_mode);
        View btn2BDirectMode = view.findViewById(R.id.btn_2B_direct_mode);

        btn1SingleMode.setSelected(true);
        btn2ASingleMode.setSelected(true);
        btn2BSingleMode.setSelected(true);
        mViewModel.setSelectedHall1CombineDisplayMode(DisplayMode.SINGLE);
        mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_1);
        mViewModel.setSelectedHall2ACombineDisplayMode(DisplayMode.SINGLE);
        mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_2A);
        mViewModel.setSelectedHall2BCombineDisplayMode(DisplayMode.SINGLE);
        mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_2B);


        btn1SingleMode.setOnClickListener(v -> {
            btn1SingleMode.setSelected(true);
            btn1DoubleMode.setSelected(false);
            btn1DirectMode.setSelected(false);

            mViewModel.setSelectedHall1CombineDisplayMode(DisplayMode.SINGLE);
            mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_1);


        });
        btn1DoubleMode.setOnClickListener(v -> {
            btn1SingleMode.setSelected(false);
            btn1DoubleMode.setSelected(true);
            btn1DirectMode.setSelected(false);
            mViewModel.setSelectedHall1CombineDisplayMode(DisplayMode.DOUBLE);
            mViewModel.setPreset(LEDPreset.PRESET_2, Hall.HALL_1);

        });
        btn1DirectMode.setOnClickListener(v -> {
            btn1SingleMode.setSelected(false);
            btn1DoubleMode.setSelected(false);
            btn1DirectMode.setSelected(true);

            mViewModel.setSelectedHall1CombineDisplayMode(DisplayMode.DIRECT);
            mViewModel.setPreset(LEDPreset.PRESET_3, Hall.HALL_1);
        });

        btn2ASingleMode.setOnClickListener(v -> {
           btn2ASingleMode.setSelected(true);
           btn2ADoubleMode.setSelected(false);
           btn2ADirectMode.setSelected(false);

           mViewModel.setSelectedHall2ACombineDisplayMode(DisplayMode.SINGLE);
           mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_2A);
        });
        btn2ADoubleMode.setOnClickListener(v -> {
            btn2ASingleMode.setSelected(false);
            btn2ADoubleMode.setSelected(true);
            btn2ADirectMode.setSelected(false);

            mViewModel.setSelectedHall2ACombineDisplayMode(DisplayMode.DOUBLE);
            mViewModel.setPreset(LEDPreset.PRESET_2, Hall.HALL_2A);
        });
        btn2ADirectMode.setOnClickListener(v -> {
            btn2ASingleMode.setSelected(false);
            btn2ADoubleMode.setSelected(false);
            btn2ADirectMode.setSelected(true);

            mViewModel.setSelectedDisplayMode(DisplayMode.DIRECT);
            mViewModel.setPreset(LEDPreset.PRESET_3, Hall.HALL_2A);
        });
        btn2BSingleMode.setOnClickListener(v -> {
            btn2BSingleMode.setSelected(true);
            btn2BDoubleMode.setSelected(false);
            btn2BDirectMode.setSelected(false);

            mViewModel.setSelectedHall2BCombineDisplayMode(DisplayMode.SINGLE);
            mViewModel.setPreset(LEDPreset.PRESET_1, Hall.HALL_2B);
        });
        btn2BDoubleMode.setOnClickListener(v -> {
            btn2BSingleMode.setSelected(false);
            btn2BDoubleMode.setSelected(true);
            btn2BDirectMode.setSelected(false);

            mViewModel.setSelectedHall2BCombineDisplayMode(DisplayMode.DOUBLE);
            mViewModel.setPreset(LEDPreset.PRESET_2, Hall.HALL_2B);
        });
        btn2BDirectMode.setOnClickListener(v -> {
            btn2BSingleMode.setSelected(false);
            btn2BDoubleMode.setSelected(false);
            btn2BDirectMode.setSelected(true);

            mViewModel.setSelectedDisplayMode(DisplayMode.DIRECT);
            mViewModel.setPreset(LEDPreset.PRESET_3, Hall.HALL_2B);
        });

    }

    private void setupOutputCombine(View view) {

        rcvOutput = view.findViewById(R.id.rcv_output);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false);
        rcvOutput.setLayoutManager(layoutManager);

        ledOutputAdapter = new LEDPanelOutAdapter(item -> {
            if (item != null) {
                return;
            }
            mViewModel.routeSourceTo(item.getOutport());
            mViewModel.routeDSP();
        });

        rcvOutput.setAdapter(ledOutputAdapter);

        btnUSB1 = view.findViewById(R.id.btn_combine_usb1);
        btnUSB2 = view.findViewById(R.id.btn_combine_usb2);
        btnWireless = view.findViewById(R.id.btn_combine_wireless);

        btnCombine2USB1 = view.findViewById(R.id.btn_combine2_usb1);
        btnCombine2USB2 = view.findViewById(R.id.btn_combine2_usb2);
        btnCombine2Wireless = view.findViewById(R.id.btn_combine2_wireless);


        DisplaySourceItem usb1 = new DisplaySourceItem("USB-C 1", 1, R.drawable.ic_usb_c, false );
        DisplaySourceItem usb2 = new DisplaySourceItem("USB-C 2", 2, R.drawable.ic_usb_c, false );
        DisplaySourceItem wireless = new DisplaySourceItem("Wireless Share", 3, R.drawable.ic_wireless_share, false );
        DisplaySourceItem combineUsb1 = new DisplaySourceItem("USB-C 1", 4, R.drawable.ic_usb_c, false );
        DisplaySourceItem combineUsb2 = new DisplaySourceItem("USB-C 2", 5, R.drawable.ic_usb_c, false );
        DisplaySourceItem combineWireless = new DisplaySourceItem("Wireless Share", 6, R.drawable.ic_wireless_share, false );

        btnUSB1.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnUSB1.setSelected(true);
            mViewModel.setSelectedSource(usb1);
        });

        btnUSB2.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnUSB2.setSelected(true);
            mViewModel.setSelectedSource(usb2);
        });

        btnWireless.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnWireless.setSelected(true);
            mViewModel.setSelectedSource(wireless);
        });

        btnCombine2USB1.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnCombine2USB1.setSelected(true);
            mViewModel.setSelectedSource(combineUsb1);
        });
        btnCombine2USB2.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnCombine2USB2.setSelected(true);
            mViewModel.setSelectedSource(combineUsb2);
        });
        btnCombine2Wireless.setOnClickListener(view1 -> {
            setCombineHallButtonState();
            btnCombine2Wireless.setSelected(true);
            mViewModel.setSelectedSource(combineWireless);
        });
    }

    private void setCombineHallButtonState() {
        btnUSB1.setSelected(false);
        btnUSB2.setSelected(false);
        btnWireless.setSelected(false);

        btnCombine2USB1.setSelected(false);
        btnCombine2USB2.setSelected(false);
        btnCombine2Wireless.setSelected(false);
    }

    private void fetchControlDevicesData() {
        mShareModel.getControlRoomDevices().observe(getViewLifecycleOwner(), controlRoomDevices -> {
            if (controlRoomDevices == null) {
                return;
            }

            if (controlRoomDevices.roomDevices == null) {
                return;
            }

            if (displayOutputItems != null) {
                displayOutputItems.clear();
                displayOutputItems = null;
            } else {
                displayOutputItems = new ArrayList<>();
            }

            for(RoomDevice roomDevice : controlRoomDevices.roomDevices) {
                DisplayOutputItem displayOutputItem = new DisplayOutputItem(roomDevice, R.drawable.ic_output);
                displayOutputItem.setDeviceStatusVisible(true);
                displayOutputItems.add(displayOutputItem);
            }

            RoomDevice roomDevice1 = new RoomDevice();
            roomDevice1.setId(71);
            roomDevice1.setDeviceName("LED_WALL");
            roomDevice1.setDeviceIpAddress("192.168.1.162");
            roomDevice1.setDevicePort(15200);
            roomDevice1.setDeviceDesc("Front TV");
            roomDevice1.setOutPort(3);

            DisplayOutputItem displayOutputItem1 = new DisplayOutputItem(roomDevice1, R.drawable.ic_output);
            displayOutputItem1.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem1);

            RoomDevice roomDevice2 = new RoomDevice();
            roomDevice2.setId(72);
            roomDevice2.setDeviceName("LED_WALL");
            roomDevice2.setDeviceIpAddress("192.168.1.163");
            roomDevice2.setDevicePort(15200);
            roomDevice2.setDeviceDesc("Side TV");
            roomDevice2.setOutPort(5);

            DisplayOutputItem displayOutputItem2 = new DisplayOutputItem(roomDevice2, R.drawable.ic_output);
            displayOutputItem2.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem2);

            RoomDevice roomDevice3 = new RoomDevice();
            roomDevice3.setId(73);
            roomDevice3.setDeviceName("DSP");
            roomDevice3.setDeviceIpAddress("192.168.1.164");
            roomDevice3.setDevicePort(17300);
            roomDevice3.setDeviceDesc("DSP");
            roomDevice3.setOutPort(1);

            DisplayOutputItem displayOutputItem3 = new DisplayOutputItem(roomDevice3, R.drawable.ic_output);
            displayOutputItem3.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem3);

            mViewModel.connectAllDevices(displayOutputItems);

        });
    }

    private void observerViewModel() {
        mViewModel.isLEDConnected().observe(getViewLifecycleOwner(), isLEDConnected -> {

        });

        mViewModel.getDisplayMode().observe(getViewLifecycleOwner(), displayMode -> {
            if (displayMode == null) {
                return;
            }
            if (displayMode == DisplayMode.SINGLE) {
                // dnt show Display Out Device
                outputSelectPanel.setVisibility(View.GONE);
            }
            if (displayMode == DisplayMode.DOUBLE) {
                // show double display out device
                outputSelectPanel.setVisibility(View.VISIBLE);
            }
            if (displayMode == DisplayMode.DIRECT) {

            }

        });

        mViewModel.getDisplayOutputItems().observe(getViewLifecycleOwner(), displayOutputItems -> {
            // update the display output items;
            if (displayOutputItems == null) {
                return;
            }

//            List<DisplayOutputItem> displayOutputItemList = displayOutputItems.stream().filter(displayOutputItem -> {
//                return displayOutputItem.getDisplayName().equals("LED_WALL");
//            }).collect(Collectors.toList());
//
//            if (displayOutputItemList == null) {
//                return;
//            }
//
//            for (DisplayOutputItem item : displayOutputItemList) {
//                if (item != null) {
//                    LEDPanelOutput ledPanelOutput = new LEDPanelOutput(item.getDisplayName(), R.drawable.ic_output);
//                    ledPanelOutputList.add(ledPanelOutput);
//                }
//            }
//
//            ledOutputAdapter.setItems(ledPanelOutputList);
        });

        mViewModel.getLedPanelOutputList().observe(getViewLifecycleOwner(), ledPanelOutputList -> {
            if (ledPanelOutputList == null) {
                return;
            }

            ledOutputAdapter.setItems(ledPanelOutputList);

        });
    }
}