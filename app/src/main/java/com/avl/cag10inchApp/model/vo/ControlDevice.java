package com.avl.cag10inchApp.model.vo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "control_device")
public class ControlDevice {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "ip_address")
    private String ipAdd;

    @ColumnInfo(name = "room_name")
    private String roomName;

    @ColumnInfo(name = "device_ui")
    private Integer deviceUI;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getIpAdd() {
        return ipAdd;
    }

    public void setIpAdd(String ipAdd) {
        this.ipAdd = ipAdd;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getDeviceUI() {
        return deviceUI;
    }

    public void setDeviceUI(Integer deviceUI) {
        this.deviceUI = deviceUI;
    }
}
