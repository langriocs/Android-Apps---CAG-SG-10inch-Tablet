package com.avl.cag10inchApp.model;

public class LEDPanelOutput {

    private String desc;
    private int img;
    private int outport;

    public LEDPanelOutput(String desc,int img, int outport) {
        this.desc = desc;
        this.img = img;
        this.outport = outport;
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

    public int getOutport() {
        return outport;
    }

    public void setOutport(int outport) {
        this.outport = outport;
    }
}
