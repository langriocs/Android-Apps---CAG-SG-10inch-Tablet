package com.avl.cag10inchApp.viewmodel;

public enum NavigationState {
    DISPLAY_MODE_STATE(1),
    SOURCE_STATE(2),
    AUDIO_STATE(3),
    CAMERA_STATE(4);

    private final int value;

    NavigationState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
