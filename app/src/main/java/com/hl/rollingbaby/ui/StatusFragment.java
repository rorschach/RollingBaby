package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
//import android.widget.ImageView;
=======
>>>>>>> RollingBaby/master
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

//import it.gmariotti.cardslib.library.internal.Card;
//import it.gmariotti.cardslib.library.view.CardViewNative;


public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";

    private int currentTemperature;
    private int settingTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    private OnStatusFragmentInteractionListener mListener;

    private TextView temperatureText;
//    private TextView humidityText;
    private TextView soundText;
    private TextView swingText;

    public static StatusFragment newInstance(int currentTem, int settingTem,
                String heatingState, String soundMode, int playState, String swingMode) {
        StatusFragment fragment  = new StatusFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_CURRENT_TEMPERATURE, currentTem);
        args.putInt(Constants.ARG_SETTING_TEMPERATURE, settingTem);
        args.putString(Constants.ARG_HEATING_STATE, heatingState);
        args.putString(Constants.ARG_SOUND_MODE, soundMode);
        args.putInt(Constants.ARG_PLAY_STATE, playState);
        args.putString(Constants.ARG_SWING_MODE, swingMode);
        Log.d(TAG, ".." + currentTem +  + settingTem + soundMode
                + playState + swingMode + "..");
        fragment.setArguments(args);
        return fragment;
    }
    public StatusFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTemperature = getArguments().getInt(Constants.ARG_CURRENT_TEMPERATURE);
            settingTemperature = getArguments().getInt(Constants.ARG_SETTING_TEMPERATURE);
            mHeatingState = getArguments().getString(Constants.ARG_HEATING_STATE);
            mSoundMode = getArguments().getString(Constants.ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(Constants.ARG_PLAY_STATE);
            mSwingMode = getArguments().getString(Constants.ARG_SWING_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

//        initViews(view);

        return view;
    }

   /* private void initViews(View view) {
        Card card_temperature = new Card(getActivity());
//        card_humidity = new Card(getActivity());
        Card card_music = new Card(getActivity());
        Card card_swing = new Card(getActivity());

        CardViewNative cardView_temperature = (CardViewNative) view.findViewById(R.id.carddemo);
//        cardView_humidity = (CardViewNative) view.findViewById(R.id.carddemo1);
        CardViewNative cardView_music = (CardViewNative) view.findViewById(R.id.carddemo2);
        CardViewNative cardView_swing = (CardViewNative) view.findViewById(R.id.carddemo3);

        card_temperature.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), TemperatureActivity.class);
                sendIntent(intent);
                startActivityForResult(intent, 0);
                Log.d(TAG, currentTemperature + ":" + mHeatingState);
            }
        });

//        card_humidity.setOnClickListener(new Card.OnCardClickListener() {
//            @Override
//            public void onClick(Card card, View view) {
//                Intent intent = new Intent(getActivity(), HumidityActivity.class);
//                startActivity(intent);
//            }
//        });

        card_music.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), SoundActivity.class);
                sendIntent(intent);
                startActivityForResult(intent, 1);
                Log.d(TAG, mSoundMode + ":" + mPlayState);
            }
        });

        card_swing.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), SwingActivity.class);
                sendIntent(intent);
                startActivityForResult(intent, 2);
                Log.d(TAG, mSwingMode);
            }
        });

        cardView_temperature.setCard(card_temperature);
//        cardView_humidity.setCard(card_humidity);
        cardView_music.setCard(card_music);
        cardView_swing.setCard(card_swing);

        temperatureText = (TextView) view.findViewById(R.id.temperature_state);
//        humidityText = (TextView) view.findViewById(R.id.humidity_state);
        soundText = (TextView) view.findViewById(R.id.sound_state);
        swingText = (TextView) view.findViewById(R.id.swing_state);
        setCardStatus();

    }*/


    private void sendIntent(Intent intent) {
        intent.putExtra(Constants.CURRENT_TEMPERATURE_VALUE,
                currentTemperature);
        intent.putExtra(Constants.SETTING_TEMPERATURE_VALUE,
                settingTemperature);
        intent.putExtra(Constants.HEATING_STATE, mHeatingState);
        intent.putExtra(Constants.CURRENT_SOUND_MODE, mSoundMode);
        intent.putExtra(Constants.PLAY_STATE, mPlayState);
        intent.putExtra(Constants.CURRENT_SWING_MODE, mSwingMode);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStatusFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnSoundInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    currentTemperature = data.getIntExtra(Constants.CURRENT_TEMPERATURE_VALUE,
                            Constants.DEFAULT_TEMPERATURE);
                    settingTemperature = data.getIntExtra(Constants.SETTING_TEMPERATURE_VALUE,
                            Constants.DEFAULT_TEMPERATURE);
                    mHeatingState = data.getStringExtra(Constants.HEATING_STATE);
                }
                break;
            case 1:
                if (resultCode == -1) {
                    mSoundMode = data.getStringExtra(Constants.CURRENT_SOUND_MODE);
                    mPlayState = data.getIntExtra(Constants.PLAY_STATE, Constants.SOUND_STOP);
                }
                break;
            case 2:
                if (resultCode == -1) {
                    mSwingMode = data.getStringExtra(Constants.CURRENT_SWING_MODE);
                }
                break;
            default:
                break;
        }
        setCardStatus();
        Log.d(TAG, "onActivityResult : " + currentTemperature + settingTemperature
                + mHeatingState + mSoundMode + mPlayState + mSwingMode);
    }

    public void getCardStatus(int currentTem, int settingTem,
                              String heatingState, String soundMode,
                              int playState, String swingMode) {
        currentTemperature = currentTem;
        settingTemperature = settingTem;
        mHeatingState = heatingState;
        mSoundMode = soundMode;
        mPlayState = playState;
        mSwingMode = swingMode;
        Log.d(TAG, currentTemperature + settingTemperature + mHeatingState
                + mSoundMode + mPlayState + mSwingMode);
        setCardStatus();
    }

    public void setCardStatus() {
        String heating_tem = "";
        String sound_tem = "";
        String swing_tem = "";
        String play_tem;

        if (mHeatingState.equals(Constants.HEATING_CLOSE)) {
            heating_tem = getActivity().getResources().getString(R.string.unHeating);
        }else if(mHeatingState.equals(Constants.HEATING_OPEN)){
            heating_tem = getActivity().getResources().getString(R.string.heating);
        }

        if (mSoundMode.equals(Constants.SOUND_MUSIC)) {
            sound_tem = getActivity().getResources().getString(R.string.music_mode);
        }else if (mSoundMode.equals(Constants.SOUND_STORY)){
            sound_tem = getActivity().getResources().getString(R.string.story_mode);
        }

        if (mSwingMode.equals(Constants.SWING_SLEEP)) {
            swing_tem = getActivity().getResources().getString(R.string.swing_sleep);
        }else if (mSwingMode.equals(Constants.SWING_CLOSE)) {
            swing_tem = getActivity().getResources().getString(R.string.swing_close);
        }

        if (mPlayState == 0) {
            play_tem = getActivity().getResources().getString(R.string.isPlaying);
        }else {
            play_tem = getActivity().getResources().getString(R.string.stop);
        }

        if (settingTemperature == currentTemperature) {
            temperatureText.setText(currentTemperature + "℃ / " + heating_tem);
        } else {
            temperatureText.setText(
                    currentTemperature + "~" + settingTemperature + "℃ / " + heating_tem);
        }
        soundText.setText(sound_tem + " / " + play_tem);
        swingText.setText(swing_tem);
    }

    public int getSettingTemperature() {
        return settingTemperature;
    }

    public interface OnStatusFragmentInteractionListener {

        void geMessageFromServer(String readMessage);

    }
}
