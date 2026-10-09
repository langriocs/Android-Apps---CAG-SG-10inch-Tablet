package com.avl.cag10inchApp.fragments;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.avl.cag10inchApp.adapter.ControlSwitchAdapter;
import com.avl.cag10inchApp.adapter.DisplayOutputAdapter;
import com.avl.cag10inchApp.adapter.DisplaySourceAdapter;
import com.avl.cag10inchApp.model.ControlSwitchItem;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.tv.TVInputSource;
import com.avl.cag10inchApp.viewmodel.CAGOPSPanelViewModel;
import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CAGOPSPanel extends Fragment {

    private CAGOPSPanelViewModel mViewModel;
    private ShareViewModel mShareModel;
    private TextView tvRoomName;
    private TextView tvSelectedDevice;
    private CountDownTimer warmupTimer;
    private View layoutWarmup;
    private TextView txtWarmupCountdown;
    private RecyclerView rvSelectSource;
    private RecyclerView rvSelectOutput;
    private RecyclerView rvControlSwitch;
    private List<DisplayOutputItem> displayOutputItems;
    private List<ControlSwitchItem> controlSwitchItems;
    private DisplayOutputAdapter displayOutputAdapter;
    private ControlSwitchAdapter displaySwitchControlAdapter;
    private DisplaySourceItem selectedSource;
    private DisplayOutputItem selectedOutput;
    private ImageView imgVolDown;
    private ImageView imgVolUp;
    private SeekBar seekBarVolume;
    private MaterialButton btnMute;
    private MaterialButton btnPower;
    private int volNum=50;
    private View layoutVideo;
    private View layoutControl;
    private View btnVideo;
    private View btnControl;
    private View btnHome;
    private ImageView imgDSPOnline;

    public static CAGOPSPanel newInstance() {
        return new CAGOPSPanel();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CAGOPSPanelViewModel.class);
        mShareModel = new ViewModelProvider(requireActivity()).get(ShareViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cagops_panel, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvRoomName = view.findViewById(R.id.txtRoomName);
        rvSelectSource = view.findViewById(R.id.rv_select_source);


        imgVolDown = view.findViewById(R.id.imgVolDown);
        imgVolUp = view.findViewById(R.id.imgVolUp);
        seekBarVolume = view.findViewById(R.id.seekBarVolume);
        btnMute = view.findViewById(R.id.btnMute);
        btnPower = view.findViewById(R.id.btnPower);
        tvSelectedDevice = view.findViewById(R.id.tv_selected_device);
        imgDSPOnline = view.findViewById(R.id.imgDSPOnline);

        // Warmup UI components
        layoutWarmup = view.findViewById(R.id.layoutWarmup);
        txtWarmupCountdown = view.findViewById(R.id.txtWarmupCountdown);

        tvRoomName.setText(mShareModel.getSelectedControlDevice().getRoomName());


        setupSourceDisplay(view);
        setupOutputDisplay(view);
        setupSwitchControlDisplay(view);
        setupModeNavigation(view);
        setupAudioControl();
        observerViewModel();
        fetchControlDevicesData();

    }

    @Override
    public void onDestroyView() {
        mViewModel.cleanup();
        super.onDestroyView();
    }

    private void observerViewModel() {
        mViewModel.getIsSystemInitialized().observe(getViewLifecycleOwner(), isInitialized -> {
            if (!isInitialized) {
                startWarmup(layoutWarmup, txtWarmupCountdown);
            } else {
                layoutWarmup.setVisibility(View.GONE);
            }
        });

        mViewModel.getDisplayOutputItems().observe(getViewLifecycleOwner(), displayOutputItems -> {

            List<Integer> order = Arrays.asList(
                    63,
                    64,
                    61,
                    62
            );

            List<DisplayOutputItem> items = displayOutputItems
                    .stream()
                    .filter(displayOutputItem -> {
                        return (!displayOutputItem.getDisplayName().equals("Switch") && !displayOutputItem.getDisplayName().equals("DSP"));
                    })
                    .sorted(Comparator.comparingInt(item -> {
                        switch (item.getDeviceId()) {
                            case 63:
                                return 1;
                            case 64:
                                return 2;
                            case 61:
                                return 3;
                            case 62:
                                return 4;
                            default:
                                return 0;

                        }
                    }))
                    .collect(Collectors.toList());

            displayOutputAdapter.setItems(items);
            displaySwitchControlAdapter.setItems(items);
        });

        mViewModel.getSelectedDeviceOutput().observe(getViewLifecycleOwner(), selectedOutput -> {
           if (selectedOutput != null) {
               tvSelectedDevice.setText(selectedOutput.getDescription().replaceFirst("^CAG OPS Room\\s*", ""));
           }
        });

        mViewModel.getIsMuted().observe(getViewLifecycleOwner(), isMuted -> {
            btnMute.setBackgroundResource(isMuted ? R.drawable.bg_rounded_card_selected : R.drawable.bg_rounded_card);
            btnMute.setIconResource(isMuted ? R.drawable.ic_volume_down : R.drawable.ic_volume_mute );
        });

        mViewModel.getVolume().observe(getViewLifecycleOwner(), volume -> {
            this.volNum = volume;
        });

        mViewModel.getIsDSPConnected().observe(getViewLifecycleOwner(), isDSPConnected -> {
            imgDSPOnline.setSelected(isDSPConnected);
        });
    }

    private void setupSourceDisplay(View v) {
        List<DisplaySourceItem> inputSources = new ArrayList<>();
        inputSources.add(new DisplaySourceItem("USB-C 1", 7, R.drawable.ic_usb_c, false));
        inputSources.add( new DisplaySourceItem("USB-C 2", 8, R.drawable.ic_usb_c, false));
        inputSources.add( new DisplaySourceItem("USB-C 3", 9, R.drawable.ic_usb_c, false));
        inputSources.add( new DisplaySourceItem("Wireless Share 1", 10, R.drawable.ic_wireless_share, false));
        inputSources.add(new DisplaySourceItem("Wireless Share 2", 11, R.drawable.ic_wireless_share, false));
        inputSources.add( new DisplaySourceItem("Apple TV", 13, R.drawable.ic_appletv, false));
        inputSources.add( new DisplaySourceItem("Star Hub", 12, R.drawable.ic_starhub, false));
        inputSources.add( new DisplaySourceItem("HDMI Direct 1", 1, R.drawable.ic_usb_c, true));
        inputSources.add( new DisplaySourceItem("HDMI Direct 2", 2, R.drawable.ic_usb_c, true));
        inputSources.add( new DisplaySourceItem("HDMI Direct 3", 3, R.drawable.ic_usb_c, true));
        inputSources.add( new DisplaySourceItem("HDMI Direct 4", 4, R.drawable.ic_usb_c, true));

        rvSelectSource = v.findViewById(R.id.rv_select_source);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),7, GridLayoutManager.VERTICAL,false);
        rvSelectSource.setLayoutManager(layoutManager);

        DisplaySourceAdapter adapter = new DisplaySourceAdapter(inputSources, sourceItem -> {
            selectedSource = sourceItem;

        });

        rvSelectSource.setAdapter(adapter);
    }

    private void setupOutputDisplay(View v) {
        rvSelectOutput = v.findViewById(R.id.rv_select_output);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),8, GridLayoutManager.VERTICAL,false);
        rvSelectOutput.setLayoutManager(layoutManager);

        displayOutputAdapter = new DisplayOutputAdapter(item -> {
            if (item == null) {
                return;
            }
            if (selectedSource == null ) {
                return;
            }

            selectedOutput = item;
            item.setSelectedSourceName(selectedSource.getDisplayName());
            displayOutputAdapter.notifyDataSetChanged();

            mViewModel.setSelectedDeviceOutput(selectedOutput);

            if (!selectedSource.isDirect()) { // from switch
                mViewModel.setSelectedDeviceTurnOn();
                mViewModel.changeTVInputSource(TVInputSource.HDMI_1);
                mViewModel.routeSourceToDevice(selectedSource, selectedOutput);
                mViewModel.routeAudio(selectedSource);
            }

            if (selectedSource.isDirect()) {                                    // from direct
                mViewModel.setSelectedDeviceTurnOn();
                mViewModel.changeTVInputSource(TVInputSource.HDMI_2);
            }
        });

        rvSelectOutput.setAdapter(displayOutputAdapter);
    }

    private void setupAudioControl() {
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

                mViewModel.changeVolume(volNum);
            }
        });

        btnMute.setOnClickListener(v -> {
            Boolean current = mViewModel.getIsMuted().getValue();
            mViewModel.changeMute(current == null || !current);
        });

        btnPower.setOnClickListener(v -> {
            mViewModel.turnOffTV();
            performShutdown();
            Navigation.findNavController(v).navigate(R.id.action_cagopsPanel_to_splashScreen1);
        });
    }

    private void setupSwitchControlDisplay(View v) {
        rvControlSwitch = v.findViewById(R.id.rv_control_switch);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),4, GridLayoutManager.VERTICAL,false);
        rvControlSwitch.setLayoutManager(layoutManager);

        displaySwitchControlAdapter = new ControlSwitchAdapter(item -> {
            mViewModel.setSelectedDeviceOutput(item);
            if (item.isTurnOn()) {
                mViewModel.setSelectedDeviceTurnOn();
            } else {
                mViewModel.setSelectedDeviceTurnOff();
            }
        });
        rvControlSwitch.setAdapter(displaySwitchControlAdapter);
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

