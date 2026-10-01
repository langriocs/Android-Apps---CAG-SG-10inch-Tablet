package com.avl.cag10inchApp.model;

import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.IRoomDevice;

public class DisplayOutputItem {

    private int deviceId;
    private final String displayName;
    private final int imgResId;
    private final int portNumber;
    private String deviceType;
    private RoomDevice roomDevice;
    private boolean powerOn;


    public DisplayOutputItem(int deviceId, String displayName, int imgResId, int portNumber, String deviceType, RoomDevice roomDevice ) {
        this.deviceId = deviceId;
        this.displayName = displayName;
        this.imgResId = imgResId;
        this.portNumber = portNumber;
        this.deviceType = deviceType;
        this.roomDevice = roomDevice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getImgResId() {
        return imgResId;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }


    public RoomDevice getRoomDevice() {
        return roomDevice;
    }

    public void setRoomDevice(RoomDevice roomDevice) {
        this.roomDevice = roomDevice;
    }

    public boolean isPowerOn() {
        return powerOn;
    }

    public void setPowerOn(boolean powerOn) {
        this.powerOn = powerOn;
    }


}
