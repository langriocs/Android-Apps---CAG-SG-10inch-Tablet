package com.avl.cag10inchApp.repository;

import com.avl.cag10inchApp.model.vo.RoomDevice;

public interface IRoomDevice {

    void connect(String ip, int port);
    void disconnect();

    void setRoomDevice(RoomDevice roomDevice);
    RoomDevice getRoomDevice();

    void cleanup();
}
