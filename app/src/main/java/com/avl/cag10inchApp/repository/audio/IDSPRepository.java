package com.avl.cag10inchApp.repository.audio;

import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;

public interface IDSPRepository {
    void setChannel(DSPChannel channel);
    void setVolume( int faderValue);
    void setMute( int value);
    void setListener(ILEDWallListener listener);
    void cleanup();
}
