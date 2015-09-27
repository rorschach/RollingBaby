package com.hl.rollingbaby.bean;

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
    String ARG_CURRENT_TEMPERATURE = Constants.CURRENT_TEMPERATURE_VALUE;
    String ARG_SETTING_TEMPERATURE = Constants.SETTING_TEMPERATURE_VALUE;
    String ARG_HEATING_STATE = Constants.HEATING_STATE;
    String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
    String ARG_PLAY_STATE = Constants.PLAY_STATE;
    String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    String CURRENT_TEMPERATURE_VALUE = "TEMPERATURE";
    String SETTING_TEMPERATURE_VALUE = "SETTING_TEMPERATURE";
    String HEATING_STATE = "HEATING_STATE";
    String CURRENT_SOUND_MODE = "SOUND_MODE";
    String PLAY_STATE = "PLAY_STATE";
    String CURRENT_SWING_MODE = "SWING_MODE";

    int DEFAULT_TEMPERATURE = 33;   //默认温度

    /*
     * 和服务器通信的消息的字段
     */
    String COMMAND_TAG = "ct";                  //命令标志位
    String COMMAND_REFRESH = COMMAND_TAG + 0;   //刷新请求
    String COMMAND_EXECUTE = COMMAND_TAG + 1;   //执行命令

    String HEATING_CLOSE = "c";     //关闭升温
    String HEATING_OPEN = "o";      //开启升温
    String COOL_DOWN = "cd";        //降温

    String SOUND_TAG = "so";        //声音标志位
    String SOUND_MUSIC = "m";       //音乐模式
    String SOUND_STORY = "s";       //故事模式

    int SOUND_PAUSE_PLAY = 0;       //暂停/播放
    int SOUND_STOP = 1;             //停止播放
    int SOUND_NEXT = 2;             //上一首
    int SOUND_LAST = 3;             //下一首
    int SOUND_NOTHING = 4;          //无操作

    String SWING_TAG = "sw";        //摇摆模式
    String SWING_SLEEP = "s";       //开启摇摆
    String SWING_CLOSE = "c";       //关闭摇摆

}
