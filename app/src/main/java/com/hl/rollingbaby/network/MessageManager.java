package com.hl.rollingbaby.network;

import android.os.Handler;
import android.util.Log;

import com.hl.rollingbaby.bean.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by test on 15-5-28.
 */
public class MessageManager extends Thread {
    private volatile boolean isConnecting;
    private static final String TAG = "MessageManager";
    private Handler handler;
    private String mAddress;
    private int mPort;
    private static final int TIMEOUT = 5000;

    private InputStream iStream;
    private OutputStream oStream;
    public static String readMessage;

    public MessageManager(Handler handler, String host, int port) {
        this.handler = handler;
        this.mAddress = host;
        this.mPort = port;
    }

    public boolean getConnectState() {
        return isConnecting;
    }

/**
 *must do this before start connection
 */
    public void setConnectState(boolean connectState) {
        isConnecting = connectState;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        while (isConnecting) {
            try {
                socket.bind(null);
                socket.connect(new InetSocketAddress(mAddress, mPort), TIMEOUT);
                Log.d(TAG, "Launching the I/O handler...");
                try {
                    iStream = socket.getInputStream();
                    oStream = socket.getOutputStream();
                    byte[] buffer = new byte[1024];
                    //                   int bytes;
                    handler.obtainMessage(Constants.MESSAGE_SEND, this)
                            .sendToTarget();
                    Log.d(TAG, "handler is obtain sendMessage...");

                    read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        handler.obtainMessage(Constants.CONNECT_FAILED, this)
                                .sendToTarget();
                        Log.d(TAG, "socket is closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                    handler.obtainMessage(Constants.CONNECT_FAILED, this)
                            .sendToTarget();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            }
        }
    }

    private void read(byte[] buffer) {
        int bytes;
        while (true) {
            try {
                // Read from the InputStream
                bytes = iStream.read(buffer);
                if (bytes == -1) {
                    break;
                } else {
                    readMessage = new String(buffer, "UTF-8");
                }
                // Send the obtained bytes to the UI Activity
                handler.obtainMessage(Constants.MESSAGE_READ,
                        bytes, -1, buffer).sendToTarget();
                Log.d(TAG, "handler is obtain readMessage...");
            } catch (IOException e) {
                Log.e(TAG, "socket is disconnected", e);
                handler.obtainMessage(Constants.CONNECT_FAILED, this)
                        .sendToTarget();
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
            String sendMessage = new String(buffer, "UTF-8");
            Log.d(TAG, "Send : " + sendMessage);
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }


}
