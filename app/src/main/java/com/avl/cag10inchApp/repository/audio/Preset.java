package com.avl.cag10inchApp.repository.audio;

public enum Preset {
    RECALL_1(1),
    RECALL_2(2);

    private final int value;

    Preset(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
