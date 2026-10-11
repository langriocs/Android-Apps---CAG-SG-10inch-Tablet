package com.avl.cag10inchApp.repository.ledwall;

public interface ILEDWallRepository {
    void setPreset(LEDPreset preset);
    void setPresetDirect();
    void getStatus();
    void setListener(ILEDWallListener listener);
    void cleanup();
}
