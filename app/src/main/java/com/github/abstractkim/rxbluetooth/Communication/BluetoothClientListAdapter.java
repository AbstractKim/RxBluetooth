package com.github.abstractkim.rxbluetooth.Communication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.github.abstractkim.rxbluetooth.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Beomseok on 6/12/2017.
 */

public class BluetoothClientListAdapter extends RecyclerView.Adapter<BluetoothClientListAdapter.ViewHolder>{
  private static final String TAG  = BluetoothClientListAdapter.class.getSimpleName();
  private final Context mContext;
  private final List<BluetoothDevice> mBluetoothDevices = new ArrayList<>();
  private ReceiveBTSocketListener mReceiveBTSocketListener;
  public Disposable mConnectingBTDisposable;

  //define interface - ReceiveBTSocketListener
  public interface ReceiveBTSocketListener{
    void onReceiveBTSocket(ChatClient chatClient);
  }
  public void setReceiveBTSocketListener(ReceiveBTSocketListener listener){
    mReceiveBTSocketListener = listener;
  }

  //constructor
  public BluetoothClientListAdapter(Context c){
    mContext = c;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder{
    public TextView mTextView;
    public ViewHolder(View v){
      super(v);
      mTextView = (TextView) v.findViewById(R.id.client_display);
    }
  }

  public void addBluetoothDevice(BluetoothDevice device){
    mBluetoothDevices.add(device);
    String deviceName = (device.getName() == null) ? "No Name" : device.getName();
    Log.d(TAG, deviceName);
    notifyDataSetChanged();
  }

  public void clearAllBluetoothDevices(){
    mBluetoothDevices.clear();
    notifyDataSetChanged();
  }

  //Create new views (invoked by the layout manager)
  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType){
    //create a new view
    View v = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.client_text_view,parent, false);
    //set teh view's size, margins, paddings and layout parameters
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  //replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(ViewHolder holder, final int position){
    // - get element from your dataset at this position
    // - replace the conntents of the view with that element
    BluetoothDevice device = mBluetoothDevices.get(position);

    String deviceName = (device.getName() == null) ? "No Name" : device.getName();
    String str = new StringBuffer(deviceName).append("(")
        .append(device.getAddress()).append(")").toString();
    holder.mTextView.setText(str);
    Log.d(TAG, new StringBuffer("position:").append(position)
        .append("what:").append(str).toString());

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        BluetoothDevice device = mBluetoothDevices.get(position);

        String deviceName = (device.getName() == null) ? "No Name" : device.getName();
        String str = new StringBuffer(deviceName).append("(")
            .append(device.getAddress()).append(")").toString();
        //Toast.makeText(mContext, str , Toast.LENGTH_SHORT).show();

        final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        mConnectingBTDisposable = RxPeterBluetooth
            .createConnectObservable(device, MY_UUID, false)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<BluetoothSocket>() {
              @Override
              public void accept(@NonNull BluetoothSocket bluetoothSocket)
                  throws Exception {
                Toast.makeText(mContext, "connected" , Toast.LENGTH_SHORT).show();
                if(mReceiveBTSocketListener == null){
                  Log.d(TAG, "setOnClickListener () " +
                      "- mReceiveBTSocketListener is null");
                  return;
                }
                ChatClient chatClient = new ChatClient(new CommunicationUnit(
                    new BtClassicCommunicationStrategy(bluetoothSocket)));
                mReceiveBTSocketListener.onReceiveBTSocket(chatClient);
              }
            });
      }
    });

  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount(){
    Log.d(TAG, new StringBuffer("getItemCount():").append(mBluetoothDevices.size()).toString());
    return mBluetoothDevices.size();
  }
}
