package com.avl.cag10inchApp.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.vo.RoomDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlScreen1ViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    // Centralized LiveData for the device list and connection states
    private final MutableLiveData<List<RoomDevice>> roomDevices = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Boolean>> deviceConnectionStates = new MutableLiveData<>(new HashMap<>());
    private final MutableLiveData<Boolean> isSystemInitialized = new MutableLiveData<>(false);

    // Keep track of active TCPClient connections mapped by device ID
    private final Map<String, TCPClient> dynamicTcpClients = new HashMap<>();

    public LiveData<Boolean> getIsSystemInitialized() { return isSystemInitialized; }
    public void setSystemInitialized(boolean initialized) { isSystemInitialized.postValue(initialized); }

    public ControlScreen1ViewModel () {

    }

    // --- Dynamic Room Devices Connection Management ---

    public void establishConnection(List<RoomDevice> roomDeviceList) {
        this.roomDevices.setValue(roomDeviceList);

        for (RoomDevice roomDevice : roomDeviceList) {
            if (roomDevice == null) continue;

            String deviceIP = roomDevice.getDeviceIpAddress();

            // Avoid creating duplicate connections if one already exists for this device ID
            if (!dynamicTcpClients.containsKey(deviceIP)) {
                TCPClient client = new TCPClient("cag-" + deviceIP,
                        message -> Log.d("Device Message " + deviceIP, message),
                        new TCPClient.OnConnectionStatusChanged() {
                            @Override
                            public void onConnected() {
                                updateConnectionState(deviceIP, true);
                            }

                            @Override
                            public void onDisconnected() {
                                updateConnectionState(deviceIP, false);
                            }
                        }
                );

                dynamicTcpClients.put(deviceIP, client);
                // Connect automatically using the device credentials
                client.connect(roomDevice.getDeviceIpAddress(), roomDevice.getDevicePort());
            }
        }
    }

    private synchronized void updateConnectionState(String deviceIP, boolean isConnected) {
        Map<String, Boolean> currentStates = deviceConnectionStates.getValue();
        if (currentStates == null) {
            currentStates = new HashMap<>();
        }
        currentStates.put(deviceIP, isConnected);
        deviceConnectionStates.postValue(currentStates); // Notifies all UI observers
    }

    public LiveData<Map<String, Boolean>> getDeviceConnectionStates() {
        return deviceConnectionStates;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // Crucial: Stop all connections to prevent memory leaks when the ViewModel is destroyed

        dynamicTcpClients.values().forEach(TCPClient::stopClient);
        dynamicTcpClients.clear();
    }

}