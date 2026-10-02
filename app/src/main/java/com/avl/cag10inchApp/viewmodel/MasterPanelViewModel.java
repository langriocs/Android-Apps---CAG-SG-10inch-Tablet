package com.avl.cag10inchApp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.ControlSwitchItem;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPRepository;
import com.avl.cag10inchApp.repository.audio.IDSPRepository;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallRepository;
import com.avl.cag10inchApp.repository.ledwall.LEDWallRepository;
import com.avl.cag10inchApp.repository.switcher.ISwitchListener;
import com.avl.cag10inchApp.repository.switcher.ISwitchRepository;
import com.avl.cag10inchApp.repository.switcher.Switch32x32Repository;
import com.avl.cag10inchApp.repository.tv.ITVListener;
import com.avl.cag10inchApp.repository.tv.ITVRepository;
import com.avl.cag10inchApp.repository.tv.LGTVRepository;
import com.avl.cag10inchApp.repository.tv.TVPowerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterPanelViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLEDConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTVConnected = new MutableLiveData<>(false);

    private final MutableLiveData<List<DisplayOutputItem>> displayOutputItems = new MutableLiveData<>(new ArrayList<>());

    private final Map<Integer, IRoomDevice> outputDevices = new HashMap<>();
    private final Map<String, IRoomDevice> controlDeviceItems = new HashMap<>();
    private ISwitchRepository parentSwitch;

    public MasterPanelViewModel() {
//        setupSwitchListener();
//        setupLEDListener();
//        setupTVListener();
    }

    public LiveData<Boolean> getIsSwitcherConnected() {
        return isSwitcherConnected;
    }

    public LiveData<Boolean> getIsLEDConnected() {
        return isLEDConnected;
    }

    public LiveData<Boolean> getIsSystemInitialized() {
        return isSystemInitialized;
    }

    public LiveData<List<DisplayOutputItem>> getDisplayOutputItems() {
        return displayOutputItems;
    }


    public void setSystemInitialized(boolean initialized) {
        this.isSystemInitialized.postValue(initialized);
    }

    public void setRoomDevices(List<RoomDevice> roomDevices) {
        if (roomDevices == null) {
            displayOutputItems.setValue(new ArrayList<>());
            return;
        }

        List<DisplayOutputItem> items = new ArrayList<>();

        for (RoomDevice roomDevice : roomDevices) {
            if ((roomDevice == null) || roomDevice.getDeviceName() == null) {
                continue;
            }

            String deviceName = roomDevice.getDeviceName();
            boolean validDevice = deviceName.equals("LED_WALL") ||
                    deviceName.equals("LED_AUDIO") ||
                    deviceName.equals("TV") ||
                    deviceName.equals("Switch") && (roomDevice.getParentId() > 0);


            if (!validDevice) {
                continue;
            }

            DisplayOutputItem item =
                    new DisplayOutputItem(
                            roomDevice.getId(),
                            roomDevice.getDeviceDesc(),
                            R.drawable.ic_display,
                            roomDevice.getOutPort(),
                            roomDevice.getDeviceName(),
                            roomDevice
                    );

            items.add(item);
        }

        displayOutputItems.setValue(items);


    }

    public void routeSourceToDevice(int selectedInput, DisplayOutputItem selectedOutput) {

        if (parentSwitch == null) {
            return;
        }

//        route AV Hall Matrix to selected output
        parentSwitch.routeAV(selectedInput, selectedOutput.getPortNumber());

        IRoomDevice roomDevice = outputDevices.get(selectedOutput.getDeviceId());
        if (roomDevice instanceof LEDWallRepository) {
            ((LEDWallRepository) roomDevice).setPreset(1);
        }

        if (roomDevice instanceof LGTVRepository ) {
            ((LGTVRepository) roomDevice).turnOn();
        }

        if (roomDevice instanceof Switch32x32Repository) {
            ((Switch32x32Repository) roomDevice).routeAV(5, 1);
        }

        if (roomDevice instanceof DSPRepository) {
            ((DSPRepository) roomDevice).setMute();
        }
    }

    public void setControlSwitchItems(List<ControlSwitchItem> controlSwitchItems) {
        for (ControlSwitchItem controlSwitchItem : controlSwitchItems) {
            if (controlSwitchItem.getDeviceType().equals("TV")) {
                ITVRepository tvRepository = new LGTVRepository();
                tvRepository.setListener(new ITVListener() {
                    @Override
                    public void onConnected() {
                        // query for power status
                        tvRepository.getStatus();
                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onPowerStateChanged(TVPowerState state) {
                        if (state == TVPowerState.ON) {
                            controlSwitchItem.setState(true);
                        }

                        if (state == TVPowerState.OFF) {
                            controlSwitchItem.setState(false);
                        }

                    }

                    @Override
                    public void onVolumeChanged(int volume) {

                    }

                    @Override
                    public void onMuteChanged(boolean isMuted) {

                    }

                    @Override
                    public void onError(String message) {

                    }
                });
                ((IRoomDevice) tvRepository).connect(controlSwitchItem.getIpAddress(), controlSwitchItem.getPortNumber());
                controlDeviceItems.put(controlSwitchItem.getIpAddress(), (IRoomDevice) tvRepository);
            }
            if (controlSwitchItem.getDeviceType().equals("LED_WALL")) {
                ILEDWallRepository ledWallRepository = new LEDWallRepository();
                ledWallRepository.setListener(new ILEDWallListener() {
                    @Override
                    public void onConnected() {
                        // query for power status here
                        ledWallRepository.getStatus();
                    }

                    @Override
                    public void onDisconnected() {

                    }
                });
                ((IRoomDevice) ledWallRepository).connect(controlSwitchItem.getIpAddress(), controlSwitchItem.getPortNumber());
                controlDeviceItems.put(controlSwitchItem.getIpAddress(), (IRoomDevice) ledWallRepository);
            }
        }
    }

    public void switchControl(ControlSwitchItem controlSwitchItem) {

        IRoomDevice roomDevice = controlDeviceItems.get(controlSwitchItem.getIpAddress());

        if (roomDevice instanceof LEDWallRepository) {
            ((LEDWallRepository) roomDevice).setPreset(1);
        }

        if (roomDevice instanceof LGTVRepository ) {
            ((LGTVRepository) roomDevice).turnOn();
        }
    }

    public void connectDevice(RoomDevice displayOutputItem) {
        if (displayOutputItem == null || displayOutputItem.getDeviceName() == null) {
            return;
        }

        if (displayOutputItem.getDeviceName().equals("Switch")) {
            connectSwitcher(displayOutputItem);
        } else if (displayOutputItem.getDeviceName().equals("TV")) {
            connectTV(displayOutputItem);
        } else if (displayOutputItem.getDeviceName().equals("LED_WALL")) {
            connectLEDWall(displayOutputItem);
        } else if (displayOutputItem.getDeviceName().equals("LED_AUDIO")) {
            connectDSP(displayOutputItem);
        }
    }

    private void connectDSP(RoomDevice roomDevice) {
        IRoomDevice ledAudioRepository = new DSPRepository();
        ((IDSPRepository) ledAudioRepository).setListener(new ILEDWallListener() {

            @Override
            public void onConnected() {
                isLEDConnected.postValue(true);
            }

            @Override
            public void onDisconnected() {
                isLEDConnected.postValue(false);
            }
        });


        ledAudioRepository.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
        ledAudioRepository.setRoomDevice(roomDevice);
        outputDevices.put(roomDevice.getId(), ledAudioRepository);
    }

    private void connectSwitcher(RoomDevice roomDevice) {
        IRoomDevice switchRepository = new Switch32x32Repository();
        ((ISwitchRepository) switchRepository).setListener(new ISwitchListener() {

            @Override
            public void onConnected() {
                isSwitcherConnected.postValue(true);
            }

            @Override
            public void onDisconnected() {
                isSwitcherConnected.postValue(false);
            }
        });

        switchRepository.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
        switchRepository.setRoomDevice(roomDevice);
        if (roomDevice.getParentId() == 0) {
            parentSwitch = (ISwitchRepository) switchRepository;
        } else {
            outputDevices.put(roomDevice.getId(), switchRepository);
        }
    }

    private void connectLEDWall(RoomDevice roomDevice) {
        IRoomDevice ledWallRepository = new LEDWallRepository();
        ((ILEDWallRepository) ledWallRepository).setListener(new ILEDWallListener() {

            @Override
            public void onConnected() {
                isLEDConnected.postValue(true);
            }

            @Override
            public void onDisconnected() {
                isLEDConnected.postValue(false);
            }
        });

        ledWallRepository.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
        ledWallRepository.setRoomDevice(roomDevice);
        outputDevices.put(roomDevice.getId(), ledWallRepository);
    }

    private void connectTV(RoomDevice roomDevice) {
        IRoomDevice tvRepository = new LGTVRepository();
        ((ITVRepository) tvRepository).setListener(new ITVListener() {

            @Override
            public void onConnected() {
                isTVConnected.postValue(true);
                ((ITVRepository) tvRepository).getStatus();
            }

            @Override
            public void onDisconnected() {
                isTVConnected.postValue(false);
            }

            @Override
            public void onPowerStateChanged(TVPowerState state) {
                boolean isOn = state == TVPowerState.ON;
                updateDisplayPowerState(roomDevice.getId(), isOn);
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onMuteChanged(boolean isMuted) {
            }

            @Override
            public void onError(String message) {
            }
        });

        tvRepository.setRoomDevice(roomDevice);
        tvRepository.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());

        outputDevices.put(roomDevice.getId(), tvRepository);
    }

    private void updateDisplayPowerState(int deviceId, boolean powerOn) {
        List<DisplayOutputItem> currentItems = displayOutputItems.getValue();

        if (currentItems == null) {
            return;
        }

        List<DisplayOutputItem> updatedItems = new ArrayList<>(currentItems);

        for (DisplayOutputItem item : updatedItems) {
            if (item.getDeviceId() == deviceId) {
                item.setPowerOn(powerOn);
                break;
            }

        }

        displayOutputItems.postValue(updatedItems);
    }

    public void cleanup() {
        outputDevices.clear();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }

//    public void updateControlSwitchItemsStatus() {
//
//        devices.forEach(device -> {
//            if (device != null) {
//                if (device instanceof LGTVRepository) {
//                    ((ITVRepository) device).getStatus();
//                }
//
//                if (device instanceof LEDWallRepository) {
//                    ((ILEDWallRepository) device).getStatus();
//                }
//            }
//        });
//    }
}
