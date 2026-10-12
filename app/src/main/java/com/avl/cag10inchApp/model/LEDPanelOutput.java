package com.avl.cag10inchApp.model;

public class LEDPanelOutput {

    private String desc;
    private int img;

    public LEDPanelOutput(String desc,int img) {
        this.desc = desc;
        this.img = img;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
