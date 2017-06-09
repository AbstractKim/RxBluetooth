package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Beomseok on 6/9/2017.
 */

public class ChatServer {
  public static final String TAG = ChatServer.class.getSimpleName();

  private List<CommunicationUnit> mCommunicationUnitList = new ArrayList<>();
  private Disposable mBTServerDisposable;
  /**
   * method related as server
   */
  public void startBluetoothServer(UUID u){
    //    final UUID MY_UUID =
    //        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    mBTServerDisposable = RxPeterBluetooth.createServerObservable("Server", u)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            new Consumer<BluetoothSocket>() {
              @Override public void accept(@NonNull BluetoothSocket bluetoothSocket)
                  throws Exception {
                Log.d(TAG, "A device is connecnt:" + bluetoothSocket.toString());
                mCommunicationUnitList.add(
                    new CommunicationUnit(
                        new BtClassicCommunicationStrategy(bluetoothSocket)));
                Log.d(TAG, "Create CommunicationUnit");
                Log.d(TAG, "mCommunicationUnitList size: " + mCommunicationUnitList.size());
              }
            });
  }
  void sendMessage(String message){
    for(CommunicationUnit unit :mCommunicationUnitList){
      unit.createWriteObservable(new Packet(message.getBytes()))
          .subscribeOn(Schedulers.io())
          .subscribe(new CompletableObserver() {
            @Override public void onSubscribe(@NonNull Disposable d) {

            }

            @Override public void onComplete() {
              Log.d(TAG, "Send Message");
            }
            @Override public void onError(@NonNull Throwable e) {

            }
          });
    }
  }
}
