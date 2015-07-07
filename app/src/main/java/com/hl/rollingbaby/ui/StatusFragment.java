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
import com.hl.rollingbaby.network.MessageManager;
import com.hl.rollingbaby.network.StatusService;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardViewNative;


public class StatusFragment extends Fragment {

    private static final String TAG = "StateFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnStatusFragmentInteractionListener mListener;

    private MessageManager messageManager;

    private Card card_temperature;
    private Card card_humidity;
    private Card card_music;
    private Card card_swing;

    private CardViewNative cardView_temperature;
    private CardViewNative cardView_humidity;
    private CardViewNative cardView_music;
    private CardViewNative cardView_swing;

    private TextView temperatureText;
    private TextView humidityText;
    private TextView palyText;
    private TextView swingText;
    private ArrayList<String> list = new ArrayList<>();

    public static StatusFragment newInstance(String param1, String param2) {
        StatusFragment fragment = new StatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public StatusFragment() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        card_temperature = new Card(getActivity());
        card_humidity = new Card(getActivity());
        card_music = new Card(getActivity());
        card_swing = new Card(getActivity());

        cardView_temperature = (CardViewNative) view.findViewById(R.id.carddemo);
        cardView_humidity = (CardViewNative) view.findViewById(R.id.carddemo1);
        cardView_music = (CardViewNative) view.findViewById(R.id.carddemo2);
        cardView_swing = (CardViewNative) view.findViewById(R.id.carddemo3);

                card_temperature.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        Intent intent = new Intent(getActivity(), TemperatureActivity.class);
                        startActivity(intent);
                    }
                });

        card_humidity.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), HumidityActivity.class);
                startActivity(intent);
            }
        });

        card_music.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), SoundActivity.class);
                startActivity(intent);
            }
        });

        card_swing.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getActivity(), SwingActivity.class);
                startActivity(intent);
            }
        });

        cardView_temperature.setCard(card_temperature);
        cardView_humidity.setCard(card_humidity);
        cardView_music.setCard(card_music);
        cardView_swing.setCard(card_swing);

//        list = mListener.getStateFromSP();
//        temperatureText.setText(list.get(0));
//        humidityText.setText(list.get(1));
//        palyText.setText(list.get(1) + "/" + list.get(2));
//        swingText.setText(list.get(3));

        StatusService.startActionProcessTemperature(
                getActivity(), Constants.GET, 25);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setMessageManager(MessageManager obj) {
        messageManager = obj;
        Log.d(TAG, "is in MessageManager : " + obj);
    }

    public interface OnStatusFragmentInteractionListener {

        public void geMessageFromServer(String readMessage);

        public ArrayList<String> getStateFromSP();
    }
}
