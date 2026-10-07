package com.avl.cag10inchApp.model;

import com.avl.cag10inchApp.libs.TCPClient;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.DeviceConnectionState;
import com.avl.cag10inchApp.repository.IRoomDevice;

public class DisplayOutputItem {

    private int deviceId;
    private String displayName;
    private String description;
    private String deviceIp;
    private int devicePort;
    private int imgResId;
    private int outportNumber;
    private IRoomDevice roomDevice;
    private String selectedSourceName;
    private boolean deviceStatusVisible;
    private boolean isTurnOn;

    private DeviceConnectionState connectionState = DeviceConnectionState.UNKNOWN;

    public DisplayOutputItem(RoomDevice roomDevice, int imgResId) {
        this.deviceId = roomDevice.getId();
        this.displayName = roomDevice.getDeviceName();
        this.description = roomDevice.getDeviceDesc();
        this.deviceIp = roomDevice.getDeviceIpAddress();
        this.devicePort = roomDevice.getDevicePort();
        this.outportNumber = roomDevice.getOutPort();
        this.imgResId = imgResId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public int getImgResId() {
        return imgResId;
    }

    public int getOutportNumber() {
        return outportNumber;
    }

    public String getSelectedSourceName() {
        return selectedSourceName;
    }

    public void setSelectedSourceName(String selectedSourceName) {
        this.selectedSourceName = selectedSourceName;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public void setOutportNumber(int outportNumber) {
        this.outportNumber = outportNumber;
    }

    public IRoomDevice getRoomDevice() {
        return roomDevice;
    }

    public void setRoomDevice(IRoomDevice roomDevice) {
        this.roomDevice = roomDevice;
    }

    public void setConnectionState(DeviceConnectionState connectionState) {
        this.connectionState = connectionState;
    }

    public DeviceConnectionState getConnectionState() {
        return connectionState;
    }

    public boolean isDeviceStatusVisible() {
        return deviceStatusVisible;
    }

    public void setDeviceStatusVisible(boolean deviceStatusVisible) {
        this.deviceStatusVisible = deviceStatusVisible;
    }

    public boolean isTurnOn() {
        return isTurnOn;
    }

    public void setTurnOn(boolean turnOn) {
        isTurnOn = turnOn;
    }
}
