package com.avl.cag10inchApp.adapter;

import com.avl.cag10inchApp.model.DisplayOutputItem;
import com.avl.cag10inchApp.model.LEDPanelOutput;

public interface ILEDPanelOutputListener {
    void onLEDOutputItemClick(LEDPanelOutput item);
}
