package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.model.ControlSwitchItem;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.DeviceConnectionState;
import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPRepository;
import com.avl.cag10inchApp.repository.audio.IAudioListener;
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

    private List<DisplayOutputItem> deviceOutputItems = new ArrayList<>();

    public MasterPanelViewModel() {

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

    public void routeSourceToDevice(int selectedInput, DisplayOutputItem selectedOutput) {

        if (parentSwitch == null) {
            return;
        }

//        route AV Hall Matrix to selected output
        parentSwitch.routeAV(selectedInput, selectedOutput.getOutportNumber());

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
//            ((DSPRepository) roomDevice).setMute();
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

    public void connectAllDevices(List<DisplayOutputItem> displayOutputItems) {

        for (DisplayOutputItem item : displayOutputItems) {
            if (item.getDisplayName().equals("Switch")) {
                item.setRoomDevice(connectSwitcher(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("TV")) {
                item.setRoomDevice(connectTV(item));
            } else if (item.getDisplayName().equals("LED_WALL")) {
                item.setRoomDevice(connectLEDWall(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("LED_AUDIO")) {
                item.setRoomDevice(connectDSP(item.getDeviceId(), item.getDeviceIp(), item.getDevicePort()));
            }
        }

        this.displayOutputItems.postValue(displayOutputItems);
    }

    private IRoomDevice connectDSP(int id, String ip, int port) {
        IRoomDevice ledAudioRepository = new DSPRepository();
        ((IDSPRepository) ledAudioRepository).setListener(new IAudioListener() {

            @Override
            public void onConnected() {
                updateConnectionState( id, DeviceConnectionState.CONNECTED);
            }

            @Override
            public void onDisconnected() {
                updateConnectionState( id, DeviceConnectionState.DISCONNECTED);
            }

            @Override
            public void onVolumeChanged(int volume) {

            }

            @Override
            public void onMuteChanged(boolean isMuted) {

            }

            @Override
            public void setListener(IAudioListener listener) {

            }

            @Override
            public void onError(String message) {

            }
        });

        ledAudioRepository.connect(ip, port);

        return ledAudioRepository;
    }

    private IRoomDevice connectSwitcher(String ip, int port) {
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

        switchRepository.connect(ip, port);

        return switchRepository;

    }

    private IRoomDevice connectLEDWall(String ip, int port) {
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

        ledWallRepository.connect(ip, port);

        return ledWallRepository;
    }

    private IRoomDevice connectTV(DisplayOutputItem item) {
        IRoomDevice tvRepository = new LGTVRepository();
        ((ITVRepository) tvRepository).setListener(new ITVListener() {

            @Override
            public void onConnected() {
                updateConnectionState(item.getDeviceId(), DeviceConnectionState.CONNECTED);
                Log.d("cag", "TV connected " + item.getDeviceIp());
            }

            @Override
            public void onDisconnected() {
                updateConnectionState(item.getDeviceId(), DeviceConnectionState.DISCONNECTED);
            }

            @Override
            public void onPowerStateChanged(TVPowerState state) {
//                boolean isOn = state == TVPowerState.ON;
//                updateDisplayPowerState(roomDevice.getId(), isOn);
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onMuteChanged(boolean isMuted) {
//                this.isMuted.postValue(isMuted);
            }

            @Override
            public void onError(String message) {
            }
        });

        updateConnectionState(item.getDeviceId(), DeviceConnectionState.CONNECTING);
        tvRepository.connect(item.getDeviceIp(), item.getDevicePort());

        return tvRepository;

    }

    private void updateConnectionState(int deviceId, DeviceConnectionState state) {

        List<DisplayOutputItem> currentItems = displayOutputItems.getValue();

        if (currentItems == null) {
            return;
        }

        List<DisplayOutputItem> updatedItems = new ArrayList<>(currentItems);

        for (DisplayOutputItem item : updatedItems) {
            if (item.getDeviceId() == deviceId) {
                item.setConnectionState(state);
                break;
            }

        }

        displayOutputItems.postValue(updatedItems);


    }

    private void updateDisplayPowerState(int deviceId, boolean powerOn) {
        List<DisplayOutputItem> currentItems = displayOutputItems.getValue();

        if (currentItems == null) {
            return;
        }

        List<DisplayOutputItem> updatedItems = new ArrayList<>(currentItems);

//        for (DisplayOutputItem item : updatedItems) {
//            if (item.getDeviceId() == deviceId) {
//                item.setPowerOn(powerOn);
//                break;
//            }
//
//        }

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

}
