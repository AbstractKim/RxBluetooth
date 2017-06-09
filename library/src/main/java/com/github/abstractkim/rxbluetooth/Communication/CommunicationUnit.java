package com.github.abstractkim.rxbluetooth.Communication;

import android.util.Log;
import com.github.abstractkim.rxbluetooth.Communication.Error.DisconnectedThrowable;
import com.github.abstractkim.rxbluetooth.Communication.Error.NullThrowable;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Beomseok on 5/23/2017.
 */

public class CommunicationUnit {
    private final String TAG = this.getClass().getSimpleName();
    InputStream mIn;
    OutputStream mOut;
    CommunicationStrategy mCommunicationStrategy;
    public CommunicationUnit(CommunicationStrategy communicationStrategy){
        mCommunicationStrategy = communicationStrategy;
        buildUpConnection();
    }

    /**
     * Set up InputStream and OutputStream for communication
     */
    public void buildUpConnection(){
        if(mCommunicationStrategy == null){
            Log.d(TAG, "mCommunicationStrategy is null");
            return;
        }
        mIn = mCommunicationStrategy.getInputStream();
        mOut = mCommunicationStrategy.getOutputStream();
    }

    /**
     * Create Observable which read bytes from InputStream
     *  - it must be run on thread (not UI Thread)
     * @return Observable that wraps InputStream and read (blocking function)
     */
   public Observable<Packet> createReadObservable(){
        return Observable.create(new ObservableOnSubscribe<Packet>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Packet> emitter) throws Exception {
                /**
                 * this subscription must be run on thread (not UI thread)
                 */
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        Log.d(TAG, "createReadObservable() - cancel()");
                    }
                });
                if(mIn == null){
                    String errorMsg = "InputStream is null. It should call buildUpConnection()";
                    Log.d(TAG, "createReadObservable() - " + errorMsg);
                    emitter.onError(new NullThrowable(errorMsg));
                    return;
                }
                byte[] buffer = new byte[1024];
                int numBytes = 0;  // bytes returned from read()
                //Keep listening to the InpuStream until an exception occurs.
                while(true) {
                    try {
                        //read from the InputStream
                        numBytes = mIn.read(buffer);
                        //create and emit which wraps the obtained bytes
                        Log.d(TAG, new StringBuffer("Read Msg:").append(new String(buffer)).toString());
                        emitter.onNext(new Packet(buffer, numBytes));
                    } catch (IOException e) {
                        String errorMsg = "Input stream was disconnected";
                        Log.d(TAG, "createReadObservable() - " + errorMsg);
                        emitter.onError(new DisconnectedThrowable(errorMsg));
                        break;
                    }
                }
            }
        });
    }

    /**
     * Crreate Completable which writes bytes to OutputStream
     * @param packet
     * @return Completable that wraps OutputStream and write (blocking function)
     */
    public Completable createWriteObservable(final Packet packet){
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(@NonNull CompletableEmitter emitter) throws Exception {
                if(packet == null) {
                    Log.d(TAG, "packet is null");
                    emitter.onError(new NullThrowable("packet is null"));
                    return;
                }
                if(packet.mData == null) {
                    Log.d(TAG, "packet.mData is null");
                    emitter.onError(new NullThrowable("packet.mData is null"));
                    return;
                }
                try {
                    mOut.write(packet.mData);
                    mOut.flush();
                    System.out.print(TAG + "(peter) write: " + new String(packet.mData));
                    Log.w(TAG, "Write : "+new String(packet.mData));
                    emitter.onComplete();
                } catch (IOException e) {
                    Log.w(TAG, "Write Error" + e.getMessage() );
                    emitter.onError(e);
                }
            }
        });
    }


}
