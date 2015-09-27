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
 * 负责管理与服务器之间接收和发送消息的线程
 */
public class MessageManager extends Thread {
    private boolean isConnecting;       //判断是否连接的标志位
    private static final String TAG = "MessageManager";
    private Handler handler;
    private String mAddress;    //ip地址
    private int mPort;          //端口号
    private static final int TIMEOUT = 5000;    //超时时间

    private InputStream iStream;
    private OutputStream oStream;
    public static String readMessage;

    public MessageManager(Handler handler, String host, int port) {
        this.handler = handler;
        this.mAddress = host;
        this.mPort = port;
    }

    /**
     * @return state of connection
     * 获取连接状态
     */
    public boolean getConnectState() {
        if (oStream != null && iStream != null) {
            return isConnecting;
        } else {
            return false;
        }
    }

/**
 *must do this before start connection
 * 设置连接标志位，必须在开始连接前实现
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
                    isConnecting = false;
                } finally {
                    try {
                        //关闭资源
                        socket.close();
                        handler.obtainMessage(Constants.CONNECT_FAILED, this)
                                .sendToTarget();
                        Log.d(TAG, "socket is closed");
                    } catch (IOException e) {
                        e.printStackTrace();
                        isConnecting = false;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                isConnecting = false;
                try {
                    socket.close();
                    handler.obtainMessage(Constants.CONNECT_FAILED, this)
                            .sendToTarget();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    isConnecting = false;
                }
                return;
            }
        }
    }

    /**
     *
     * @param buffer
     * 读取服务器发送的消息
     */
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
                //收到消息后将其传递给MainActivity的handleMessage方法
                handler.obtainMessage(Constants.MESSAGE_READ,
                        bytes, -1, buffer).sendToTarget();
                Log.d(TAG, "handler is obtain readMessage...");
            } catch (IOException e) {
                isConnecting = false;
                Log.e(TAG, "socket is disconnected", e);
                handler.obtainMessage(Constants.CONNECT_FAILED, this)
                        .sendToTarget();
            }
        }
    }

    /**
     * @param buffer
     * 发送字节数组类型的消息给服务器
     */
    public void write(byte[] buffer) {
        try {
            oStream.write(buffer);
            String sendMessage = new String(buffer, "UTF-8");
            Log.d(TAG, "Send : " + sendMessage);
        } catch (IOException e) {
            isConnecting = false;
            Log.e(TAG, "Exception during write", e);
        }
    }


}
