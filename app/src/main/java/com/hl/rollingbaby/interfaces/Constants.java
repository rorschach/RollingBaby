package com.hl.rollingbaby.interfaces;

/**
 * Created by test on 15-6-11.
 * 该接口用于存放各种常量
 */
public interface Constants {
    /*
     * message type
     * 和服务器同通信的消息类型
     */
    int MESSAGE_READ = 0x400 + 1;
    int MESSAGE_SEND = 0x400 + 2;
    int CONNECT_SUCCESS = 0x400 + 4;
    int CONNECT_FAILED = 0x400 + 8;

    String ADDRESS_PORT = "192.168.23.5:7838";
    /*
     * params to transport intent
     * 用于启动Intent的参数
     */
    String ARG_CURRENT_TEMPERATURE = "CURRENT_TEMPERATURE";
    String ARG_SETTING_TEMPERATURE = "SETTING_TEMPERATURE";
    String ARG_HUMIDITY = "HUMIDITY_VALUE";
    String ARG_HEATING_STATE = "HEATING_STATE";
    String ARG_SOUND_MODE = "SOUND_MODE";
    String ARG_PLAY_STATE = "PLAY_STATE";
    String ARG_SWING_MODE = "SWING_MODE";

    int DEFAULT_TEMPERATURE = 33;           //默认温度

    /*
     * 和服务器通信的消息的字段
     */
    String REFRESH_TAG = "ref";   //刷新请求

    String TEMPERATURE_TAG = "t";
    String TEMPERATURE_CLOSE = "c";
    String TEMPERATURE_UP = "u";            //升温
    String TEMPERATURE_DOWN = "d";          //降温
    //t.u.40;

    String HUMIDITY_TAG = "h";              //湿度标志
    //h.46;

    String MUSIC_TAG = "m";                 //音乐模式
    String STORY_TAG = "s";                 //故事模式
    int SOUND_PLAY = 0;                     //发送／接收到的播放指令
    int SOUND_STOP = 1;                     //停止播放
    int SOUND_NEXT = 2;                     //上一首
    int SOUND_PREVIOUS = 3;                 //下一首
    int SOUND_SEND_PAUSE = 0;               //发送暂停指令
    int SOUND_RECEIVE_PAUSE = 4;            //接收暂停指令
    //m.0;   m.0    s.0   s.4

    String SWING_TAG = "w";        //摇摆模式
    String SWING_OPEN = "o";       //开启摇摆
    String SWING_CLOSE = "c";       //关闭摇摆
    //w.o;

}
