package com.avl.cag10inchApp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.switcher.Switch5x1Output;
import com.avl.cag10inchApp.repository.tv.TVPowerState;
import com.avl.cag10inchApp.repository.switcher.ISwitchListener;
import com.avl.cag10inchApp.repository.switcher.Switch5x1Repository;
import com.avl.cag10inchApp.repository.tv.ITVListener;
import com.avl.cag10inchApp.repository.tv.LGTVRepository;

public class ControlScreenViewModel extends ViewModel {

    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isTVConnected = new MutableLiveData<>(false);
    private final MutableLiveData<TVPowerState> tvState = new MutableLiveData<>(TVPowerState.OFF);
    private final MutableLiveData<Boolean> isUsbCSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isWirelessSelected = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> tvMuted = new MutableLiveData<>(false);
    private final MutableLiveData<String> tvMessage = new MutableLiveData<>("");
    private final MutableLiveData<Integer> tvVolume = new MutableLiveData<>(50);


    private final IRoomDevice tvRepository;
    private final IRoomDevice switchRepository;

    public ControlScreenViewModel () {
        tvRepository = new LGTVRepository();
        switchRepository = new Switch5x1Repository();
        setupTVListener();
        setupSwitchListener();
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

    // TV setup
    private void setupTVListener() {
        ((LGTVRepository) tvRepository).setListener(new ITVListener() {
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
                tvState.postValue(state);
            }

            @Override
            public void onVolumeChanged(int volume) {
                tvVolume.postValue(volume);
            }

            @Override
            public void onMuteChanged(boolean state) {
                tvMuted.postValue(state);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public void connectTV(String ip, int port) {
        tvRepository.connect(ip, port);
    }

    public void turnOnTV() {
        ((LGTVRepository) tvRepository).turnOn();
    }

    public void turnOffTV() {
        ((LGTVRepository) tvRepository).turnOff();
    }

    public void changeMute(boolean isMute) {
        ((LGTVRepository) tvRepository).setMute(isMute);
    }

    public void changeVolume(int volume) {
        ((LGTVRepository) tvRepository).setVolume(volume);
    }

    // Switch setup
    private void setupSwitchListener() {
        ((Switch5x1Repository) switchRepository).setListener(new ISwitchListener() {
            @Override
            public void onConnected() {
                isSwitcherConnected.postValue(true);
            }

            @Override
            public void onDisconnected() {
                isSwitcherConnected.postValue(false);
            }
        });
    }

    public void connectSwitcher(String ip, int port) {
        switchRepository.connect(ip, port);
    }
    public void disconnectSwitcher() {
        switchRepository.disconnect();
    }
    public void routeInputSourceToUSB() {
        isUsbCSelected.postValue(true);
        isWirelessSelected.postValue(false);
        ((Switch5x1Repository) switchRepository).routeInputSourceTo(Switch5x1Output.USB_1);
    }

    public void routeInputSourceToWireless() {
        isUsbCSelected.postValue(false);
        isWirelessSelected.postValue(true);
        ((Switch5x1Repository) switchRepository).routeInputSourceTo(Switch5x1Output.HDMI_4);
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        tvRepository.cleanup();
        switchRepository.cleanup();
    }

}
