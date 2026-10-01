package com.avl.cag10inchApp.repository.room;

import androidx.lifecycle.LiveData;

import com.avl.cag10inchApp.model.vo.ControlDevice;
import com.avl.cag10inchApp.model.vo.ControlRoomDevices;
import com.avl.cag10inchApp.model.vo.RoomDevice;

import java.util.List;

public interface IDeviceRoomRepository {

    LiveData<ControlDevice> fetchControlDeviceByIpAddress(String ipAddress);
    void saveControlDevice(ControlDevice controlDevice);
    void saveRoomDevices(List<RoomDevice> roomDevices);
    void saveControlRoomDevices(ControlDevice controlDevice, List<RoomDevice> roomDevices);
    LiveData<ControlRoomDevices> fetchControlDeviceWithRoomDevicesByIpAddress(String ipAddress);
}
