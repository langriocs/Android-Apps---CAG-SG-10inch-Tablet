package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.R;
import com.avl.cag10inchApp.model.DisplayMode;
import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.DisplaySourceItem;
import com.avl.cag10inchApp.model.Hall;
import com.avl.cag10inchApp.model.HallOperation;
import com.avl.cag10inchApp.model.LEDPanelOutput;
import com.avl.cag10inchApp.repository.IRoomDevice;
import com.avl.cag10inchApp.repository.audio.DSPChannel;
import com.avl.cag10inchApp.repository.audio.DSPRepository;
import com.avl.cag10inchApp.repository.audio.IAudioListener;
import com.avl.cag10inchApp.repository.audio.IDSPRepository;
import com.avl.cag10inchApp.repository.audio.Preset;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;
import com.avl.cag10inchApp.repository.ledwall.ILEDWallRepository;
import com.avl.cag10inchApp.repository.ledwall.LEDPreset;
import com.avl.cag10inchApp.repository.ledwall.LEDWallRepository;
import com.avl.cag10inchApp.repository.switcher.ISwitchListener;
import com.avl.cag10inchApp.repository.switcher.ISwitchRepository;
import com.avl.cag10inchApp.repository.switcher.Switch32x32Repository;

import java.util.ArrayList;
import java.util.List;

