package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Beomseok on 5/23/2017.
 */

public class BtClassicCommunicationStrategy implements CommunicationStrategy {
    private final String TAG = this.getClass().getSimpleName();
    private BluetoothSocket mBluetoothSocket;
    public BtClassicCommunicationStrategy(BluetoothSocket bluetoothSocket){
        mBluetoothSocket = bluetoothSocket;
    }

    /**
     * get InputStream from BluetoothSocket
     * @return InputStream
     */
    public InputStream getInputStream(){
        if(mBluetoothSocket == null){
            Log.d(TAG, "mBluetoothSocket is null");
            return null;
        }
        InputStream in = null;
        try {
            in = mBluetoothSocket.getInputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        return in;
    }

    /**
     * get OutputStream from BluetoothSocket
     * @return OutputStream
     */
    public OutputStream getOutputStream(){
        if(mBluetoothSocket == null){
            Log.d(TAG, "mBluetoothSocket is null");
            return null;
        }
        OutputStream out = null;
        try {
            out = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }
        return out;
    }
}
