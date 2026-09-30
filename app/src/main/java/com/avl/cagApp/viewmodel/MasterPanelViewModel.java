package com.avl.cagApp.viewmodel;

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
import java.util.List;
import java.util.stream.Collectors;

public class MasterPanelViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLEDConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTVConnected = new MutableLiveData<>(false);

    private final List<IRoomDevice> devices = new ArrayList<>();
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

        if (parentSwitch != null) {
            parentSwitch.routeAudio(selectedInput, AudioMode.SOURCE);
            parentSwitch.routeAV(selectedInput, selectedOutput.getPortNumber());
        }

        for (IRoomDevice device : devices) {
            if (device != null) {
                if (device.getRoomDevice().getOutPort() == selectedOutput.getRoomDevice().getOutPort()) {
                    if (device instanceof LEDWallRepository) {
                        ((LEDWallRepository) device).setPreset(1);
                    }

                    if (device instanceof LGTVRepository ) {
                        ((LGTVRepository) device).turnOn();
                    }
                }
            }
        }



//        devices.forEach(iRoomDevice -> {
//            if (iRoomDevice.getRoomDevice().getId() == selectedOutput.getDeviceId()) {
//
//            }
//
//            if (iRoomDevice instanceof LEDWallRepository) {
//
//                if (iRoomDevice.getRoomDevice().getDeviceName().equals("LED Wall Hall 2 Front")) {
//
//                }
//
////                ((LEDWallRepository) iRoomDevice).routeSourceToLED(selectedInput, selectedOutput);
//            }
//        });
    }

    public void routeAudio(int selectedInput) {
    }

    public void switchControl(ControlSwitchItem controlSwitchItem) {
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
}
