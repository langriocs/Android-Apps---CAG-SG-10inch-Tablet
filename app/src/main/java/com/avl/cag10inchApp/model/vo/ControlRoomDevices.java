package com.avl.cag10inchApp.model.vo;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class ControlRoomDevices {
    @Embedded
    public ControlDevice controlDevice;

    @Relation(
        parentColumn = "id",
        entityColumn = "control_device_id"
    )
    public List<RoomDevice> roomDevices;
}
