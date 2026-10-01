package com.avl.cag10inchApp.repository.tv;

import android.util.Log;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.IRoomDevice;

import java.util.Locale;

public class LGTVRepository implements ITVRepository, IRoomDevice {

    private volatile ITVListener listener;
    private final TCPClient tcpClient;
    private RoomDevice roomDevice;

    public LGTVRepository() {
        tcpClient = createClient();
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
                ITVListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onConnected();
                }
            }

            @Override
            public void onDisconnected() {
                ITVListener currentListener = listener;
                if (currentListener != null) {
                    currentListener.onDisconnected();
                }
            }
        });
    }

    private void handleMessage(String message) {
        // Parse LG protocol

        ITVListener currentListener = listener;

        if (currentListener == null) {
            return;
        }

        if (isPowerResponse(message)) {
            TVPowerState powerState = parsePowerState(message);
            currentListener.onPowerStateChanged(powerState);
            return;
        }

        if (isVolumeResponse(message)) {
            Integer volume = parseVolume(message);
            if (volume != null) {
                currentListener.onVolumeChanged(volume);
            }
            return;
        }

        if (isMuteResponse(message)) {
            Boolean mute = parseMute(message);
            if (mute != null) {
                currentListener.onMuteChanged(mute);
            }
            return;
        }

        Log.d("cag", "Unknown message: " + message);

    }

    private boolean isPowerResponse(String message) {
        return message.startsWith("a ");
    }

    private boolean isVolumeResponse(String message) {
        return message.startsWith("f ");
    }

    private boolean isMuteResponse(String message) {
        return message.startsWith("e ");
    }

    private TVPowerState parsePowerState(String message) {
        if (message == null) {
            return TVPowerState.UNKNOWN;
        }

        if (message.contains("OK01")) {
            return TVPowerState.ON;
        }

        if (message.contains("OK00")) {
            return TVPowerState.OFF;
        }

        return TVPowerState.UNKNOWN;
    }

    private Integer parseVolume(String message) {
        try {

            int okIndex = message.indexOf("OK");

            if (okIndex == -1) {
                return null;
            }

            // Need 2 characters after OK
            if (message.length() < okIndex + 4) {
                return null;
            }

            String hexValue = message.substring( okIndex + 2, okIndex + 4 );

            int volume = Integer.parseInt(hexValue, 16);

            if (volume < 0 || volume > 100) {
                return null;
            }

            return volume;

        } catch (NumberFormatException e) {

            Log.e("cag", "Unable to parse volume: " + message, e );

            return null;
        }
    }

    private Boolean parseMute (String message) {
        if (message.contains("OK00")) {
            return true;
        }
        if (message.contains("OK01")) {
            return false;
        }
        return null;
    }


    @Override
    public void getPowerState() {
        tcpClient.sendMessage("ka 00 FF\r");
    }

    @Override
    public void getVolume() {
        tcpClient.sendMessage("kf 00 FF\r");
    }

    @Override
    public void getMuteState() {
        tcpClient.sendMessage("ke 00 FF\r");
    }

    @Override
    public void setVolume(int volume) {

        if (volume < 0 || volume > 100) {
            return;
        }

        String hexVolume = String.format(Locale.US, "%02X", volume);
        tcpClient.sendMessage("kf 00 " + hexVolume + "\r");
    }

    @Override
    public void turnOn() {
        tcpClient.sendMessage("ka 00 01\r");
    }

    @Override
    public void turnOff() {
        tcpClient.sendMessage("ka 00 00\r");
    }

    @Override
    public void setMute(boolean mute) {
        String data = mute ? "00" : "01";
        tcpClient.sendMessage("ke 00 "+ data + "\r");
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
    public void getStatus() {
        tcpClient.sendMessage("ka 01 ff\r");
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
    public void setListener(ITVListener tvListener) {
        this.listener = tvListener;
    }

    @Override
    public void cleanup() {
        this.listener = null;
        tcpClient.cleanup();
     }

}
