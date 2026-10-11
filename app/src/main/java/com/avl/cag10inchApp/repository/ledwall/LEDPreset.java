package com.avl.cag10inchApp.repository.ledwall;

public enum LEDPreset {
    PRESET_1(1),
    PRESET_2(2),
    PRESET_3(3),
    PRESET_4(4);

    private final int value;

    LEDPreset(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
