package com.github.abstractkim.rxbluetooth.Communication;

import android.util.Log;

/**
 * Created by Beomseok on 5/23/2017.
 */

public class Packet {
    private final String TAG = this.getClass().getSimpleName();
    public byte[] mData;
    public int mSize;

    /**
     * Create Packet
     * @param data
     * @param size
     */
    public Packet(byte[] data, int size) {
        if(data == null) {
            Log.d(TAG, "data is null");
            return;
        }
        mData = data;
        mSize = size;
    }

    /**
     * Create Packet
     * @param data
     */
    public Packet(byte[] data) {
        if(data == null) {
            Log.d(TAG, "data is null");
            return;
        }
        mData = data;
        mSize = data.length;
    }
}
