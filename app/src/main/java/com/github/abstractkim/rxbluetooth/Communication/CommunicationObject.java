package com.github.abstractkim.rxbluetooth.Communication;

/**
 * Created by Beomseok on 6/7/2017.
 * CommunicationObject represent information of connected device
 */

public class CommunicationObject {
  public static final String TAG = CommunicationObject.class.getSimpleName();

  /**
   * Properties
   */
  private String mDeviceName;
  private CommunicationStrategy mCommunicationStrategy;

  /**
   * sets and gets
   */
  public void setDeviceName(String dn){
    mDeviceName = dn;
  }
  public String getDeviceName(){
    return mDeviceName;
  }
  public void setCommunicationStrategy(CommunicationStrategy cs){
    mCommunicationStrategy = cs;
  }
  public CommunicationStrategy getCommunicationStrategy(){
    return mCommunicationStrategy;
  }
}
