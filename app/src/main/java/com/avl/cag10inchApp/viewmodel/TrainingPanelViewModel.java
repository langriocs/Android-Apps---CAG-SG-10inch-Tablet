package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPChannel;
import com.avl.cag10inchApp.repository.audio.DSPRepository;
import com.avl.cag10inchApp.repository.audio.IAudioListener;
import com.avl.cag10inchApp.repository.audio.IDSPRepository;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallRepository;
import com.avl.cag10inchApp.repository.ledwall.LEDWallRepository;
import com.avl.cag10inchApp.repository.switcher.ISwitchRepository;
import com.avl.cag10inchApp.repository.switcher.Switch32x32Repository;
import com.avl.cag10inchApp.repository.tv.ITVRepository;
import com.avl.cag10inchApp.repository.tv.TVPowerState;
import com.avl.cag10inchApp.repository.switcher.ISwitchListener;
import com.avl.cag10inchApp.repository.tv.ITVListener;
import com.avl.cag10inchApp.repository.tv.LGTVRepository;

import java.util.ArrayList;
import java.util.List;

public class TrainingPanelViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTVConnected = new MutableLiveData<>(false);
    private final MutableLiveData<TVPowerState> tvState = new MutableLiveData<>(TVPowerState.OFF);
    private final MutableLiveData<Boolean> isUsbCSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isWirelessSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> tvMuted = new MutableLiveData<>(false);
    private final MutableLiveData<String> tvMessage = new MutableLiveData<>("");
    private final MutableLiveData<Integer> tvVolume = new MutableLiveData<>(50);
    private final MutableLiveData<Boolean> dspMuted = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> dspVolume = new MutableLiveData<>(50);

    private final MutableLiveData<List<DisplayOutputItem>> displayOutputItems = new MutableLiveData<>(new ArrayList<>());
    private DisplaySourceItem selectedSource;

    public TrainingPanelViewModel() {

    }

    public LiveData<List<DisplayOutputItem>> getDisplayOutputItems() {
        return displayOutputItems;
    }

    public LiveData<Boolean> getIsSystemInitialized() { return isSystemInitialized; }
    public void setSystemInitialized(boolean initialized) { isSystemInitialized.postValue(initialized); }

    public LiveData<Boolean> getIsSwitcherConnected() {
        return isSwitcherConnected;
    }

    public LiveData<Boolean> getIsTVConnected() {
        return isTVConnected;
    }

    public LiveData<TVPowerState> getTvPowerState() {
        return tvState;
    }

    public LiveData<Boolean> getIsUsbCSelected() {
        return isUsbCSelected;
    }

    public void setUsbCSelected(boolean selected) {
        isUsbCSelected.postValue(selected);
    }

    public LiveData<Boolean> getIsWirelessSelected() {
        return isWirelessSelected;
    }

    public void setWirelessSelected(boolean selected) {
        isWirelessSelected.postValue(selected);
    }

    public LiveData<Boolean> getTVMuted() {
        return tvMuted;
    }

    public LiveData<String> getTVMessage() {
        return tvMessage;
    }

    public LiveData<Integer> getTVVolume() {
        return tvVolume;
    }

    public LiveData<Boolean> getDSPMuted() { return dspMuted; }

    public void connectAllDevices(List<DisplayOutputItem> displayOutputItems) {

        for (DisplayOutputItem item : displayOutputItems) {
            if (item.getDisplayName().equals("Switch")) {
                item.setRoomDevice(connectSwitch(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("TV")) {
                item.setRoomDevice(connectTV(item));
            } else if (item.getDisplayName().equals("LED_WALL")) {
                item.setRoomDevice(connectLEDWall(item.getDeviceIp(), item.getDevicePort()));
            } else if (item.getDisplayName().equals("DSP")) {
                item.setRoomDevice(connectDSP(item.getDeviceIp(), item.getDevicePort()));
            }
        }

        this.displayOutputItems.postValue(displayOutputItems);
    }
    private IRoomDevice connectSwitch(String ip, int port) {
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

    private IRoomDevice connectTV(DisplayOutputItem item) {
        IRoomDevice tvRepository = new LGTVRepository();
        ((ITVRepository) tvRepository).setListener(new ITVListener() {

            @Override
            public void onConnected() {

                Log.d("cag", "TV connected " + item.getDeviceIp());
            }

            @Override
            public void onDisconnected() {

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

    private IRoomDevice connectDSP(String ip, int port) {
        IRoomDevice ledAudioRepository = new DSPRepository();
        ((IDSPRepository) ledAudioRepository).setListener(new IAudioListener() {

            @Override
            public void onConnected() {
                ((IDSPRepository) ledAudioRepository).setChannel(DSPChannel.CH_1);
                ((IDSPRepository) ledAudioRepository).setVolume(411);
                ((IDSPRepository) ledAudioRepository).setMute(0);
                dspVolume.postValue(411);
            }

            @Override
            public void onDisconnected() {
                dspVolume.postValue(411);
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
        ((IDSPRepository) ledAudioRepository).setVolume(411);
        ((IDSPRepository) ledAudioRepository).setMute(0);

        dspVolume.postValue(411);

        return ledAudioRepository;
    }

    public void turnOnTV() {

    }

    public void turnOffTV() {

    }

    public void changeMute(boolean isMute) {
        List<DisplayOutputItem> items = displayOutputItems.getValue();
        if (items == null) {
            return;
        }

        for(DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof DSPRepository) {
                ((IDSPRepository) item.getRoomDevice()).setMute(isMute ? 1 : 0);
                break;
            }
        }

        this.dspMuted.postValue(isMute);
    }

    public void changeVolume(int volume) {
        List<DisplayOutputItem> items = this.displayOutputItems.getValue();

        if (items == null) {
            return;
        }

        for (DisplayOutputItem item: items ) {

            if (item.getRoomDevice() instanceof  DSPRepository) {
                ((IDSPRepository) item.getRoomDevice()).setVolume(volume);
            }
        }
        this.dspVolume.postValue(volume);
    }


    public void routeInputSourceUSB() {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items == null) {
            return;
        }

        // get the repository for the LED wall
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof LEDWallRepository) {
                // set the preset
                ((LEDWallRepository) item.getRoomDevice()).setPresetDirect();
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the video
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 1);
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the audio
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 8);
                break;
            }
        }
//        isUsbCSelected.postValue(true);
//        isWirelessSelected.postValue(false);

    }

    public void routeInputSourceWireless() {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items == null) {
            return;
        }

        // get the repository for the LED wall
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof LEDWallRepository) {
                // set the preset
                ((LEDWallRepository) item.getRoomDevice()).setPresetDirect();
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the video
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 1);
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the audio
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 8);
                break;
            }
        }

        isUsbCSelected.postValue(false);
        isWirelessSelected.postValue(true);

    }


    @Override
    protected void onCleared() {
        super.onCleared();

    }

    public void setSelectedSource(DisplaySourceItem source) {
        selectedSource = source;
    }

    public void routeHDMIDirectToLEDWall() {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items == null) {
            return;
        }

        // get the repository for the LED wall
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof LEDWallRepository) {
                // set the preset
                ((LEDWallRepository) item.getRoomDevice()).setPresetDirect();
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the video
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 1);
                break;
            }
        }

        // get the repository for the switch
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof Switch32x32Repository) {
                // route the audio
                ((Switch32x32Repository) item.getRoomDevice()).routeAV(selectedSource.getPortNumber(), 8);
                break;
            }
        }
    }

    public void shutdown() {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items == null) {
            return;
        }

        // get the repository for the LED wall
        for (DisplayOutputItem item : items) {
            if (item.getRoomDevice() instanceof LEDWallRepository) {
                // set the preset
                ((LEDWallRepository) item.getRoomDevice()).setPreset(4);
                break;
            }
        }
    }
}
