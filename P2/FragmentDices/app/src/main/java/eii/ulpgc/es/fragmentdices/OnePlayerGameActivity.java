package eii.ulpgc.es.fragmentdices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.Random;

import eii.ulpgc.es.fragmentdices.fragments.DiceOnePlayerFragment;
import eii.ulpgc.es.fragmentdices.fragments.PanelOnePlayerFragment;
import eii.ulpgc.es.fragmentdices.fragments.PlayersOnePlayerFragment;

public class OnePlayerGameActivity extends AppCompatActivity
        implements PanelOnePlayerFragment.OnSetDiceRoll, PanelOnePlayerFragment.OnTakeScore,
        PanelOnePlayerFragment.OnStartTurn, PlayersOnePlayerFragment.OnSetScore, DiceOnePlayerFragment.OnChangeDice {

    private int currentPlayer = 1;
    private int score = 0;
    private int puntuation = 0;

    private PanelOnePlayerFragment panelOnePlayerFragment;
    private PlayersOnePlayerFragment playersOnePlayerFragment;
    private DiceOnePlayerFragment diceOnePlayerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_player_game);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        panelOnePlayerFragment = (PanelOnePlayerFragment)
                getFragmentManager().findFragmentById(R.id.panelOPFragment);

        playersOnePlayerFragment = (PlayersOnePlayerFragment)
                getFragmentManager().findFragmentById(R.id.playersOPFragment);

        diceOnePlayerFragment = (DiceOnePlayerFragment)
                getFragmentManager().findFragmentById(R.id.diceFragment);

        panelOnePlayerFragment.prepareNewTurn(currentPlayer);

    }


    /**
     * Método que se encarga de la construcción de la alerta cuando el jugador o la IA gana.
     * Sí el usuario elige volver a jugar, se inicia una nueva partida, empezando el jugador
     * que ha perdido.
     * Si el jugador elige no volver a jugar, se le devuelve al menú inicial de la aplicación.
     *
     * @param player Jugador que ha ganado.
     */
    public void win(int player) {
        if (player == 1) {
            new AlertDialog.Builder(OnePlayerGameActivity.this)
                    .setTitle(R.string.win1)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            currentPlayer = 2;
                            resetGame();
                            panelOnePlayerFragment.prepareNewTurn(currentPlayer);
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            Intent menuIntent = new Intent().setClass(
                                    OnePlayerGameActivity.this, PlayerMenu.class);
                            startActivity(menuIntent);
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(OnePlayerGameActivity.this)
                    .setTitle(R.string.winMachine)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            currentPlayer = 1;
                            resetGame();
                            panelOnePlayerFragment.prepareNewTurn(currentPlayer);
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            Intent menuIntent = new Intent().setClass(
                                    OnePlayerGameActivity.this, PlayerMenu.class);
                            startActivity(menuIntent);
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();

        }
    }

    /**
     * Método que se encarga de reiniciar los marcadores y las barras de progreso.
     */
    private void resetGame() {
        playersOnePlayerFragment.scoreP1.setText("0");
        playersOnePlayerFragment.scoreMachine.setText("0");

        playersOnePlayerFragment.progressP1.setProgress(0);
        playersOnePlayerFragment.progressMachine.setProgress(0);
    }

    /**
     * Método que se encarga de cambiar el turno.
     */
    public void changeTurn() {
        puntuation = 0;
        if (currentPlayer == 1) {
            currentPlayer = 2;
            panelOnePlayerFragment.prepareNewTurn(currentPlayer);
        } else {
            currentPlayer = 1;
            panelOnePlayerFragment.prepareNewTurn(currentPlayer);
        }
    }


    @Override
    public void doDiceRoll() {
        diceOnePlayerFragment.changeDice();
    }

    @Override
    public void doStartTurn() {
        if (currentPlayer == 1) {
            panelOnePlayerFragment.startTurn();
        } else {
            startTurnMachine();
        }
    }

    @Override
    public void doTakeScore() {
        playersOnePlayerFragment.setScore(panelOnePlayerFragment.scoreToAdd, currentPlayer);
        changeTurn();
    }

    @Override
    public void onSetScore() {

    }

    @Override
    public void onChangeDice() {

    }

    /**
     * Método que se encarga de pedirle al Fragmento que actualice la puntuación acumulada en el
     * turno.
     *
     * @param number Puntuación a sumar.
     */
    public void setAcumulatedScore(int number) {
        panelOnePlayerFragment.setDiceRoll(number);

        puntuation = puntuation + number;
        int partialScore = puntuation + Integer.parseInt(playersOnePlayerFragment.scoreP1.getText().toString());
        if (partialScore >= PlayersOnePlayerFragment.SCORE_TO_WIN){
            win(currentPlayer);
        }
    }

    /**
     * Método que se encarga de empezar el turno para la IA.
     */
    public void startTurnMachine() {
        panelOnePlayerFragment.startTurn();
        makeMachineTurn();
    }

    /**
     * Método que se encarga de realizar el turno de la IA.
     */
    public void makeMachineTurn() {
        boolean one = true;

        Random random = new Random();
        int rolls = random.nextInt(10 - 1 + 1) + 1;
        score = 0;
        for (int i = 0; i <= rolls; i++) {
            int number = diceOnePlayerFragment.changeDiceMachineTurn();
            if (number == 1) {
                one = false;
                break;
            }
            score += number;
        }
        if (one) {
            playersOnePlayerFragment.setScore(panelOnePlayerFragment.scoreToAdd,
                    currentPlayer);
            changeTurn();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("scoreP1",
                (String) playersOnePlayerFragment.scoreP1.getText());
        savedInstanceState.putInt("progressP1", playersOnePlayerFragment.progressP1.getProgress());
        savedInstanceState.putString("scoreAI",
                (String) playersOnePlayerFragment.scoreMachine.getText());
        savedInstanceState.putInt("progressAI",
                playersOnePlayerFragment.progressMachine.getProgress());

        savedInstanceState.putInt("currentPlayer", currentPlayer);

        savedInstanceState.putString("playerName",
                (String) panelOnePlayerFragment.playerName.getText());
        savedInstanceState.putString("acumulatedScore",
                (String) panelOnePlayerFragment.acumulatedScore.getText());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        playersOnePlayerFragment.scoreP1.setText(savedInstanceState.getString("scoreP1"));
        playersOnePlayerFragment.progressP1.setProgress(savedInstanceState.getInt("progressP1"));
        playersOnePlayerFragment.scoreMachine.setText(savedInstanceState.getString("scoreAI"));
        playersOnePlayerFragment.progressMachine.setProgress(savedInstanceState.getInt("progressAI"));

        currentPlayer = savedInstanceState.getInt("currentPlayer");

        panelOnePlayerFragment.playerName.setText(savedInstanceState.getString("playerName"));
        panelOnePlayerFragment.acumulatedScore.setText(savedInstanceState.getString("acumulatedScore"));
    }

    @Override
    public void onBackPressed() {
    }
}
