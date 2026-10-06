package com.avl.cag10inchApp.model;

public class DisplaySourceItem {
    private String displayName;
    private int portNumber;
    private int imgResId;

    private boolean selected = false;


    public DisplaySourceItem(String displayName, int portNumber, int imgResId) {
        this.displayName = displayName;
        this.portNumber = portNumber;
        this.imgResId = imgResId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public int getImgResId() {
        return imgResId;
    }

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
