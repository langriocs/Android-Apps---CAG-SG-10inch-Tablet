package com.avl.cag10inchApp.repository.switcher;

public interface ISwitchRepository {
    void routeInputSourceTo(Switch5x1Output output);
    void routeAV(Integer selectedInput, Integer selectedOutput);
    void routeAudio(Integer selectedInput, AudioMode mode);
    void setListener(ISwitchListener listener);
}
