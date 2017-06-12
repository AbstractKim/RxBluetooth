package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.github.abstractkim.rxbluetooth.Communication.Error.DisconnectedThrowable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.UUID;
import org.w3c.dom.Text;

/**
 * Created by Beomseok on 6/9/2017.
 */

public class ChatClient {
  static public final String TAG = ChatClient.class.getSimpleName();
  private CommunicationUnit mCommunicationUnit;
  private Disposable mConnectDisposable;
  private Disposable mReadDisposable;

  public ChatClient(CommunicationUnit unit) {
    mCommunicationUnit = unit;
  }

  /*
    public void connectToServer(final BluetoothDevice d, UUID u, boolean s){
      mConnectDisposable = RxPeterBluetooth.createConnectObservable(d, u, s)
          .subscribe(new Consumer<BluetoothSocket>() {
            @Override public void accept(@NonNull BluetoothSocket bluetoothSocket) throws Exception {
              mCommunicationUnit =
                  new CommunicationUnit(
                      new BtClassicCommunicationStrategy(bluetoothSocket));
            }
          });

    }
  */
 public void sendMessage(String message){
    mCommunicationUnit.createWriteObservable(new Packet(message.getBytes()))
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

  public void ConnectReadMessageToTextView(final TextView v){
    mReadDisposable = mCommunicationUnit.createReadObservable()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .onErrorReturn(new Function<Throwable, Packet>() {
          @Override public Packet apply(@NonNull Throwable throwable) throws Exception {
            Log.d(TAG, "onErrorReturn is called");
            if(throwable instanceof DisconnectedThrowable) {
              v.setText("Disconnected");
            }
            return null;
          }
        })
        .subscribe(new Consumer<Packet>() {
      @Override public void accept(@NonNull Packet packet) throws Exception {
        v.setText(packet.mData.toString());
      }

    });
  }

  public void clear(){
    mConnectDisposable.dispose();
    mReadDisposable.dispose();
  }
}
