package com.avl.cagApp.model;

public class ControlSwitchItem {
    private final String displayName;
    private boolean state;
    private final int outportNumber;
    private String ipAddress;
    private int portNumber;
    private String deviceType;


    public ControlSwitchItem(String displayName, boolean state, int outportNumber, String ipAddress, int portNumber, String deviceType ) {
        this.displayName = displayName;
        this.state = state;
        this.outportNumber = outportNumber;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.deviceType = deviceType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getState() {
        return state;
    }
    public void setState(boolean state) {
        this.state = state;
    }
    public int getOutportNumber() {
        return outportNumber;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
