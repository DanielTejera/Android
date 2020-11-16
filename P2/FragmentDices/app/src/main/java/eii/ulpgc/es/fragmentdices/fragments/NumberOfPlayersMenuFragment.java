package eii.ulpgc.es.fragmentdices.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eii.ulpgc.es.fragmentdices.OnePlayerGameActivity;
import eii.ulpgc.es.fragmentdices.R;
import eii.ulpgc.es.fragmentdices.TwoPlayersGameActivity;

/**
 * Created by Daniel on 09/03/2017.
 */

public class NumberOfPlayersMenuFragment extends Fragment implements View.OnClickListener{

    private Button onePlayer;
    private Button twoPlayers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_of_players_menu, container, false);

        onePlayer = (Button) view.findViewById(R.id.one_player);
        twoPlayers = (Button) view.findViewById(R.id.two_players);

        onePlayer.setOnClickListener(this);
        twoPlayers.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one_player:
                Intent onePlayerIntent = new Intent(getActivity(), OnePlayerGameActivity.class);
                startActivity(onePlayerIntent);
                break;

            case R.id.two_players:
                Intent twoPlayersIntent = new Intent(getActivity(), TwoPlayersGameActivity.class);
                startActivity(twoPlayersIntent);
                break;
        }
    }
}
