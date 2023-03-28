package com.example.photopuzzle;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Pictures implements Serializable {
    Bitmap img;
    int tag;

    public Pictures(Bitmap img, int tag) {
        this.img = img;
        this.tag = tag;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
