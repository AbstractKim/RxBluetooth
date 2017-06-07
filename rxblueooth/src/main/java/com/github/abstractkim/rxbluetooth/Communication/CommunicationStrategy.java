package com.github.abstractkim.rxbluetooth.Communication;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Beomseok on 5/23/2017.
 */

public interface CommunicationStrategy {
    public InputStream getInputStream();
    public OutputStream getOutputStream();
}
