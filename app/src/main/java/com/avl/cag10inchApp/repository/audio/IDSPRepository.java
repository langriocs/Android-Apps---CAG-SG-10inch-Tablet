package com.avl.cag10inchApp.repository.audio;

import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;

public interface IDSPRepository {
    void setMute();

    void setListener(ILEDWallListener listener);
    void cleanup();
}
