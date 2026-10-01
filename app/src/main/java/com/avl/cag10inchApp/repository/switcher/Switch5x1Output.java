package com.avl.cag10inchApp.repository.switcher;

public enum Switch5x1Output {
    USB_1(1),
    USB_2(2),
    HDMI_3(3),
    HDMI_4(4),
    HDMI_5(5);

    private final int value;


    Switch5x1Output(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
