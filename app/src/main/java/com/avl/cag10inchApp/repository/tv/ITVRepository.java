package com.avl.cag10inchApp.repository.tv;

public interface ITVRepository {

    void getPowerState();
    void getVolume();
    void getMuteState();
    void setVolume(int volume);
    void turnOn();
    void turnOff();
    void setMute(boolean mute);
//    void connect(String ip, int port);
//    void disconnect();
    void getStatus();
    void setListener(ITVListener listener);
//    void cleanup();
}
