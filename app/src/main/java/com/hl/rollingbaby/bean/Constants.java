package com.hl.rollingbaby.bean;

/**
 * Created by test on 15-6-11.
 */
public interface Constants {
    /*
     *message type
     */
    int MESSAGE_READ = 0x400 + 1;
    int MESSAGE_SEND = 0x400 + 2;
    int CONNECT_SUCCESS = 0x400 + 4;
    int CONNECT_FAILED = 0x400 + 8;

    String ADDRESS_PORT = "192.168.23.5:7838";
    /*
     *params to trans intent
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

    int DEFAULT_TEMPERATURE = 30;

    /*
     *constants for translate to server
     */
    String COMMAND_TAG = "ct";
    String COMMAND_REFRESH = COMMAND_TAG + 0;
    String COMMAND_EXECUTE = COMMAND_TAG + 1;

    String HEATING_CLOSE = "c";
    String HEATING_OPEN = "o";
    String COOL_DOWN = "cd";
    //TODO:drop temperature

    String SOUND_TAG = "so";
    String SOUND_MUSIC = "m";
    String SOUND_STORY = "s";

    int SOUND_PAUSE_PLAY = 0;
    int SOUND_STOP = 1;
    int SOUND_NEXT = 2;
    int SOUND_LAST = 3;

    String SWING_TAG = "sw";
    String SWING_SLEEP = "s";
//    String SWING_ENTERTAINMENT = "E";
    String SWING_CLOSE = "c";

}
