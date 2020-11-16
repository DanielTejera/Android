package eii.ulpgc.es.fragmentdices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import eii.ulpgc.es.fragmentdices.fragments.DiceTwoPlayersFragment;
import eii.ulpgc.es.fragmentdices.fragments.PanelTwoPlayersFragment;
import eii.ulpgc.es.fragmentdices.fragments.PlayersTwoPlayersFragment;

public class TwoPlayersGameActivity extends AppCompatActivity implements
        PanelTwoPlayersFragment.OnSetDiceRoll, PanelTwoPlayersFragment.OnTakeScore,
        PanelTwoPlayersFragment.OnStartTurn, PlayersTwoPlayersFragment.OnSetScore,
        DiceTwoPlayersFragment.OnChangeDice {

    private PanelTwoPlayersFragment panelTwoPlayersFragment;
    private PlayersTwoPlayersFragment playersTwoPlayersFragment;
    private DiceTwoPlayersFragment diceTwoPlayersFragment;

    private int currentPlayer = 1;
    private int puntuation = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_players_game);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        panelTwoPlayersFragment = (PanelTwoPlayersFragment)
                getFragmentManager().findFragmentById(R.id.panelTPFragment);

        playersTwoPlayersFragment = (PlayersTwoPlayersFragment)
                getFragmentManager().findFragmentById(R.id.playersTPFragment);

        diceTwoPlayersFragment = (DiceTwoPlayersFragment)
                getFragmentManager().findFragmentById(R.id.diceTPFragment);

        panelTwoPlayersFragment.prepareNewTurn(currentPlayer);
    }

    /**
     * Método que se encarga de la construcción de la alerta cuando uno de los dos jugadores gana.
     * Sí el usuario elige volver a jugar, se inicia una nueva partida, empezando el jugador
     * que ha perdido.
     * Si el jugador elige no volver a jugar, se le devuelve al menú inicial de la aplicación.
     *
     * @param player Jugador que ha ganado.
     */
    public void win(int player) {
        if (player == 1) {
            new AlertDialog.Builder(TwoPlayersGameActivity.this)
                    .setTitle(R.string.win1)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            currentPlayer = 2;
                            resetGame();
                            panelTwoPlayersFragment.prepareNewTurn(currentPlayer);
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            Intent menuIntent = new Intent().setClass(
                                    TwoPlayersGameActivity.this, PlayerMenu.class);
                            startActivity(menuIntent);
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        } else {
            new AlertDialog.Builder(TwoPlayersGameActivity.this)
                    .setTitle(R.string.win2)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            currentPlayer = 2;
                            resetGame();
                            panelTwoPlayersFragment.prepareNewTurn(currentPlayer);
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int wich) {
                            Intent menuIntent = new Intent().setClass(
                                    TwoPlayersGameActivity.this, PlayerMenu.class);
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

        playersTwoPlayersFragment.scoreP1.setText("0");
        playersTwoPlayersFragment.scoreP2.setText("0");

        playersTwoPlayersFragment.progressP1.setProgress(0);
        playersTwoPlayersFragment.progressP2.setProgress(0);
    }

    /**
     * Método que se encarga de cambiar el turno.
     */
    public void changeTurn() {
        puntuation = 0;

        if (currentPlayer == 1) {
            currentPlayer = 2;
            panelTwoPlayersFragment.prepareNewTurn(currentPlayer);
        } else {
            currentPlayer = 1;
            panelTwoPlayersFragment.prepareNewTurn(currentPlayer);
        }
    }

    @Override
    public void doDiceRoll() {
        diceTwoPlayersFragment.changeDice();
    }

    @Override
    public void doStartTurn() {
        panelTwoPlayersFragment.startTurn();
    }

    @Override
    public void doTakeScore() {
        playersTwoPlayersFragment.setScore(panelTwoPlayersFragment.scoreToAdd, currentPlayer);
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
        panelTwoPlayersFragment.setDiceRoll(number);

        puntuation = puntuation + number;
        int partialScore = puntuation + Integer.parseInt(playersTwoPlayersFragment.scoreP1.getText().toString());
        if (partialScore >= PlayersTwoPlayersFragment.SCORE_TO_WIN){
            win(currentPlayer);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("scoreP1",
                (String) playersTwoPlayersFragment.scoreP1.getText());
        savedInstanceState.putInt("progressP1", playersTwoPlayersFragment.progressP1.getProgress());
        savedInstanceState.putString("scoreP2",
                (String) playersTwoPlayersFragment.scoreP2.getText());
        savedInstanceState.putInt("progressP2",
                playersTwoPlayersFragment.progressP2.getProgress());

        savedInstanceState.putInt("currentPlayer", currentPlayer);

        savedInstanceState.putString("playerName",
                (String) panelTwoPlayersFragment.playerName.getText());
        savedInstanceState.putString("acumulatedScore",
                (String) panelTwoPlayersFragment.acumulatedScore.getText());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        playersTwoPlayersFragment.scoreP1.setText(savedInstanceState.getString("scoreP1"));
        playersTwoPlayersFragment.progressP1.setProgress(savedInstanceState.getInt("progressP1"));
        playersTwoPlayersFragment.scoreP2.setText(savedInstanceState.getString("scoreP2"));
        playersTwoPlayersFragment.progressP2.setProgress(savedInstanceState.getInt("progressP2"));

        currentPlayer = savedInstanceState.getInt("currentPlayer");

        panelTwoPlayersFragment.playerName.setText(savedInstanceState.getString("playerName"));
        panelTwoPlayersFragment.acumulatedScore.setText(savedInstanceState.getString("acumulatedScore"));
    }

    @Override
    public void onBackPressed() {
    }
}
