package com.avl.cag10inchApp.repository.ledwall;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.IRoomDevice;

public class LEDWallRepository implements ILEDWallRepository, IRoomDevice {

    private ILEDWallListener listener;
    private final TCPClient tcpClient;
    private RoomDevice roomDevice;

    public LEDWallRepository() {
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
                ILEDWallListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onConnected();
                }
            }

            @Override
            public void onDisconnected() {
                ILEDWallListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onDisconnected();
                }
            }
        });
    }

    private void handleMessage(String message) {

    }

    @Override
    public void setPreset(int preset) {
        if (preset == 1) {
            tcpClient.sendHex("55 AA 00 00 FE 00 00 00 00 00 01 00 00 01 51 13 01 00 00 B6 56\r");
        }
        if (preset == 2) {
            tcpClient.sendHex("55 AA 00 00 FE 00 00 00 00 00 01 00 00 01 51 13 01 00 01 B7 56\r");
        }
        if (preset == 3) {
            tcpClient.sendHex("55 AA 00 00 FE 00 00 00 00 00 01 00 00 01 51 13 01 00 02 B8 56\r");
        }
    }

    @Override
    public void getStatus() {

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
    public void setListener(ILEDWallListener listener) {
        this.listener = listener;
    }

    @Override
    public void cleanup() {
        this.listener = null;
        tcpClient.cleanup();
    }
}
