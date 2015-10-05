package com.hl.rollingbaby.interfaces;

/**
 * Created by root on 15-10-2.
 */
public interface MessageProcesser {

    public void sendRefreshRequest();

    public void sendSingleMessage(String tag);

    public void parseMessage(String readMessage);
}
