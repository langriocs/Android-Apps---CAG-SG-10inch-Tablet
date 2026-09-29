package com.avl.cagApp.repository.switcher;

import com.avl.cagApp.repository.tv.ITVListener;

import java.util.List;

public interface ISwitchRepository {
    void routeInputSourceTo(Switch5x1Output output);
    void routeAV(Integer selectedInput, Integer selectedOutput);
    void routeAudio(Integer selectedInput, AudioMode mode);
    void setListener(ISwitchListener listener);
}
