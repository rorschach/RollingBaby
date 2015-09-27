package com.hl.rollingbaby.bean;

import android.os.Handler;

/**
 * Created by test on 15-6-11.
 * 获取handler的接口，由MainActivity实现
 */
public interface MessageTarget {

    public Handler getHandler();

}
