package eii.ulpgc.es.fragmentdices.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eii.ulpgc.es.fragmentdices.R;
import eii.ulpgc.es.fragmentdices.TwoPlayersGameActivity;

/**
 * Created by Daniel on 18/03/2017.
 */

public class PanelTwoPlayersFragment extends Fragment implements View.OnClickListener{

    private Button rollDice;
    private Button takeScore;
    private Button startTurn;

    public TextView acumulatedScore;
    public TextView playerName;

    public int scoreToAdd = 0;
    OnSetDiceRoll diceRollCallBack;
    OnTakeScore takeScoreCallBack;
    OnStartTurn startTurnCallBack;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panel_two_players, container, false);

        rollDice = (Button) view.findViewById(R.id.rollDice);
        takeScore = (Button) view.findViewById(R.id.takeScore);
        startTurn = (Button) view.findViewById(R.id.startTurn);

        acumulatedScore = (TextView) view.findViewById(R.id.acumulatedScore);
        playerName = (TextView) view.findViewById(R.id.playerName);

        rollDice.setOnClickListener(this);
        takeScore.setOnClickListener(this);
        startTurn.setOnClickListener(this);


        return view;
    }

    /**
     * Interfaz que tendrá que usar la actividad que aloje a este Fragmento para comunicarse con él,
     * para poder realizar las acciones correspondientes a tirar el dado cuando el usuario pulse el
     * botón designado para ello.
     */
    public interface OnSetDiceRoll {
        public void doDiceRoll();
    }

    /**
     * Interfaz que tendrá que usar la actividad que aloje a este Fragmento para comunicarse con él,
     * para poder realizar las acciones correspondientes a recoger la puntuación acumulada en el
     * turno cuando el usuario pulse el botón designado para ello.
     */
    public interface OnTakeScore {
        public void doTakeScore();
    }

    /**
     * Interfaz que tendrá que usar la actividad que aloje a este Fragmento para comunicarse con él,
     * para poder realizar las acciones correspondientes cuando el usuario pulse el botón asignado
     * para comenzar el turno.
     */
    public interface OnStartTurn {
        public void doStartTurn();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            diceRollCallBack = (OnSetDiceRoll) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar SetDiceRoll");
        }

        try {
            takeScoreCallBack = (OnTakeScore) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar takeScore()");
        }

        try {
            startTurnCallBack = (OnStartTurn) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar doStartTurn()");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rollDice:
                diceRollCallBack.doDiceRoll();
                break;
            case R.id.takeScore:
                takeScoreCallBack.doTakeScore();
                break;
            case R.id.startTurn:
                startTurnCallBack.doStartTurn();
                break;
            default:
                break;
        }
    }

    /**
     * Método encargado de sumar la puntuación obtenida al tirar el dado a la puntuación acumulada
     * en el turno. Controla además si el número obtenido en el dado es un 1, actuando en
     * consecuencia.
     */
    public void setDiceRoll(int number) {
        if (number == 1) {
            scoreToAdd = 0;
            ((TwoPlayersGameActivity) getActivity()).changeTurn();
        } else {
            scoreToAdd = scoreToAdd + number;
            acumulatedScore.setText(getString(R.string.acumulatedScoreString) + " " + String.valueOf(scoreToAdd));
            takeScore.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Método encargado de preparar un nuevo turno.
     * @param player Jugador para el que hay que preparar el turno
     */
    public void prepareNewTurn(int player) {
        startTurn.setVisibility(View.VISIBLE);

        rollDice.setVisibility(View.INVISIBLE);
        takeScore.setVisibility(View.INVISIBLE);
        playerName.setVisibility(View.INVISIBLE);
        acumulatedScore.setVisibility(View.INVISIBLE);

        scoreToAdd = 0;

        acumulatedScore.setText(getString(R.string.acumulatedScoreString) + " 0");

        if (player == 1) {
            startTurn.setText(R.string.startP1);
            playerName.setText(R.string.player1);
        } else {
            startTurn.setText(R.string.startP2);
            playerName.setText(R.string.player2);
        }
    }

    /**
     * Método encargado de preparar la interfaz cuando el turno ha sido iniciado.
     */
    public void startTurn() {
        startTurn.setVisibility(View.INVISIBLE);

        rollDice.setVisibility(View.VISIBLE);
        playerName.setVisibility(View.VISIBLE);
        acumulatedScore.setVisibility(View.VISIBLE);
    }
}
