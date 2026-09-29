package com.avl.cagApp.repository.ledwall;

public interface ILEDWallRepository {
    void setPreset(int preset);
//    void connect(String ip, int port);
//    void disconnect();
    void setListener(ILEDWallListener listener);
    void cleanup();
}
