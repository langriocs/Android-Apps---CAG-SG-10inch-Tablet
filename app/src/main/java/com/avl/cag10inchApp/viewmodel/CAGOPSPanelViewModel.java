package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.DeviceConnectionState;
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

public class CAGOPSPanelViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    // Centralized LiveData for the device list and connection states
    private final MutableLiveData<List<RoomDevice>> roomDevices = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> deviceConnectionStates = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<List<DisplayOutputItem>> displayOutputItems = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<DisplayOutputItem> selectedDeviceOutput = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isMuted = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> volume = new MutableLiveData<>(50);

    // Keep track of active TCPClient connections mapped by device ID
//    private final Map<String, TCPClient> dynamicTcpClients = new HashMap<>();

    public LiveData<Boolean> getIsSystemInitialized() { return isSystemInitialized; }
    public void setSystemInitialized(boolean initialized) { isSystemInitialized.postValue(initialized); }
    public LiveData<List<DisplayOutputItem>> getDisplayOutputItems() { return displayOutputItems; }
    public LiveData<DisplayOutputItem> getSelectedDeviceOutput() { return selectedDeviceOutput; }
    public LiveData<Boolean> getIsMuted() { return isMuted; }
    public LiveData<Integer> getVolume() { return volume; }

    public CAGOPSPanelViewModel() {

    }

    // --- Dynamic Room Devices Connection Management ---

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
        ((IDSPRepository) ledAudioRepository).setListener(new ILEDWallListener() {

            @Override
            public void onConnected() {
                updateConnectionState( id, DeviceConnectionState.CONNECTED);
            }

            @Override
            public void onDisconnected() {
                updateConnectionState( id, DeviceConnectionState.DISCONNECTED);
            }
        });

        ledAudioRepository.connect(ip, port);

        return ledAudioRepository;
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

    private IRoomDevice connectLEDWall(String ip, int port) {
        IRoomDevice ledWallRepository = new LEDWallRepository();
        ((ILEDWallRepository) ledWallRepository).setListener(new ILEDWallListener() {

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {

            }
        });

        ledWallRepository.connect(ip, port);

        return ledWallRepository;
    }

    private IRoomDevice connectSwitcher(String ip, int port) {
        IRoomDevice switchRepository = new Switch32x32Repository();
        ((ISwitchRepository) switchRepository).setListener(new ISwitchListener() {

            @Override
            public void onConnected() {
                isSwitcherConnected.postValue(true);
                Log.d("cag", "Switcher connected " + ip);
            }

            @Override
            public void onDisconnected() {
                isSwitcherConnected.postValue(false);
                Log.d("cag", "Switcher disconnected " + ip);
            }
        });

        switchRepository.connect(ip, port);

        return switchRepository;

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

    public void routeSourceToDevice(DisplaySourceItem selectedSource, DisplayOutputItem selectedOutput) {
        List<DisplayOutputItem> currentItems = this.displayOutputItems.getValue();

        for (DisplayOutputItem item : currentItems) {
            if (item.getDisplayName().equals("Switch")) {
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), selectedOutput.getOutportNumber());
                break;
            }
        }

    }

    public void setSelectedDeviceOutput(DisplayOutputItem selectedDeviceOutput) {
        this.selectedDeviceOutput.postValue(selectedDeviceOutput);
    }

    public void changeMute(boolean isMute) {
        DisplayOutputItem selDeviceOutput = this.selectedDeviceOutput.getValue();
        if (selDeviceOutput.getRoomDevice() instanceof  LGTVRepository) {
            ((LGTVRepository) selDeviceOutput.getRoomDevice()).setMute(isMute);
        }

        if (selDeviceOutput.getRoomDevice() instanceof  DSPRepository) {
            ((DSPRepository) selDeviceOutput.getRoomDevice()).setMute(isMute ? 1 : 0);
        }

        this.isMuted.postValue(isMute);
    }

    public void changeVolume(int volume) {

        DisplayOutputItem selDeviceOutput = this.selectedDeviceOutput.getValue();
        if (selDeviceOutput.getRoomDevice() instanceof  LGTVRepository) {
            ((LGTVRepository) selDeviceOutput.getRoomDevice()).setVolume(volume);
        }
        if (selDeviceOutput.getRoomDevice() instanceof  DSPRepository) {
            ((DSPRepository) selDeviceOutput.getRoomDevice()).setVolume(volume);
        }

        this.volume.postValue(volume);

    }

    public void turnOffTV() {
        for (DisplayOutputItem item : displayOutputItems.getValue()) {
            if (item.getDisplayName().equals("TV")) {
                ((LGTVRepository) item.getRoomDevice()).turnOff();
            }
        }
    }

    @Override
    protected void onCleared() {

        // Crucial: Stop all connections to prevent memory leaks when the ViewModel is destroyed
        cleanup();
        super.onCleared();
    }

    public void cleanup() {

        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items != null) {
            for (DisplayOutputItem item : items) {

                if (item == null) continue;

                IRoomDevice device = item.getRoomDevice();

                if (device != null) {
                    device.cleanup();
                    item.setRoomDevice(null);
                }
            }
        }

        isSwitcherConnected.postValue(false);
        isSystemInitialized.postValue(false);

        Log.d("CAGOPS", "All device connections closed");
    }


}