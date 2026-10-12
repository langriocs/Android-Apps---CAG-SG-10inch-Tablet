package com.avl.cag10inchApp.viewmodel;

public enum HallPanelView {
    HALL_MENU(1),
    HALL_DISPLAY_MODE_SELECT(2),
    HALL_SOURCE_SELECT(3),
    HALL_COMBINE_MODE(4),
    HALL_COMBINE_SOURCE(5);

    private final int value;

    HallPanelView(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
