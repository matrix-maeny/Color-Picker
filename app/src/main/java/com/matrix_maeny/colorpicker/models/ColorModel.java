package com.matrix_maeny.colorpicker.models;

public class ColorModel {
    private int color;
    private String name,hexCode,argbCode;
    int position;

    public ColorModel(int position, String name,int color, String hexCode, String argbCode) {
        this.color = color;
        this.name = name;
        this.hexCode = hexCode;
        this.argbCode = argbCode;
        this.position = position;
    }


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHexCode() {
        return hexCode;
    }

    public void setHexCode(String hexCode) {
        this.hexCode = hexCode;
    }

    public String getArgbCode() {
        return argbCode;
    }

    public void setArgbCode(String argbCode) {
        this.argbCode = argbCode;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
