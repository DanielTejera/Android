package eii.ulpgc.es.dices;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int score, currentTurn;
    private ProgressBar progressP1, progressP2;
    private TextView scoreP1, scoreP2, playerName, acumulatedScore;
    private ImageView dice;
    private Button rollButton, takeButton, startButton;

    private static int MAXPUNTUATION = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        score = 0;
        currentTurn = 1;

        progressP1 = (ProgressBar) findViewById(R.id.progressBarPlayer1);
        progressP2 = (ProgressBar) findViewById(R.id.progressBarPlayer2);

        scoreP1 = (TextView) findViewById(R.id.scorePlayer1);
        scoreP2 = (TextView) findViewById(R.id.scorePlayer2);
        playerName = (TextView) findViewById(R.id.playerName);
        acumulatedScore = (TextView) findViewById(R.id.acumulatedScore);

        dice = (ImageView) findViewById(R.id.diceImage);

        rollButton = (Button) findViewById(R.id.rollDice);
        takeButton = (Button) findViewById(R.id.takeScore);
        startButton = (Button) findViewById(R.id.startTurn);


        prepareGame();
    }

    /**
    * Método encargado de preparar el juego para que comience
    */
    public void prepareGame(){
        startButton.setVisibility(View.VISIBLE);

        dice.setVisibility(View.INVISIBLE);
        rollButton.setVisibility(View.INVISIBLE);
        takeButton.setVisibility(View.INVISIBLE);
        playerName.setVisibility(View.INVISIBLE);

        acumulatedScore.setText(String.valueOf(score));

        if (currentTurn == 1){
            startButton.setText(R.string.startP1);
            playerName.setText(R.string.player1);
        }else{
            startButton.setText(R.string.startP2);
            playerName.setText(R.string.player2);
        }

    }

    /**
    * Método encargado de empezar el turno de juego, cuando el usuario pulsa el botón
    * @param view Botón
    */
    public void startTurn(View view){
        startButton.setVisibility(View.INVISIBLE);
        rollButton.setVisibility(View.VISIBLE);
        playerName.setVisibility(View.VISIBLE);
    }

    /**
     * Método encargado de cambiar los turnos entre jugadores
     */
    public void changeTurn(){
        playerName.setVisibility(View.INVISIBLE);
        rollButton.setVisibility(View.INVISIBLE);
        takeButton.setVisibility(View.INVISIBLE);

        if (currentTurn == 1){
            currentTurn = 2;
            playerName.setText(R.string.player2);
            startButton.setText(R.string.startP2);
        }else{
            currentTurn = 1;
            playerName.setText(R.string.player1);
            startButton.setText(R.string.startP1);
        }

        startButton.setVisibility(View.VISIBLE);
    }

    /**
     * Método que se encarga de reiniciar los contadores del juego
     * cuando el usuario quiere realizar una nueva partida
     */
    public void resetGame(){
        scoreP1.setText(R.string.initialScore);
        scoreP2.setText(R.string.initialScore);

        score = 0;
        if(currentTurn == 1){
            currentTurn = 2;
        }else{
            currentTurn = 1;
        }

        acumulatedScore.setText(R.string.initialScore);

        progressP1.setProgress(0);
        progressP2.setProgress(0);

        prepareGame();
    }

    /**
     * Método que lanza el dialogo de fin de partida
     * @param player Jugador que ha ganado
     */
    public void win(int player){
        if (player == 1){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.win1)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            resetGame();
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else{
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.win2)
                    .setMessage(R.string.playAgain)
                    .setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            resetGame();
                        }
                    })
                    .setNegativeButton(R.string.negativeButton, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int wich){
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    /**
     * Método que ejecuta la tirada del dado y acumula la puntuación.
     * Si la tirada es uno, cambia de turno
     * @param view Botón
     */
    public void rollDice(View view){
        dice.setVisibility(View.VISIBLE);
        takeButton.setVisibility(View.VISIBLE);
        playerName.setVisibility(View.VISIBLE);

        Random random = new Random();
        int number = random.nextInt(6 - 1 + 1) + 1;
        switch (number){
            case 1:
                dice.setImageResource(R.drawable.one);
                break;
            case 2:
                dice.setImageResource(R.drawable.two);
                break;
            case 3:
                dice.setImageResource(R.drawable.three);
                break;
            case 4:
                dice.setImageResource(R.drawable.four);
                break;
            case 5:
                dice.setImageResource(R.drawable.five);
                break;
            case 6:
                dice.setImageResource(R.drawable.six);
                break;
            default:
                break;
        }

        if (number != 1){
            score = score + number;
        }else{
            score = 0;
            changeTurn();
        }

        acumulatedScore.setText(String.valueOf(score));

        int partialScore = Integer.parseInt(acumulatedScore.getText().toString()) +
                Integer.parseInt(scoreP1.getText().toString());

        if (partialScore >= MAXPUNTUATION){
            win(currentTurn);
        }
    }


    /**
     * Método que suma a la puntuación global del jugador la puntuación acumulada en el turno.
     * Controla además la condición de victoria
     * @param view Botón
     */
    public void takeScore(View view){
        int playerScore;
        if(currentTurn == 1){
            playerScore = Integer.valueOf((String) scoreP1.getText());
            playerScore = playerScore + score;
            scoreP1.setText(String.valueOf(playerScore));
            score = 0;
            acumulatedScore.setText(R.string.initialScore);
            progressP1.setProgress(playerScore);

            if (progressP1.getProgress() >= MAXPUNTUATION){
                win(currentTurn);
            }else{
                changeTurn();
            }

        }else{
            playerScore = Integer.valueOf((String) scoreP2.getText());
            playerScore = playerScore + score;
            scoreP2.setText(String.valueOf(playerScore));
            score = 0;
            acumulatedScore.setText(R.string.initialScore);
            progressP2.setProgress(playerScore);

            if (progressP2.getProgress() >= MAXPUNTUATION){
                win(currentTurn);
            }else{
                changeTurn();
            }
        }
    }
}