public class HallPanelViewModel extends ViewModel {
    private MutableLiveData<Boolean> isSwitcherConnected = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isDSPConnected = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLEDConnected = new MutableLiveData<>(false);
    private MutableLiveData<List<LEDPanelOutput>> ledPanelOutputList = new MutableLiveData<>();
    private MutableLiveData<Integer> volume = new MutableLiveData<>(50);
    private MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);
    private MutableLiveData<DisplayMode> displayMode = new MutableLiveData<>(null);
    private MutableLiveData<List<DisplayOutputItem>> displayOutputItems = new MutableLiveData<>();
    private MutableLiveData<Boolean> isRightLedSelected = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isLeftLedSelected = new MutableLiveData<>(false);

    private DisplaySourceItem selectedSourceDevice;
    private DisplayMode selectedDisplayMode;
    private HallOperation hallOperation;
    private DisplayMode hall1CombineDisplayMode;
    private DisplayMode hall2ACombineDisplayMode;
    private DisplayMode hall2BCombineDisplayMode;

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
    public LiveData<List<LEDPanelOutput>> getLedPanelOutputList() {
        return ledPanelOutputList;
    }
    public LiveData<Boolean> getIsRightLedSelected() {
        return isRightLedSelected;
    }
    public LiveData<Boolean> getIsLeftLedSelected() {
        return isLeftLedSelected;
    }
    public LiveData<List<DisplayOutputItem>> getDisplayOutputItems() {
        return displayOutputItems;
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

    public void setPreset(LEDPreset preset, Hall hall) {
        DisplayOutputItem switcher = null;

        if (hall == Hall.HALL_1) {
            switcher = getSelectedDeviceByIP("192.168.1.152");
        }
        if (hall == Hall.HALL_2A) {
            switcher = getSelectedDeviceByIP("192.168.1.162");
        }
        if (hall == Hall.HALL_2B) {
            switcher = getSelectedDeviceByIP("192.168.1.163");
        }

        if (switcher == null) {
            return;
        }

        ( (ILEDWallRepository)  switcher.getRoomDevice()).setPreset(preset);
    }

    private DisplayOutputItem getSelectedDeviceByIP(String ip) {
        List<DisplayOutputItem> items = displayOutputItems.getValue();

        if (items == null) {
            return null;
        }

        for (DisplayOutputItem item : items) {

            if (item.getDeviceIp().equals(ip)) {
                return item;
            }

        }

        return null;
    }

    public void routeSourceToSingle() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        if (selectedSourceDevice == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),1);
    }

    public void routeSourceTo(int outputPort) {
        if (selectedSourceDevice == null) {
            return;
        }

        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),outputPort);
    }

    public void routeSourceToLeft() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }
        if (selectedSourceDevice == null) {
            return;
        }


        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),1);
    }

    public void routeSourceToRight() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }
        if (selectedSourceDevice == null) {
            return;
        }


        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),2);
    }

    public void routeDSP() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }
        if (selectedSourceDevice == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),24);
        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),25);

    }

    public void setSelectedDisplayMode(DisplayMode selectedDisplayMode) {
        this.selectedDisplayMode = selectedDisplayMode;
    }
    public DisplayMode getSelectedDisplayMode() {
        return selectedDisplayMode;
    }

    public void showSourceDisplay() {
        displayMode.postValue(selectedDisplayMode);
    }

    public void setHallOperation(HallOperation hallOperation) {
        this.hallOperation = hallOperation;
    }

    public HallOperation getHallOperation() {
        return hallOperation;
    }

    public void setSelectedHall1CombineDisplayMode(DisplayMode displayMode) {
        hall1CombineDisplayMode = displayMode;
    }

    public void setSelectedHall2ACombineDisplayMode(DisplayMode displayMode) {
        hall2ACombineDisplayMode = displayMode;
    }

    public void setSelectedHall2BCombineDisplayMode(DisplayMode displayMode) {
        hall2BCombineDisplayMode = displayMode;
    }

    public void showSourceCombineDisplay() {

        List<LEDPanelOutput> ledOutputList = ledPanelOutputList.getValue();
        if (ledOutputList == null) {
            ledOutputList = new ArrayList<>();
        }

        ledOutputList.clear();

        if (hall1CombineDisplayMode != null) {
            if (hall1CombineDisplayMode == DisplayMode.SINGLE) {
                LEDPanelOutput ledPanelOutput = new LEDPanelOutput("1", R.drawable.ic_output,1);
                ledOutputList.add(ledPanelOutput);

            }
            if (hall1CombineDisplayMode == DisplayMode.DOUBLE) {
                LEDPanelOutput ledPanelOutput1 = new LEDPanelOutput("1 - 1", R.drawable.ic_output,1);
                LEDPanelOutput ledPanelOutput2 = new LEDPanelOutput("1 - 2", R.drawable.ic_output, 2);
                ledOutputList.add(ledPanelOutput1);
                ledOutputList.add(ledPanelOutput2);
            }
        }

        if (hall2ACombineDisplayMode != null) {
            if (hall2ACombineDisplayMode == DisplayMode.SINGLE) {
                LEDPanelOutput led2APanelOutput = new LEDPanelOutput("2A", R.drawable.ic_output, 3);
                ledOutputList.add(led2APanelOutput);
            }

            if (hall2ACombineDisplayMode == DisplayMode.DOUBLE) {
                LEDPanelOutput led2APanelOutput1 = new LEDPanelOutput("2A - 1", R.drawable.ic_output, 3);
                LEDPanelOutput led2APanelOutput2 = new LEDPanelOutput("2A - 2", R.drawable.ic_output, 4);
                ledOutputList.add(led2APanelOutput1);
                ledOutputList.add(led2APanelOutput2);
            }
        }

        if (hall2BCombineDisplayMode != null) {
            if (hall2BCombineDisplayMode == DisplayMode.SINGLE) {
                LEDPanelOutput led2BPanelOutput = new LEDPanelOutput("2B", R.drawable.ic_output,5);
                ledOutputList.add(led2BPanelOutput);
            }
            if (hall2BCombineDisplayMode == DisplayMode.DOUBLE) {
                LEDPanelOutput led2BPanelOutput1 = new LEDPanelOutput("2B - 1", R.drawable.ic_output,5);
                LEDPanelOutput led2BPanelOutput2 = new LEDPanelOutput("2B - 2", R.drawable.ic_output,6);
                ledOutputList.add(led2BPanelOutput1);
                ledOutputList.add(led2BPanelOutput2);
            }
        }

        ledPanelOutputList.postValue(ledOutputList);


    }

    public void routeSourceTo2A() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        if (selectedSourceDevice == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),3);
    }

    public void routeSourceTo2B() {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.151");
        if (item == null) {
            return;
        }

        if (selectedSourceDevice == null) {
            return;
        }

        ( (ISwitchRepository) item.getRoomDevice()).routeAV(selectedSourceDevice.getPortNumber(),5);
    }

    public void presetRecall(Preset preset) {
        DisplayOutputItem item = getSelectedDeviceByIP("192.168.1.153");

        if (item == null) {
            return;
        }

        ( (IDSPRepository) item.getRoomDevice()).setRecallPreset(preset);

    }
}