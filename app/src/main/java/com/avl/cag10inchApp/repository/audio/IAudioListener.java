package com.avl.cag10inchApp.repository.audio;

import com.avl.cag10inchApp.repository.ledwall.ILEDWallListener;
import com.avl.cag10inchApp.repository.tv.TVPowerState;

public interface IAudioListener {

    void onConnected();
    void onDisconnected();
    void onVolumeChanged(int volume);
    void onMuteChanged(boolean isMuted);
    void setListener(IAudioListener listener);
    void onError(String message);

}
