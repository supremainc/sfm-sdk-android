package com.supremainc.sfm_sdk.structure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;

public class UFImage {

    final int UF_IMAGE_HEADER_SIZE = 7;
    final int UF_MAX_IMAGE_SIZE = (640 * 480);
    private int _width;
    private int _height;
    private int _compressed;
    private int _encrypted;
    private int _format;
    private int _templateLen;
    byte[] _buffer = new byte[UF_MAX_IMAGE_SIZE];

    public int width() {
        return this._width;
    }

    public int height() {
        return this._height;
    }

    public int compressed() {
        return this._compressed;
    }

    public int encrypted() {
        return this._encrypted;
    }

    public int format() {
        return this._format;
    }

    public int templateLen() {
        return this._templateLen;
    }

    public byte[] buffer() {
        return this._buffer;
    }


    public UFImage(int width, int height, int compressed, int encrypted, int format, int templateLen, byte[] buffer) {
        this._width = width;
        this._height = height;
        this._compressed = compressed;
        this._encrypted = encrypted;
        this._format = format;
        this._templateLen = templateLen;
        this._buffer = Arrays.copyOf(buffer, buffer.length);
    }

    public UFImage() {

    }

    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    public Bitmap getBitmap() {
        return byteArrayToBitmap(this.buffer());
    }

}
