package com.github.abstractkim.rxbluetooth.Communication;

import java.util.HashMap;
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
  private HashMap<String, CommunicationObject> mConnectedDeviceMap = new HashMap<>();

  /**
   * sets and gets
   */
  public void setRole(ROLE r){
    mRole = r;
  }
  public ROLE getRole(){
    return mRole;
  }
  public void addCommunicationObject(CommunicationObject co){
    if(co != null){
      mConnectedDeviceMap.put(co.getDeviceName(), co);
    }
  }
  /**
   * method related as server
   */
  public void startBluetoothServer(UUID u){


  }
}
