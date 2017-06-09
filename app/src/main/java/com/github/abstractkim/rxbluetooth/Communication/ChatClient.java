package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import java.util.UUID;

/**
 * Created by Beomseok on 6/9/2017.
 */

public class ChatClient {
  static public final String TAG = ChatClient.class.getSimpleName();
  private CommunicationUnit mCommunicationUnit;
  private Disposable mConnectDisposable;
  void connectToServer(final BluetoothDevice d, UUID u, boolean s){
    mConnectDisposable = RxPeterBluetooth.createConnectObservable(d, u, s)
        .subscribe(new Consumer<BluetoothSocket>() {
          @Override public void accept(@NonNull BluetoothSocket bluetoothSocket) throws Exception {
            mCommunicationUnit =
                new CommunicationUnit(
                    new BtClassicCommunicationStrategy(bluetoothSocket));
          }
        });

  }
}
