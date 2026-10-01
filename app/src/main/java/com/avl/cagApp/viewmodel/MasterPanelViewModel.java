package com.avl.cagApp.viewmodel;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cagApp.model.ControlSwitchItem;
import com.avl.cagApp.model.DisplayOutputItem;
import com.avl.cagApp.model.vo.RoomDevice;
import com.avl.cagApp.repository.IRoomDevice;
import com.avl.cagApp.repository.ledwall.ILEDWallListener;
import com.avl.cagApp.repository.ledwall.ILEDWallRepository;
import com.avl.cagApp.repository.ledwall.LEDWallRepository;
import com.avl.cagApp.repository.switcher.AudioMode;
import com.avl.cagApp.repository.switcher.ISwitchListener;
import com.avl.cagApp.repository.switcher.ISwitchRepository;
import com.avl.cagApp.repository.switcher.Switch32x32Repository;
import com.avl.cagApp.repository.tv.ITVListener;
import com.avl.cagApp.repository.tv.ITVRepository;
import com.avl.cagApp.repository.tv.LGTVRepository;
import com.avl.cagApp.repository.tv.TVPowerState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;
import java.util.stream.Collectors;

public class MasterPanelViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLEDConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTVConnected = new MutableLiveData<>(false);

    private final List<IRoomDevice> devices = new ArrayList<>();
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

    public void setSystemInitialized(boolean initialized) {
        this.isSystemInitialized.postValue(initialized);
    }

    private void setupSwitchListener() {
    }

    private void setupLEDListener() {
    }

    private void setupTVListener() {
    }

    public void routeAV(int selectedInput, List<Integer> selectedOutput) {

    }

    public void routeSourceToDevice(int selectedInput, DisplayOutputItem selectedOutput) {

        if (parentSwitch == null) {
            return;
        }

        parentSwitch.routeAV(selectedInput, selectedOutput.getPortNumber());

        for (IRoomDevice device : devices) {
            if (device != null) {
                if (device.getRoomDevice().getId() == selectedOutput.getDeviceId()) {
                    if (device instanceof LEDWallRepository) {
                        ((LEDWallRepository) device).setPreset(1);
                        break;
                    }

                    if (device instanceof LGTVRepository ) {
                        ((LGTVRepository) device).turnOn();
                        break;
                    }

                    if (device instanceof Switch32x32Repository) {
                        ((Switch32x32Repository) device).routeAV(5, 1);
                        break;
                    }
                }
            }
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

    public void connectDevice(RoomDevice roomDevice) {
        if (roomDevice == null || roomDevice.getDeviceName() == null) {
            return;
        }

        if (roomDevice.getDeviceName().equals("Switch")) {
            connectSwitcher(roomDevice);
        } else if (roomDevice.getDeviceName().equals("TV")) {
            connectTV(roomDevice);
        } else if (roomDevice.getDeviceName().equals("LED_WALL")) {
            connectLEDWall(roomDevice);
        }
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
            parentSwitch = (Switch32x32Repository) switchRepository;
        } else {
            devices.add(switchRepository);
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
        devices.add(ledWallRepository);
    }

    private void connectTV(RoomDevice roomDevice) {
        IRoomDevice tvRepository = new LGTVRepository();
        ((ITVRepository) tvRepository).setListener(new ITVListener() {

            @Override
            public void onConnected() {
                isTVConnected.postValue(true);
            }

            @Override
            public void onDisconnected() {
                isTVConnected.postValue(false);
            }

            @Override
            public void onPowerStateChanged(TVPowerState state) {
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
        tvRepository.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
        tvRepository.setRoomDevice(roomDevice);
        devices.add(tvRepository);
    }

    public void cleanup() {
        for (IRoomDevice device : devices) {
            if (device != null) {
                try {
                    device.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        devices.clear();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cleanup();
    }

    public void updateControlSwitchItemsStatus() {

        devices.forEach(device -> {
            if (device != null) {
                if (device instanceof LGTVRepository) {
                    ((ITVRepository) device).getStatus();
                }

                if (device instanceof LEDWallRepository) {
                    ((ILEDWallRepository) device).getStatus();
                }
            }
        });
    }
}
