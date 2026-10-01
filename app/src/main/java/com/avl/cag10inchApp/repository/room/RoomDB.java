package com.avl.cag10inchApp.repository.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.avl.cag10inchApp.model.vo.ControlDevice;
import com.avl.cag10inchApp.model.vo.RoomDevice;
import com.avl.cag10inchApp.repository.room.roomservice.ControlDeviceDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {
        ControlDevice.class,
        RoomDevice.class
}, version = 1, exportSchema = false)

public abstract class RoomDB extends RoomDatabase {

    public abstract ControlDeviceDao deviceInfoDao();

    private static volatile RoomDB  INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static RoomDB getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    RoomDB.class, "cag_db.db")
                            .createFromAsset("database/cag_db.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
