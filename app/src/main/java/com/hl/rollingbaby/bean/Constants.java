package com.hl.rollingbaby.bean;

/**
 * Created by test on 15-6-11.
 */
public interface Constants {
    /*
     *message type
     */
    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MESSAGE_SEND = 0x400 + 2;
    public static final int CONNECT_SUCCESS = 0x400 + 4;
    public static final int CONNECT_FAILED = 0x400 + 8;
//    public static final int UPDATE_UI = 0x400 + 16;

    /*
     *constants in SharedPreference file
     */
    public static final String FILE_NAME = "data";

    public static final String CURRENT_TEMPERATURE_VALUE = "TEMPERATURE";
    public static final String HEATING_STATE = "HEATING_STATE";

    public static final int DEFAULT_TEMPERATURE = 36;

    public static final String CURRENT_SOUND_MODE = "SOUND_MODE";
    public static final String PLAY_STATE = "PLAY_STATE";//int

    public static final String CURRENT_SWING_MODE = "SWING_MODE";

    public static final String CLOSE = "CLOSE";
    public static final String MUSIC = "MUSIC";
    public static final String STORY = "STORY";
    public static final String SLEEP = "SLEEP";

//    public static final String ENTERTAINMENT = "ENTERTAINMENT";
//    public static final int DEFAULT_HUMIDITY = 40;
//    public static final String HUMIDITY_STATE = "HUMIDITY_STATE";//boolean

    /*
     *constants for translate to server
     */
    public static final String COMMAND_TAG = "ct";
    public static final String COMMAND_REFRESH = COMMAND_TAG + 0;
    public static final String COMMAND_EXECUTE = COMMAND_TAG + 1;

//    public static final String TIME_TAG = "TM";
//    public static final int TIME_DELAY = 0;
//
//    public static final String OPRATION_TAG = "OP";
//    public static final int DO_FIRST = 0;
//    public static final int DO_LATER = 1;
//
//    public static final String HUMIDITY_TAG = "H";
//    public int SET_HUMIDITY = 0;

//    public static final String TEMPERATURE_TAG = "T";
    public static final String HEATING_CLOSE = "c";
    public static final String HEATING_OPEN = "o";
    public int SET_TEMPERATURE = 0;

    public static final String SOUND_TAG = "so";
//    public static final String SOUND_CLOSE = "C";
    public static final String SOUND_MUSIC = "m";
    public static final String SOUND_STORY = "s";

    public static final int SOUND_PAUSE_PLAY = 0;
    public static final int SOUND_STOP = 1;
    public static final int SOUND_NEXT = 2;
    public static final int SOUND_PERVIOUS = 3;

    public static final String SWING_TAG = "sw";
    public static final String SWING_SLEEP = "s";
//    public static final String SWING_ENTERTAINMENT = "E";
    public static final String SWING_CLOSE = "c";

}
