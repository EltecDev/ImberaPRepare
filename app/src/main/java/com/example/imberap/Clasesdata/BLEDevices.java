package com.example.imberap.Clasesdata;

import android.widget.ImageView;

public class BLEDevices {
    private String mac;
    private String name;
    private ImageView img;

    public BLEDevices(String name,String mac, ImageView im){
        this.mac = mac;
        this.name = name;
        this.img = im;
    }

    public void setNombre(String nombre) {
        this.name = nombre;
    }

    public void setMac(String info) {
        this.mac = info;
    }

    public String getNombre() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public ImageView getImg() {
        return img;
    }

}
