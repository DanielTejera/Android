package eii.ulpgc.es.fragmentdices.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import eii.ulpgc.es.fragmentdices.OnePlayerGameActivity;
import eii.ulpgc.es.fragmentdices.R;

/**
 * Created by Daniel on 13/03/2017.
 */

public class PlayersOnePlayerFragment extends Fragment {

    public TextView scoreP1;
    public TextView scoreMachine;

    public ProgressBar progressP1;
    public ProgressBar progressMachine;

    private int scoreToAdd = 0;
    public static int SCORE_TO_WIN = 20;

    OnSetScore mCallBack;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_players_one_player, container, false);

        scoreP1 = (TextView) view.findViewById(R.id.scorePlayer1);
        scoreMachine = (TextView) view.findViewById(R.id.scoreMachine);

        progressP1 = (ProgressBar) view.findViewById(R.id.progressBarPlayer1);
        progressMachine = (ProgressBar) view.findViewById(R.id.progressBarMachine);

        progressP1.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        progressMachine.setProgressTintList(ColorStateList.valueOf(Color.GREEN));

        setRetainInstance(true);

        return view;
    }

    /**
     * Interfaz que tendrá que usar la actividad que aloje a este Fragmento para comunicarse con él,
     * con el objetivo de actualizar los marcadores que maneja este Fragmento.
     */
    public interface OnSetScore {
        public void onSetScore();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (OnSetScore) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnChangeDice");
        }

    }

    /**
     * Método que actualiza los marcadores y las barras de progreso de los jugadores. Además,
     * controla la condición de victoria.
     * @param score Puntuación a añadir.
     * @param player Jugador al que hay que añadir la puntuación.
     */
    public void setScore(int score, int player) {
        if (player == 1) {
            scoreToAdd = Integer.valueOf((String) scoreP1.getText()) + score;
            scoreP1.setText(String.valueOf(scoreToAdd));
            progressP1.setProgress(scoreToAdd);

            if (scoreToAdd >= SCORE_TO_WIN) {
                ((OnePlayerGameActivity) getActivity()).win(player);
            }
        } else {
            scoreToAdd = Integer.valueOf((String) scoreMachine.getText()) + score;
            scoreMachine.setText(String.valueOf(scoreToAdd));
            progressMachine.setProgress(scoreToAdd);

            if (scoreToAdd >= SCORE_TO_WIN) {
                ((OnePlayerGameActivity) getActivity()).win(player);
            }
        }
    }
}
