package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothSocket;
import android.util.Log;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Beomseok on 6/7/2017.
 */

public class CommunicationManager {
  public static final String TAG = CommunicationManager.class.getSimpleName();


  public enum ROLE{
    CLIENT(1, "client"),
    SERVER(2, "server");
    public final int state;
    public final String description;
    ROLE(final int s, final String d){
      state = s;
      description = d;
    }
  }

  public enum TYPE{
    BLUETOOTHCLASSIC(1,"Bluetooth Classic"),
    WIFIP2P(2,"Wi-Fi Direct(P2P)");

    public final int state;
    public final String description;
    TYPE(final int s, final String d){
      state = s;
      description = d;
    }
  }
  /**
   * Properties
   */
  private ROLE mRole = ROLE.CLIENT; //default is client;
  //for group chatting
  private HashMap<String, CommunicationUnit> mConnectedDeviceMap = new HashMap<>();
  private List<CommunicationUnit> mTempList = new ArrayList<>();
  private Disposable mBTServerDisposable;

  /**
   * sets and gets
   */
  public void setRole(ROLE r){
    mRole = r;
  }
  public ROLE getRole(){
    return mRole;
  }
  /*
  public void addCommunicationUnit(CommunicationUnit co){
    if(co != null){
      mConnectedDeviceMap.put(co.getDeviceName(), co);
    }
  }
  */
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
                mTempList.add(
                    new CommunicationUnit(
                        new BtClassicCommunicationStrategy(bluetoothSocket)));
              }
            });
  }

  public void clear(){
    mBTServerDisposable.dispose();
  }
}