//            for(RoomDevice roomDevice : controlRoomDevices.roomDevices) {
//                DisplayOutputItem displayOutputItem = new DisplayOutputItem(roomDevice, R.drawable.ic_output);
//                displayOutputItem.setDeviceStatusVisible(true);
//                displayOutputItems.add(displayOutputItem);
//            }

            RoomDevice roomDevice1 = new RoomDevice();
            roomDevice1.setId(63);
            roomDevice1.setDeviceName("TV");
            roomDevice1.setDeviceIpAddress("192.168.1.114");
            roomDevice1.setDevicePort(9761);
            roomDevice1.setDeviceDesc("Side Left TV");
            roomDevice1.setOutPort(11);

            DisplayOutputItem displayOutputItem1 = new DisplayOutputItem(roomDevice1, R.drawable.ic_output);
            displayOutputItem1.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem1);

            RoomDevice roomDevice2 = new RoomDevice();
            roomDevice2.setId(64);
            roomDevice2.setDeviceName("TV");
            roomDevice2.setDeviceIpAddress("192.168.1.115");
            roomDevice2.setDevicePort(9761);
            roomDevice2.setDeviceDesc("Side Right TV");
            roomDevice2.setOutPort(12);

            DisplayOutputItem displayOutputItem2 = new DisplayOutputItem(roomDevice2, R.drawable.ic_output);
            displayOutputItem2.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem2);

            RoomDevice roomDevice3 = new RoomDevice();
            roomDevice3.setId(61);
            roomDevice3.setDeviceName("TV");
            roomDevice3.setDeviceIpAddress("192.168.1.112");
            roomDevice3.setDevicePort(9761);
            roomDevice3.setDeviceDesc("Front Left TV");
            roomDevice3.setOutPort(9);

            DisplayOutputItem displayOutputItem3 = new DisplayOutputItem(roomDevice3, R.drawable.ic_output);
            displayOutputItem3.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem3);

            RoomDevice roomDevice4 = new RoomDevice();
            roomDevice4.setId(62);
            roomDevice4.setDeviceName("TV");
            roomDevice4.setDeviceIpAddress("192.168.1.113");
            roomDevice4.setDevicePort(9761);
            roomDevice4.setDeviceDesc("Front Right TV");
            roomDevice4.setOutPort(10);

            DisplayOutputItem displayOutputItem4 = new DisplayOutputItem(roomDevice4, R.drawable.ic_output);
            displayOutputItem4.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem4);

            RoomDevice roomDevice5 = new RoomDevice();
            roomDevice5.setId(60);
            roomDevice5.setDeviceName("Switch");
            roomDevice5.setDeviceIpAddress("192.168.1.151");
            roomDevice5.setDevicePort(8000);
            roomDevice5.setDeviceDesc("Switch");
            roomDevice5.setOutPort(0);

            DisplayOutputItem displayOutputItem5 = new DisplayOutputItem(roomDevice5, R.drawable.ic_output);
            displayOutputItem5.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem5);

            RoomDevice roomDevice6 = new RoomDevice();
            roomDevice6.setId(65);
            roomDevice6.setDeviceName("DSP");
            roomDevice6.setDeviceIpAddress("192.168.1.172");
            roomDevice6.setDevicePort(17300);
            roomDevice6.setDeviceDesc("CAG OPS DSP");
            roomDevice6.setOutPort(0);

            DisplayOutputItem displayOutputItem6 = new DisplayOutputItem(roomDevice6, R.drawable.ic_output);
            displayOutputItem6.setDeviceStatusVisible(true);
            displayOutputItems.add(displayOutputItem6);

            mViewModel.connectAllDevices(displayOutputItems);

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
                mViewModel.turnOffTV();
                Navigation.findNavController(v).navigate(R.id.action_cagopsPanel_to_splashScreen1);
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