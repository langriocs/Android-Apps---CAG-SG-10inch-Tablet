package com.avl.cagApp.model.vo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
    tableName = "room_device",
    foreignKeys = @ForeignKey(
        entity = ControlDevice.class,
        parentColumns = "id",
        childColumns = "control_device_id",
        onDelete = CASCADE
    ),
    indices = {@Index("control_device_id")}
)
public class RoomDevice {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "control_device_id")
    private int controlDeviceId;

    @ColumnInfo(name = "device_name")
    private String deviceName;
    
    @ColumnInfo(name = "device_desc")
    private String deviceDesc;
    
    @ColumnInfo(name = "device_ip_address")
    private String deviceIpAddress;
    
    @ColumnInfo(name = "device_port")
    private int devicePort;

    @ColumnInfo(name = "out_port")
    private int outPort;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getControlDeviceId() {
        return controlDeviceId;
    }

    public void setControlDeviceId(int controlDeviceId) {
        this.controlDeviceId = controlDeviceId;
    }


    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public String getDeviceIpAddress() {
        return deviceIpAddress;
    }

    public void setDeviceIpAddress(String deviceIpAddress) {
        this.deviceIpAddress = deviceIpAddress;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }


    public int getOutPort() {
        return outPort;
    }

    public void setOutPort(int outPort) {
        this.outPort = outPort;
    }
}
