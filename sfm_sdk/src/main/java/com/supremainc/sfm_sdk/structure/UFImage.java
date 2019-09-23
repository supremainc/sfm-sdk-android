package com.supremainc.sfm_sdk.structure;

import android.graphics.Bitmap;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UFImage {

    private static final int IMAGE_FORMAT_GRAY = 0;
    private static final int IMAGE_FORMAT_BINARY = 1;
    private static final int IMAGE_FORMAT_4BIT_GRAY = 2;
    private static final int IMAGE_FORMAT_WSQ = 3;
    private final int UF_IMAGE_HEADER_SIZE = 7;
    private final int UF_MAX_IMAGE_SIZE = (640 * 480);
    private int _width;
    private int _height;
    private int _compressed;
    private int _encrypted;
    private int _format;
    private int _imgLen;
    private int _templateLen;
    private byte[] _buffer = new byte[UF_MAX_IMAGE_SIZE];
    private byte[] _rawData = new byte[UF_MAX_IMAGE_SIZE];
    private Bitmap _bitmap;

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

    public int imgLen() {
        return this._imgLen;
    }

    public int templateLen() {
        return this._templateLen;
    }

    public byte[] buffer() {
        return this._buffer;
    }

    public byte[] rawData() {
        getBitmap();
        return _rawData;
    }


    public UFImage(int width, int height, int compressed, int encrypted, int format, int imgLen, int templateLen, byte[] receivedBuffer, byte[] rawData) {
        this._width = width;
        this._height = height;
        this._compressed = compressed;
        this._encrypted = encrypted;
        this._format = format;
        this._imgLen = imgLen;
        this._templateLen = templateLen;
        this._buffer = Arrays.copyOf(receivedBuffer, receivedBuffer.length);
        this._rawData = Arrays.copyOf(rawData, rawData.length);
    }

    public UFImage() {


    }
//
//    public Bitmap byteArrayToBitmap(byte[] $byteArray, int width, int height) {
//        this._bitmap = BitmapFactory.decodeByteArray($byteArray, 0, width*height);
//        return this._bitmap;
//    }


    public Bitmap getWSQImage() {

        for (int i = 0; i < this.width() * this.height(); i++) {

            this._rawData[i] = (byte) ~this._rawData[i];
        }
        Log.d("IMAGE", "getBitmap: " + String.format("width : %d, height : %d, imgLen : %d, templateLen : %d image format : %d", _width, _height, _imgLen, _templateLen, _format));
        Bitmap bm = Bitmap.createBitmap(width(), height(), Bitmap.Config.ALPHA_8);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(_rawData));

        this._bitmap = bm;
        return this._bitmap;

    }
    public Bitmap getBitmap() {

        if (_imgLen == 0)
            return null;

        byte[] bmImageBuffer = new byte[_imgLen];

        switch (_format) {
            case IMAGE_FORMAT_GRAY:
                for (int i = 0; i < this._imgLen; i++) {
//                    bmImageBuffer[i] = (byte) ((byte) 255 - this._buffer[i]);
                    bmImageBuffer[i] = (byte) ~this._buffer[i];
                }
                break;
            case IMAGE_FORMAT_BINARY:
                byte pixelData = 0;
                for (int i = 0; i < this._imgLen; i++) {
                    pixelData = _buffer[i / 8];
                    bmImageBuffer[i] = (byte) ~((pixelData >> (i % 8) & 1) == 1 ? 255 : 0);
                }
                break;
            case IMAGE_FORMAT_4BIT_GRAY:
                int xStart = _compressed & 0xFFFF;
                int yStart = _compressed >> 16;
                int subWidth = _encrypted & 0xFFFF;
                int subHeight = _encrypted >> 16;

                int sourceIdx = 0;
                for (int i = yStart; i < yStart + subHeight; i++) {
                    int targetIdx = i * _width + xStart;
                    for (int j = xStart; j < xStart + subWidth; j += 2) {
                        bmImageBuffer[targetIdx++] = (byte) ~((_buffer[sourceIdx] & 0x0F) << 4);
                        bmImageBuffer[targetIdx++] = (byte) ~(_buffer[sourceIdx++] & 0xF0);
                    }
                }
                break;
            default:
                bmImageBuffer = null;
                return null;

        }

        Log.d("IMAGE", "getBitmap: " + String.format("width : %d, height : %d, imgLen : %d, templateLen : %d image format : %d", _width, _height, _imgLen, _templateLen, _format));
        Bitmap bm = Bitmap.createBitmap(width(), height(), Bitmap.Config.ALPHA_8);
        bm.copyPixelsFromBuffer(ByteBuffer.wrap(bmImageBuffer));

        System.arraycopy(bmImageBuffer, 0, this._rawData, 0, bmImageBuffer.length);

        this._bitmap = bm;

        bmImageBuffer = null;

        return this._bitmap;
    }
}
