package com.avl.cagApp.repository.switcher;

public enum AudioMode {
    SOURCE(0),
    INSERT(1);

    private final int value;

    AudioMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
