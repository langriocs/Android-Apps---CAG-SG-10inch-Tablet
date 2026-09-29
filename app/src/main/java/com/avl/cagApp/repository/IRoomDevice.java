package com.avl.cagApp.repository;

import com.avl.cagApp.model.vo.RoomDevice;

public interface IRoomDevice {

    void connect(String ip, int port);
    void disconnect();

    void setRoomDevice(RoomDevice roomDevice);
    RoomDevice getRoomDevice();

    void cleanup();
}
