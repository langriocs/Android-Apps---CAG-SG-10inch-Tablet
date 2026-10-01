package com.avl.cag10inchApp.repository.ledwall;

public interface ILEDWallRepository {
    void setPreset(int preset);
//    void connect(String ip, int port);
//    void disconnect();
    void getStatus();
    void setListener(ILEDWallListener listener);
    void cleanup();
}
