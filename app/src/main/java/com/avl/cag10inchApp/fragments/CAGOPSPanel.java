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

import com.avl.cag10inchApp.adapter.DisplayOutputAdapter;
import com.avl.cag10inchApp.adapter.DisplaySourceAdapter;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.viewmodel.CAGOPSPanelViewModel;
import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.viewmodel.ShareViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
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
    private List<DisplayOutputItem> displayOutputItems;
    private DisplayOutputAdapter displayOutputAdapter;
    private DisplaySourceItem selectedSource;
    private DisplayOutputItem selectedOutput;
    private ImageView imgVolDown;
    private ImageView imgVolUp;
    private SeekBar seekBarVolume;
    private MaterialButton btnMute;
    private MaterialButton btnPower;
    private int volNum;

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

        // Warmup UI components
        layoutWarmup = view.findViewById(R.id.layoutWarmup);
        txtWarmupCountdown = view.findViewById(R.id.txtWarmupCountdown);

        tvRoomName.setText(mShareModel.getSelectedControlDevice().getRoomName());

        setupSourceDisplay(view);
        setupOutputDisplay(view);
        setupControl();
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

            List<DisplayOutputItem> items = displayOutputItems
                    .stream()
                    .filter(displayOutputItem -> !displayOutputItem.getDisplayName().equals("Switch"))
                    .collect(Collectors.toList());

            displayOutputAdapter.setItems(items);
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
    }

    private void setupSourceDisplay(View v) {
        List<DisplaySourceItem> inputSources = new ArrayList<>();
        inputSources.add(new DisplaySourceItem("HDMI", 0, R.drawable.ic_wireless_share));
        inputSources.add( new DisplaySourceItem("HDMI", 7, R.drawable.ic_wireless_share));
        inputSources.add( new DisplaySourceItem("HDMI", 8, R.drawable.ic_starhub));
        inputSources.add( new DisplaySourceItem("HDMI", 9, R.drawable.ic_appletv));
        inputSources.add(new DisplaySourceItem("Wireless", 10, R.drawable.ic_wireless_share));
        inputSources.add( new DisplaySourceItem("Wireless", 11, R.drawable.ic_wireless_share));
        inputSources.add( new DisplaySourceItem("Star Hub", 12, R.drawable.ic_starhub));
        inputSources.add( new DisplaySourceItem("Apple TV", 13, R.drawable.ic_appletv));

        rvSelectSource = v.findViewById(R.id.rv_select_source);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),3, GridLayoutManager.VERTICAL,false);
        rvSelectSource.setLayoutManager(layoutManager);

        DisplaySourceAdapter adapter = new DisplaySourceAdapter(inputSources, sourceItem -> {
            selectedSource = sourceItem;

        });

        rvSelectSource.setAdapter(adapter);
    }

    private void setupOutputDisplay(View v) {
        rvSelectOutput = v.findViewById(R.id.rv_select_output);
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(),4, GridLayoutManager.VERTICAL,false);
        rvSelectOutput.setLayoutManager(layoutManager);

        displayOutputAdapter = new DisplayOutputAdapter(item -> {
            if (item == null) {
                return;
            }
            if (selectedSource == null ) {
                return;
            }


            selectedOutput = item;

            mViewModel.routeSourceToDevice(selectedSource, selectedOutput);
            mViewModel.setSelectedDeviceOutput(selectedOutput);

        });

        rvSelectOutput.setAdapter(displayOutputAdapter);
    }

    private void setupControl() {
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

    private void fetchControlDevicesData() {
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

            if (displayOutputItems != null) {
                displayOutputItems.clear();
                displayOutputItems = null;
            }

            displayOutputItems = new ArrayList<>();

            for(RoomDevice roomDevice : controlRoomDevices.roomDevices) {
                DisplayOutputItem displayOutputItem = new DisplayOutputItem(roomDevice, R.drawable.ic_output);
                displayOutputItem.setDeviceStatusVisible(true);
                displayOutputItems.add(displayOutputItem);
            }

            mViewModel.connectAllDevices(displayOutputItems);

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