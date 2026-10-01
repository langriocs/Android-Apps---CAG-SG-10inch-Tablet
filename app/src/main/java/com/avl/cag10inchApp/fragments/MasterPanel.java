package com.avl.cag10inchApp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.adapter.ControlSwitchAdapter;
import com.avl.cag10inchApp.adapter.DisplayOutputAdapter;
import com.avl.cag10inchApp.model.ControlSwitchItem;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.viewmodel.MasterPanelViewModel;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MasterPanel extends Fragment {

    private MasterPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private RecyclerView rvOutput;
    private RecyclerView rvControl;
    private List<DisplayOutputItem> displayOutputItems;
    private List<ControlSwitchItem> controlSwitchItems;
    private Integer buttonInputSelected = 0;

    private final Map<Integer, View> inputButtons = new HashMap<>();

    private View layoutVideo;
    private View layoutControl;
    private View btnVideo;
    private View btnControl;
    private View btnHome;
    private TextView tvRoomName;
    private CountDownTimer warmupTimer;
    private View layoutWarmup;
    private TextView txtWarmupCountdown;

    private DisplayOutputAdapter displayOutputAdapter;
//    private List<Integer> selectedOutput;

    public static MasterPanel newInstance() {
        return new MasterPanel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(MasterPanelViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_master_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRoomName = view.findViewById(R.id.txtRoomName);
        layoutWarmup = view.findViewById(R.id.layoutWarmup);
        txtWarmupCountdown = view.findViewById(R.id.txtWarmupCountdown);

        setupInputButtons(view);
        setupOutputDisplay(view);
        setupControlSwitch(view);
        setupModeNavigation(view);
        observerViewModel();
        fetchControlDevicesData();
    }

    private void setSelectedInput(Integer selectedInput) {
        buttonInputSelected = selectedInput;

        inputButtons.forEach((key, buttonView) -> {
            boolean isSelected = (Objects.equals(key, selectedInput));
            buttonView.setSelected(isSelected);
        });
    }

    private void setupInputButtons(View view) {
        inputButtons.put(1, view.findViewById(R.id.btn_hall1_usb1));
        inputButtons.put(2, view.findViewById(R.id.btn_hall1_usb2));
        inputButtons.put(3, view.findViewById(R.id.btn_hall1_wireless));
        inputButtons.put(4, view.findViewById(R.id.btn_hall2_usb1));
        inputButtons.put(5, view.findViewById(R.id.btn_hall2_usb2));
        inputButtons.put(6, view.findViewById(R.id.btn_hall2_wireless));
        inputButtons.put(7, view.findViewById(R.id.btn_cag_usb1));
        inputButtons.put(8, view.findViewById(R.id.btn_cag_usb2));
        inputButtons.put(9, view.findViewById(R.id.btn_cag_usb3));
        inputButtons.put(10, view.findViewById(R.id.btn_cag_wireless1));
        inputButtons.put(11, view.findViewById(R.id.btn_cag_wireless2));
        inputButtons.put(12, view.findViewById(R.id.btn_cag_starhub));
        inputButtons.put(13, view.findViewById(R.id.btn_cag_apple_tv));
        inputButtons.put(14, view.findViewById(R.id.btn_cag_cctv));

        inputButtons.forEach((key, buttonView) -> {
            if (buttonView != null) {
                buttonView.setOnClickListener(view1 -> setSelectedInput(key));
            }
        });
    }

    private void setupModeNavigation(View view) {
        layoutVideo = view.findViewById(R.id.layout_master_video);
        layoutControl = view.findViewById(R.id.layout_master_control);

        btnVideo = view.findViewById(R.id.btn_video);
        btnControl = view.findViewById(R.id.btn_control);
        btnHome = view.findViewById(R.id.btn_home);

        if (btnVideo != null) {
            btnVideo.setOnClickListener(v -> showVideoLayout());
        }

        if (btnControl != null) {
            btnControl.setOnClickListener(v -> showControlLayout());
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_masterPanel_to_splashScreen1);
            });
        }

        showVideoLayout();
    }

    private void showVideoLayout() {
        if (layoutVideo != null) {
            layoutVideo.setVisibility(View.VISIBLE);
        }
        if (layoutControl != null) {
            layoutControl.setVisibility(View.GONE);
        }

        updateModeButtonsHighlight(true);
    }

    private void showControlLayout() {
        if (layoutVideo != null) {
            layoutVideo.setVisibility(View.GONE);
        }
        if (layoutControl != null) {
            layoutControl.setVisibility(View.VISIBLE);
        }

        updateModeButtonsHighlight(false);
    }

    private void updateModeButtonsHighlight(boolean isVideoMode) {
        if (btnVideo != null) {
            btnVideo.setSelected(isVideoMode);
            btnVideo.setBackgroundResource(isVideoMode ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
        }

        if (btnControl != null) {
            btnControl.setSelected(!isVideoMode);
            btnControl.setBackgroundResource(!isVideoMode ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
        }
    }

    private void setupOutputDisplay(View view) {
//        selectedOutput = new ArrayList<>();
        rvOutput = view.findViewById(R.id.rv_display_output);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),8, GridLayoutManager.VERTICAL,false);
        rvOutput.setLayoutManager(layoutManager);

        displayOutputAdapter = new DisplayOutputAdapter(displayOutputItems, item -> {
            if (item == null) {
                return;
            }
            if (buttonInputSelected == null || buttonInputSelected == 0) {
                return;
            }
            mViewModel.routeSourceToDevice(buttonInputSelected, item);

            setSelectedInput(0);
        });

        rvOutput.setAdapter(displayOutputAdapter);

    }

    private void setupControlSwitch(View view) {
        rvControl = view.findViewById(R.id.rv_control_switch);
        controlSwitchItems = new ArrayList<>();
        controlSwitchItems.add(new ControlSwitchItem("HALL 1 LED Wall", false, 1, "192.168.1.152", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("Briefing Room 1", false, 2,  "192.168.1.122", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("Training Room LED", false, 3, "192.168.1.122", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("CAG Meeting Room", false, 4, "192.168.1.52", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("Airline Room 1", false,5, "192.168.1.12", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("HALL 2 Front LED WALL", false, 6, "192.168.1.162", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("Briefing Room 2", false, 7, "192.168.1.132", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("CAG OPS Room Front Left", false, 8, "192.168.1.112", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("PMA Holding Room", false, 9, "192.168.1.162", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("Airline Room 2", false, 10, "192.168.1.21", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("HALL 2 Side LED WALL", false, 11, "192.168.1.163", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("Boardroom Front LED", false, 12, "192.168.1.92", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("CAG OPS Room Front Right", false, 13, "192.168.1.113", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("Police OPS Room", false, 14, "192.168.1.72", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("CARE OPS Room Front", false, 15, "192.168.1.32", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("Temp", false, 0, "", 0, ""));
        controlSwitchItems.add(new ControlSwitchItem("Boardroom Side LED", false, 16, "192.168.1.93", 15200, "LED_WALL"));
        controlSwitchItems.add(new ControlSwitchItem("CAG OPS Room Side Left", false, 17, "192.168.1.114", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("CID OPS Room", false, 18, "192.168.1.82", 9761, "TV"));
        controlSwitchItems.add(new ControlSwitchItem("CARE OPS Room Side", false, 19, "192.168.1.34", 9761, "TV"));

        mViewModel.setControlSwitchItems(controlSwitchItems);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),5, GridLayoutManager.VERTICAL,false);
        rvControl.setLayoutManager(layoutManager);

        ControlSwitchAdapter adapter = new ControlSwitchAdapter(controlSwitchItems, item -> {
            mViewModel.switchControl(item);
        });
        rvControl.setAdapter(adapter);

//        mViewModel.updateControlSwitchItemsStatus();
    }

    private void observerViewModel() {

        mViewModel.getDisplayOutputItems().observe(getViewLifecycleOwner(), displayOutputItems -> {
            if (displayOutputItems == null) {
                return;
            }
            displayOutputAdapter.setItems(displayOutputItems);
        });

        mViewModel.getIsSystemInitialized().observe(getViewLifecycleOwner(), isInitialized -> {
            if (!isInitialized) {
                startWarmup(layoutWarmup, txtWarmupCountdown);
            } else {
                layoutWarmup.setVisibility(View.GONE);
            }
        });

        mViewModel.getIsSwitcherConnected().observe(getViewLifecycleOwner(), isConnected -> {
            Log.d("cag", "switcher connected:" + (isConnected ? "OK" : "DisConnected") );
        });
    }

    private void fetchControlDevicesData() {
        if (displayOutputItems != null) {
            displayOutputItems.clear();
            displayOutputItems = null;
        }

        displayOutputItems = new ArrayList<>();
        mShareModel.getControlRoomDevices().observe(getViewLifecycleOwner(), controlRoomDevices -> {
            if (controlRoomDevices == null) {
                return;
            }

            if (controlRoomDevices.roomDevices == null) {
                return;
            }

            if (controlRoomDevices.controlDevice != null) {
                tvRoomName.setText(controlRoomDevices.controlDevice.getRoomName());
            }

//            connect
            for(RoomDevice roomDevice : controlRoomDevices.roomDevices) {
                mViewModel.connectDevice(roomDevice);
            }

            mViewModel.setRoomDevices(controlRoomDevices.roomDevices);

        });
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.setSystemInitialized(false);

        if (rvOutput != null) {
            rvOutput.setAdapter(null);
        }

        rvOutput = null;
        inputButtons.clear();
        displayOutputItems.clear();
        controlSwitchItems.clear();
        warmupTimer.cancel();
        warmupTimer = null;

    }
}
