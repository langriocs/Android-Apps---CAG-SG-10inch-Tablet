package com.avl.cag10inchApp.repository.switcher;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.IRoomDevice;

import java.util.List;
import java.util.stream.Collectors;

public class Switch32x32Repository implements ISwitchRepository, IRoomDevice {

    private volatile ISwitchListener listener;
    private final TCPClient tcpClient;

    private RoomDevice roomDevice;

    public Switch32x32Repository() {
        this.tcpClient = createClient();
    }

    private TCPClient createClient() {
        return new TCPClient("cag", new TCPClient.OnMessageReceived() {
            @Override
            public void onMessageReceived(String message) {
                if (message == null || message.trim().isEmpty()) {
                    return;
                }

                handleMessage(message);

            }
        }, new TCPClient.OnConnectionStatusChanged() {
            @Override
            public void onConnected() {
                ISwitchListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onConnected();
                }
            }

            @Override
            public void onDisconnected() {
                ISwitchListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onDisconnected();
                }
            }
        });
    }

    private void handleMessage(String message) {

    }

    private String parseSelectedOutput(List<Integer> selectedOutput) {
        return selectedOutput.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    @Override
    public void connect(String ip, int port) {
        tcpClient.connect(ip, port);
    }

    @Override
    public void disconnect() {
        tcpClient.stopClient();
    }

    @Override
    public void setRoomDevice(RoomDevice roomDevice) {
        this.roomDevice = roomDevice;
    }

    @Override
    public RoomDevice getRoomDevice() {
        return this.roomDevice;
    }

    @Override
    public void routeInputSourceTo(Switch5x1Output output) {

    }

    @Override
    public void routeAV(Integer selectedInput, Integer selectedOutput) {
//        String selOutput = parseSelectedOutput(selectedOutput);
        tcpClient.sendMessage("s in " + selectedInput.toString() + " av out " + selectedOutput.toString() +"! \r");
    }

    @Override
    public void routeAudio(Integer selectedInput, AudioMode mode) {
        tcpClient.sendMessage("s input " + selectedInput.toString() + " audio mode " + String.valueOf(mode.getValue()) +"! \r");
    }

    @Override
    public void setListener(ISwitchListener switchListener) {
        this.listener = switchListener;
    }

    @Override
    public void cleanup() {
        this.listener = null;
        tcpClient.cleanup();
    }

}
