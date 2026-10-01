package com.avl.cag10inchApp.repository.room.roomservice;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.avl.cag10inchApp.model.vo.ControlDevice;
import com.avl.cag10inchApp.model.vo.ControlRoomDevices;
import com.avl.cag10inchApp.model.vo.RoomDevice;

import java.util.List;

@Dao
public interface ControlDeviceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveControlDevice(ControlDevice deviceInfo);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveRoomDevices(List<RoomDevice> roomDevices);

    @Transaction
    default void saveControlRoomDevices(ControlDevice controlDevice, List<RoomDevice> roomDevices) {
        saveControlDevice(controlDevice);
        saveRoomDevices(roomDevices);
    }

    @Transaction
    @Query("SELECT * FROM control_device WHERE ip_address = :ipAddress")
    LiveData<ControlRoomDevices> getControlDeviceWithRoomDevicesByIpAddress(String ipAddress);

    @Transaction
    @Query("SELECT * FROM control_device WHERE ip_address = :ipAddress")
    LiveData<ControlDevice> getControlDeviceByIpAddress(String ipAddress);
}
