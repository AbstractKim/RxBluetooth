package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Peter Beomseok Kim on 5/20/2017.
 */

public class RxPeterBluetooth {
   static public final String TAG = "RxPeterBluetooth";

    /**
     * to confirm whether the device support BT
     * @return true(supported BT) or false(not supported BT)
     */
    static public boolean isSupprot(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "isEnableBluetooth() - This device does not support Bluetooth");
            return false;
        }else{
            Log.d(TAG, "isEnableBluetooth() - This device supports Bluetooth");
            return true;
        }
    }

    /**
     * to check whether BT is enabled
     * @return true(enabled BT) or false(enabled BT)
     */
    static public boolean isEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    /**
     * enable or disable BT
     * @param b - boolean to enable or disable BT
     */
    static public void enable(boolean b){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.d(TAG, "enable() - This devices does not suppport Bluetooth");
            return;
        }
        if(b)
            bluetoothAdapter.enable();
        else
            bluetoothAdapter.disable();
    }

    /**
     * Create Observable which wraps BroadcastReceiver of ACTION_STATE_CHANGED and emits EXTRA STATE
     * @return Observable that produces state changes
     */
    static public Observable<Integer> createStateChangedObservable(final Context context){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Integer> emitter)
                    throws Exception {
                final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        int stateChanged = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                        Log.d(TAG, new StringBuffer(
                                "createStateChangedObservable - onReceive() - EXTRA_STATE: ")
                                .append(stateChanged).toString());
                        emitter.onNext(stateChanged);
                    }
                };

                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        context.unregisterReceiver(broadcastReceiver);
                        Log.d(TAG, "unregisterReceiver of ACTION_STATE_CHANGED");
                    }
                });

                IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
                context.registerReceiver(broadcastReceiver, filter);
                Log.d(TAG, "registerReceiver of ACTION_STATE_CHANGED");

            }
        });
    }

    /**
     * stateChanged(int) to String
     * @param stateChanged :
     * @return String
     */
    static public String toString(int stateChanged){
        switch(stateChanged){
            case BluetoothAdapter.STATE_OFF:
                return "OFF";
            case BluetoothAdapter.STATE_TURNING_ON:
                return "Turning On";
            case BluetoothAdapter.STATE_ON:
                return "ON";
            case BluetoothAdapter.STATE_TURNING_OFF:
                return "Turning Off";
            default:
                return "NA";
        }
    }

    /**
     * Create Observable which emits paried BluetoothDevices
     *      -It is not asynchronous to get paired devices so that it does not need to use Observable
     *      -but for unity with discovery it create Observable
     * @return Observable that creates Bluetooth device
     */
    static public Observable<BluetoothDevice> createPairedDeviceObservable(){
        return Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BluetoothDevice> emitter)
                    throws Exception {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    String errorMsg ="This devices does not support Bluetooth";
                    Log.d(TAG, new StringBuffer("createPairedDeviceObservable() - ")
                            .append(errorMsg).toString());
                   // emitter.onError(new NullThrowable(errorMsg));
                    return;
                }
                if(bluetoothAdapter != null){
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    if(pairedDevices.size() > 0){
                        for(BluetoothDevice device: pairedDevices){
                            emitter.onNext(device);
                            Log.d(TAG, new StringBuffer(
                                    "createDiscoveryObservable -")
                                    .append(device.getName()).toString());
                        }
                    }
                }
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d(TAG,
                                "createDiscoveryObservable()- cancel");
                    }
                });
            }


        });
    }

    /**
     * Create Observable which wraps BroadcastReceiver of ACTION_FOUND and emits BluetoothDevices
     * @return
     */
    static public Observable<BluetoothDevice> createDiscoveryObservable(final Context context){
        return Observable.create(new ObservableOnSubscribe<BluetoothDevice>() {
            @Override
            public void subscribe(final @NonNull ObservableEmitter<BluetoothDevice> emitter)
                    throws Exception {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    String errorMsg ="This devices does not support Bluetooth";
                    Log.d(TAG, new StringBuffer("createDiscoveryObservable() - ")
                            .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }
                // Create a BroadcastReceiver for ACTION_FOUND.
                final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        Log.d(TAG, "createDiscoveryObservable() - receiver called");
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            // Discovery has found a device. Get the BluetoothDevice
                            // object and its info from the Intent.
                            BluetoothDevice device = intent
                                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if(device == null){
                                Log.d(TAG, "device is null");
                                return;
                            }
                            Log.d(TAG, new StringBuffer(
                                    "createDiscoveryObservable - onReceive() - ACTION_FOUND: ")
                                    .append(device.getAddress()).toString());
                            emitter.onNext(device);

                        }
                    }
                };


                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        context.unregisterReceiver(broadcastReceiver);
                        Log.d(TAG,
                                "createDiscoveryObservable()- unregisterReceiver for ACTION_FOUND");
                    }
                });

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                context.registerReceiver(broadcastReceiver, filter);
                Log.d(TAG, "createDiscoveryObservable() - registerReceiver for ACTION_FOUND ");

                if(bluetoothAdapter != null){
                    bluetoothAdapter.startDiscovery();
                    Log.d(TAG, "createDiscoveryObservable() - startDiscovery");
                }else{
                    Log.d(TAG,
                            "createDiscoveryObservable() - This device does not support Bluetooth");
                }
            }
        });
    }

    /**
     * Create Observable which creates bt connection
     *  - it should be run on thread
     * @param device
     * @return
     */
    static public Observable<BluetoothSocket> createConnectObservable(final BluetoothDevice device, UUID uuid){
        return createConnectObservable(device, uuid, true);
    }

    static public Observable<BluetoothSocket> createConnectObservable(final BluetoothDevice device
            , final UUID uuid, final boolean secured){
        return Observable.create(new ObservableOnSubscribe<BluetoothSocket>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BluetoothSocket> emitter)
                    throws Exception {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    String errorMsg ="This devices does not suppport Bluetooth";
                    Log.d(TAG, new StringBuffer("createConnectObservable() - ")
                            .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }
                if(device == null){
                    String errorMsg ="BluetoothDevice is null";
                    Log.d(TAG, new StringBuffer("createConnectObservable() - ")
                            .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }
                if(uuid == null){
                    String errorMsg ="UUID is null";
                    Log.d(TAG, new StringBuffer("createConnectObservable() - ")
                            .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }

                Log.d(TAG, new StringBuffer("set UUID: ").append(uuid).toString());
                BluetoothSocket bluetoothSocket = null;

                try{
                    if(secured) {
                        bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
                        Log.d(TAG, "createConnectObservable() " +
                                "- createRfcommSocketToServiceRecord()");
                    }
                    else {
                        bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                        Log.d(TAG, "createConnectObservable() " +
                                "- createInsecureRfcommSocketToServiceRecord()");
                    }
                }catch (IOException e){
                    Log.e(TAG, "createConnectObservable() - Socket's create() method failed", e);
                }

                /**
                 * this part should be run on thread
                 */
                // cancel discovery because otherwise shows down the connection.
                bluetoothAdapter.cancelDiscovery();
                try{
                    // connect to the remote device through the socket. this call blocks
                    // until it succeeds or throws an exception.
                    bluetoothSocket.connect();
                }catch (IOException connectException){
                    // unable to connect; close the socket and return.
                    //emitter.onError(connectException);
                    try{
                        bluetoothSocket.close();
                    } catch (IOException closeException){
                        Log.e(TAG, "createConnectObservable() - Could not close the client socket"
                                , closeException);
                        //emitter.onError(closeException);
                    }
                    return;
                }
                // The connection attempt succeeded. emits Bluetooth Socket
                emitter.onNext(bluetoothSocket);
                Log.e(TAG, "createConnectObservable() - connected");
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d(TAG, "createConnectObservable() - cancel()");
                    }
                });
            }
        });
    }


    static public Observable<BluetoothSocket> createServerObservable(final String name, final UUID uuid){
        return Observable.create(new ObservableOnSubscribe<BluetoothSocket>() {
            boolean flag = true;
            BluetoothServerSocket serverSocket = null;
            BluetoothSocket bluetoothSocket = null;
            @Override
            public void subscribe(@NonNull ObservableEmitter<BluetoothSocket> emitter)
                throws Exception {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    String errorMsg ="This devices does not suppport Bluetooth";
                    Log.d(TAG, new StringBuffer("createServerObservable() - ")
                        .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }

                if(uuid == null){
                    String errorMsg ="UUID is null";
                    Log.d(TAG, new StringBuffer("createServerObservable() - ")
                        .append(errorMsg).toString());
                    //emitter.onError(new NullThrowable(errorMsg));
                    return;
                }

                Log.d(TAG, new StringBuffer("set UUID: ").append(uuid).toString());


                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        flag = false;
                        if(serverSocket != null)
                            serverSocket.close();
                        Log.d(TAG, "createServerObservable() - cancel()");
                    }
                });

                while(flag) {
                    serverSocket = null;
                    bluetoothSocket = null;
                    try {
                        // MY_UUID is the app's UUID string, also used by the client code.
                        serverSocket =
                            bluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
                    } catch (IOException e) {
                        Log.e(TAG, "createServerObservable() " + "- listenUsingRfcommWithServiceRecord() method failed", e);
                    }

                    /**
                     * this part should be run on thread
                     */

                    while (true) {
                        try {
                            Log.d(TAG, "createServerObservable() - accept()");
                            bluetoothSocket = serverSocket.accept();
                        } catch (IOException e) {
                            Log.e(TAG, "createServerObservable() - Socket's accept() method failed",
                                e);
                            break;
                        }

                        if (bluetoothSocket != null) {
                            // A connection was accepted. Perform work associated with
                            // the connection in a separate thread.
                            emitter.onNext(bluetoothSocket);
                            serverSocket.close();
                            Log.d(TAG, "createServerObservable() - a device is connected");
                            break;
                        }
                    }
                }

                Log.d(TAG, "createServerObservable() - mFlag is false - close server");

            }
        });
    }

}
