package com.hl.rollingbaby.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hl.rollingbaby.R;
import com.hl.rollingbaby.bean.Constants;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class StatusFragment extends Fragment {

    private static final String TAG = "StatusFragment";

    private static final String ARG_TEMPERATURE = Constants.CURRENT_TEMPERATURE_VALUE;
    private static final String ARG_HEATING_STATE = Constants.HEATING_STATE;
    private static final String ARG_SOUND_MODE = Constants.CURRENT_SOUND_MODE;
    private static final String ARG_PLAY_STATE = Constants.PLAY_STATE;
    private static final String ARG_SWING_MODE = Constants.CURRENT_SWING_MODE;

    private int mTemperature;
    private String mHeatingState;
    private String mSoundMode;
    private int mPlayState;
    private String mSwingMode;

    private OnStatusFragmentInteractionListener mListener;

//    private Card card_temperature;
//    private Card card_humidity;
//    private Card card_music;
//    private Card card_swing;

//    private CardViewNative cardView_temperature;
//    private CardViewNative cardView_humidity;
//    private CardViewNative cardView_music;
//    private CardViewNative cardView_swing;

    private TextView temperatureText;
//    private TextView humidityText;
    private TextView soundText;
    private TextView swingText;
//    private ArrayList<String> list = new ArrayList<>();

    public static StatusFragment newInstance(int temperature, String heatingState,
                String soundMode, int playState, String swingMode) {
        StatusFragment fragment  = new StatusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TEMPERATURE, temperature);
        args.putString(ARG_HEATING_STATE, heatingState);
        args.putString(ARG_SOUND_MODE, soundMode);
        args.putInt(ARG_PLAY_STATE, playState);
        args.putString(ARG_SWING_MODE, swingMode);
        Log.d(TAG, ".." + temperature + heatingState + soundMode
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
            mTemperature = getArguments().getInt(ARG_TEMPERATURE);
            mHeatingState = getArguments().getString(ARG_HEATING_STATE);
            mSoundMode = getArguments().getString(ARG_SOUND_MODE);
            mPlayState = getArguments().getInt(ARG_PLAY_STATE);
            mSwingMode = getArguments().getString(ARG_SWING_MODE);
            Log.d(TAG, "..." +mTemperature + mHeatingState + mSoundMode
                    + mPlayState + mSwingMode + "...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status, container, false);

        initViews(view);

        return view;
    }

    private void initViews(View view) {
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
//                Intent intent = new Intent(getActivity(), TemperatureActivity.class);
//                intent.putExtra(Constants.CURRENT_TEMPERATURE_VALUE,
//                        mTemperature);//notice here!
//                intent.putExtra(Constants.HEATING_STATE, mHeatingState);
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), TemperatureActivity.class);
                sendIntent(intent);
                startActivityForResult(intent, 0);
                Log.d(TAG, mTemperature + ":" + mHeatingState);
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
//                intent.putExtra(Constants.CURRENT_SOUND_MODE,
//                        mSoundMode);
//                intent.putExtra(Constants.PLAY_STATE, mPlayState);
//                startActivity(intent);
                sendIntent(intent);
                startActivityForResult(intent, 1);
                Log.d(TAG, mSoundMode + ":" + mPlayState);
            }
        });

        card_swing.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), SwingActivity.class);
//                intent.putExtra(Constants.CURRENT_SWING_MODE, mSwingMode);
//                startActivity(intent);
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
//        mListener.geMessageFromServer("T.C.36;SO.S.1;SW.C;");
        setCardStatus();

    }


    private void sendIntent(Intent intent) {
        intent.putExtra(Constants.CURRENT_TEMPERATURE_VALUE,
                mTemperature);//notice here!
        intent.putExtra(Constants.HEATING_STATE, mHeatingState);
        intent.putExtra(Constants.CURRENT_SOUND_MODE,
                mSoundMode);
        intent.putExtra(Constants.PLAY_STATE, mPlayState);
        intent.putExtra(Constants.CURRENT_SWING_MODE, mSwingMode);
        getActivity().setResult(Activity.RESULT_OK, intent);
//        startActivity(intent);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStatusFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult : " + mTemperature);
        setCardStatus();
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    mTemperature = data.getIntExtra(Constants.CURRENT_TEMPERATURE_VALUE,
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
        Log.d(TAG, "onActivityResult : " + mTemperature + mHeatingState
                + mSoundMode + mPlayState + mSwingMode);
    }

    public void getCardStatus(int temperature,String heatingState, String soundMode,
                              int playState, String swingMode) {
        mTemperature = temperature;
        mHeatingState = heatingState;
        mSoundMode = soundMode;
        mPlayState = playState;
        mSwingMode = swingMode;
        Log.d(TAG, mTemperature + mHeatingState + mSoundMode + mPlayState + mSwingMode);
        setCardStatus();
    }

    public void setCardStatus() {
        String temper_tem = mTemperature + "℃";
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

        temperatureText.setText(temper_tem + " / " + heating_tem);
        soundText.setText(sound_tem + " / " + play_tem);
        swingText.setText(swing_tem);
        mListener.hideRefresh();
    }

//    public void setMessageManager(MessageManager obj) {
//        messageManager = obj;
//        Log.d(TAG, "is in MessageManager : " + obj);
//    }

    public interface OnStatusFragmentInteractionListener {
        void geMessageFromServer(String readMessage);

        void hideRefresh();
    }
}
