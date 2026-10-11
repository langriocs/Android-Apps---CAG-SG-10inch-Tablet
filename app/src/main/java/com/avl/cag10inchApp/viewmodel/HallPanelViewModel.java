package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.model.DisplayMode;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPChannel;
import com.avl.cag10inchApp.repository.audio.DSPRepository;
import com.avl.cag10inchApp.repository.audio.IAudioListener;
import com.avl.cag10inchApp.repository.audio.IDSPRepository;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallRepository;
import com.avl.cag10inchApp.repository.ledwall.LEDPreset;
import com.avl.cag10inchApp.repository.ledwall.LEDWallRepository;
import com.avl.cag10inchApp.repository.switcher.ISwitchListener;
import com.avl.cag10inchApp.repository.switcher.ISwitchRepository;
import com.avl.cag10inchApp.repository.switcher.Switch32x32Repository;

import java.util.List;

public class HallPanelViewModel extends ViewModel {
    private MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isDSPConnected = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLEDConnected = new MutableLiveData<>(false);
    private MutableLiveData<List<DisplayOutputItem>> displayOutputItems = new MutableLiveData<>();
    private MutableLiveData<Integer> volume = new MutableLiveData<>(50);
    private MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private MutableLiveData<DisplayMode> displayMode = new MutableLiveData<>(null);

    private DisplaySourceItem selectedSourceDevice;
    private DisplayMode selectedDisplayMode;

    public HallPanelViewModel() {

    }

    public LiveData<Boolean> isSystemInitialized() {
        return isSystemInitialized;
    }
    public LiveData<Boolean> isLEDConnected() {
        return isLEDConnected;
    }
    public LiveData<Boolean> isDSPConnected() {
        return isDSPConnected;
    }
    public LiveData<Boolean> isSwitcherConnected() {
        return isSwitcherConnected;
    }
    public LiveData<DisplayMode> getDisplayMode() {
        return displayMode;
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

    private IRoomDevice connectDSP(int id, String ip, int port) {
        IRoomDevice ledAudioRepository = new DSPRepository();
        ((IDSPRepository) ledAudioRepository).setListener(new IAudioListener() {
            @Override
            public void onConnected() {
//                updateConnectionState( id, DeviceConnectionState.CONNECTED);
                ((IDSPRepository) ledAudioRepository).setChannel(DSPChannel.CH_1);
                ((IDSPRepository) ledAudioRepository).setVolume(50);
                ((IDSPRepository) ledAudioRepository).setMute(0);
                volume.postValue(50);
                isDSPConnected.postValue(true);

            }

            @Override
            public void onDisconnected() {
//                updateConnectionState( id, DeviceConnectionState.DISCONNECTED);
                isDSPConnected.postValue(false);
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
        ((IDSPRepository) ledAudioRepository).setChannel(DSPChannel.CH_1);
        ((IDSPRepository) ledAudioRepository).setVolume(50);
        ((IDSPRepository) ledAudioRepository).setMute(0);

        volume.postValue(50);

        return ledAudioRepository;
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

    public void connectAllDevices(List<DisplayOutputItem> displayOutputItems) {
        for (DisplayOutputItem item : displayOutputItems) {
            if (item.getDisplayName().equals("Switch")) {
                item.setRoomDevice(connectSwitcher(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("LED_WALL")) {
                item.setRoomDevice(connectLEDWall(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("LED_AUDIO")) {
                item.setRoomDevice(connectDSP(item.getDeviceId(), item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("DSP")) {
                item.setRoomDevice(connectDSP(item.getDeviceId(), item.getDeviceIp(), item.getDevicePort()));
            }
        }

        this.displayOutputItems.postValue(displayOutputItems);
    }

    public void setSelectedSource(DisplaySourceItem item) {
        selectedSourceDevice = item;
    }

    public void setPreset(LEDPreset preset) {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.152");
        if (item == null) {
            return;
        }

        ( (ILEDWallRepository)  item.getRoomDevice()).setPreset(preset);
    }

    private DisplayOutputItem getSelectedDeviceByIP(String ip) {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        for (DisplayOutputItem item : items) {
            if (item.getDisplayName().equals("LED_WALL")) {
                if (item.getDeviceIp().equals(ip)) {
                    return item;
                }
            }
        }

        return null;
    }

    public void routeSourceToSingle() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),1);
    }

    public void routeSourceToDouble() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),1);
    }

    public void setSelectedDisplayMode(DisplayMode selectedDisplayMode) {
        this.selectedDisplayMode = selectedDisplayMode;
    }

    public void showSourceDisplay() {
        displayMode.postValue(selectedDisplayMode);
    }
}