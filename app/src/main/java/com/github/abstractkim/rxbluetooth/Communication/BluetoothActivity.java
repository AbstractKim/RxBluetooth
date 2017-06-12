package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.github.abstractkim.rxbluetooth.MainActivity;
import com.github.abstractkim.rxbluetooth.R;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import java.util.List;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {
  static private final String TAG = BluetoothActivity.class.getSimpleName();
  public static final int REQUEST_ENABLE_BT = 1;
  private RecyclerView mBTListView;
  private BluetoothClientListAdapter mAdapter;
  private TextView mTvStatus;

  private RxPeterBluetooth mRxPeterBluetooth;
  private Disposable mBTStateChangedDisposable;
  private Disposable mPairedDeviceDisposable;
  private Disposable mDiscoveryDisposable;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bluetooth);
    mBTListView = (RecyclerView) findViewById(R.id.client_list);
    mBTListView.setLayoutManager(new LinearLayoutManager(this));
    mAdapter = new BluetoothClientListAdapter(this);
    mBTListView.setAdapter(mAdapter);
    mTvStatus = (TextView)findViewById(R.id.tvStatus);

    //create stateChanged observable and observer
    if(RxPeterBluetooth.isSupprot()){
      mBTStateChangedDisposable
          = RxPeterBluetooth.createStateChangedObservable(getApplicationContext())
          .subscribe(new Consumer<Integer>() {
                       @Override
                       public void accept(@NonNull Integer integer) throws Exception {
                         Log.d(TAG, "StateChanged:" + RxPeterBluetooth
                             .toString(integer.intValue()));
                       }
                     }

          );

    }else{
      Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG);
    }
  }

  public void onRadioButtonClicked(View view){
    // Is the button now checked?
    boolean checked = ((RadioButton)view).isChecked();

    //Check which radio button was clicked
    switch(view.getId()){
      case R.id.radio_client:
        if(checked){
          clear();
          Log.d(TAG, "Client is selected");

          ///////////////////////
          //show bt lists
          //create paired device observable and observer to get bt paired devices
          if(RxPeterBluetooth.isSupprot()){
            mPairedDeviceDisposable = RxPeterBluetooth.createPairedDeviceObservable()
                .subscribe(new Consumer<BluetoothDevice>() {
                  @Override
                  public void accept(@NonNull BluetoothDevice bluetoothDevice) throws Exception {
                    mAdapter.addBluetoothDevice(bluetoothDevice);
                  }
                });
          }else{
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG);
          }
          //create discovery observable and observer to get bt discovery devices
          if(RxPeterBluetooth.isSupprot()){
            mDiscoveryDisposable = RxPeterBluetooth
                .createDiscoveryObservable(getApplicationContext())
                .subscribe(new Consumer<BluetoothDevice>() {
                  @Override
                  public void accept(@NonNull BluetoothDevice bluetoothDevice) throws Exception {
                    mAdapter.addBluetoothDevice(bluetoothDevice);
                  }
                });

          }else{
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG);
          }
          /////////////////////////

          /////////////////////////
          //implement listener for setting ChatClient
          mAdapter.setReceiveBTSocketListener(new BluetoothClientListAdapter.ReceiveBTSocketListener() {
            @Override
            public void onReceiveBTSocket(ChatClient client) {
              MainActivity.mClient = client;
              Log.d(TAG,"chat client created");
              mTvStatus.setText("Connected to server");
            }
          });


        }
        break;
      case R.id.radio_server:
        if(checked){
          clear();
          Log.d(TAG, "Server is selected");
          Intent discoverableIntent =
              new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
          discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
          startActivityForResult(discoverableIntent, REQUEST_ENABLE_BT);
        }
        break;
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // Check which request we're responding to
    if (requestCode == REQUEST_ENABLE_BT)
    {
      Log.d(TAG, "REQUEST_ENABLE_BT");
      ChatServer server = new ChatServer();
      final UUID MY_UUID =
          UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
      server.startBluetoothServer(MY_UUID);
      MainActivity.mServer = server;
      Log.d(TAG,"server is started");
      mTvStatus.setText("Server is started");
      server.setUpdateConnectedDeviceListener(new ChatServer.UpdateConnectedDeviceListener() {
        @Override public void onUpdateConnectedDevice(List<CommunicationUnit> devices) {
          mTvStatus.setText("connected device:" + devices.size());
        }
      });
    }
  }

  void clear(){
    if(MainActivity.mClient != null){
      MainActivity.mClient.clear();
      MainActivity.mClient = null;
    }

    if(MainActivity.mServer != null){
      MainActivity.mServer.clear();
      MainActivity.mServer = null;
    }

    mAdapter.clearAllBluetoothDevices();
    mTvStatus.setText("");
  }
}
