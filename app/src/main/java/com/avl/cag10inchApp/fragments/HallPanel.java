package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayMode;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.ledwall.LEDPreset;
import com.avl.cag10inchApp.viewmodel.HallPanelView;
import com.avl.cag10inchApp.viewmodel.HallPanelViewModel;
import com.avl.cag10inchApp.viewmodel.NavigationState;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class HallPanel extends Fragment {

    private HallPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private View layoutMenu;
    private View layoutDisplayMode;
    private View layoutSource;
    private View bottomNavigation;
    private ConstraintLayout btnDisplay;
    private ConstraintLayout btnSource;
    private ConstraintLayout btnAudio;
    private ConstraintLayout btnCamera;
    private ConstraintLayout btnHome;
    private LinearLayout outputSelectPanel;


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
        fetchControlDevicesData();
        observerViewModel();
    }

    private void setupMenu(View view) {
        MaterialButton btnSeparateHall = view.findViewById(R.id.btn_separation_hall);
        MaterialButton btnCombineHall = view.findViewById(R.id.btn_combine_hall);

        btnSeparateHall.setOnClickListener(v -> switchLayoutDisplay(HallPanelView.HALL_DISPLAY_MODE_SELECT));

        btnCombineHall.setOnClickListener(v -> switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT));
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
            mViewModel.setPreset(LEDPreset.PRESET_1);

            setNavigationButtonState(NavigationState.SOURCE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
            mViewModel.showSourceDisplay();
        });

        btnDoubleMode.setOnClickListener(v -> {
            btnSingleMode.setSelected(false);
            btnDoubleMode.setSelected(true);
            btnDirectMode.setSelected(false);

            mViewModel.setSelectedDisplayMode(DisplayMode.DOUBLE);
            mViewModel.setPreset(LEDPreset.PRESET_2);

            setNavigationButtonState(NavigationState.SOURCE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
            mViewModel.showSourceDisplay();
        });

        btnDirectMode.setOnClickListener(v -> {
            btnSingleMode.setSelected(false);
            btnDoubleMode.setSelected(false);
            btnDirectMode.setSelected(true);
            mViewModel.setSelectedDisplayMode(DisplayMode.DIRECT);
            mViewModel.setPreset(LEDPreset.PRESET_3);


//            setNavigationButtonState(NavigationState.SOURCE_STATE);
//            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
//            mViewModel.showSourceDisplay();
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
            btnHallLeftLedDisplay.setSelected(true);
            mViewModel.routeSourceToLeft();
        });

        btnHallRightLedDisplay.setOnClickListener(v -> {
            btnHallRightLedDisplay.setSelected(true);
            mViewModel.routeSourceToRight();
        });

    }

    private void setupLayoutDisplay(View view) {
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
            setNavigationButtonState(NavigationState.DISPLAY_MODE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_DISPLAY_MODE_SELECT);
        });

        btnSource.setOnClickListener(view1 -> {
            if (mViewModel.getSelectedDisplayMode() == null) {
                return;
            }
            if (mViewModel.getSelectedDisplayMode() == DisplayMode.DIRECT) {
                return;
            }
            setNavigationButtonState(NavigationState.SOURCE_STATE);
            switchLayoutDisplay(HallPanelView.HALL_SOURCE_SELECT);
            mViewModel.showSourceDisplay();
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
            mViewModel.setPreset(LEDPreset.PRESET_4);
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
            bottomNavigation.setVisibility(View.GONE);
        }
        if (hallPanel == HallPanelView.HALL_DISPLAY_MODE_SELECT) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.VISIBLE);
            layoutSource.setVisibility(View.GONE);
            bottomNavigation.setVisibility(View.VISIBLE);
        }

        if (hallPanel == HallPanelView.HALL_SOURCE_SELECT) {
            layoutMenu.setVisibility(View.GONE);
            layoutDisplayMode.setVisibility(View.GONE);
            layoutSource.setVisibility(View.VISIBLE);
            bottomNavigation.setVisibility(View.VISIBLE);
        }
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
    }
}