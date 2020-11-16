package eii.ulpgc.es.fragmentdices.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Random;

import eii.ulpgc.es.fragmentdices.OnePlayerGameActivity;
import eii.ulpgc.es.fragmentdices.R;

/**
 * Created by Daniel on 09/03/2017.
 */

public class DiceOnePlayerFragment extends Fragment {

    OnChangeDice mCallBack;

    private AnimationDrawable diceAnimation;
    private ImageView dice;

    private final int ANIMATION_WAIT = 3000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dice, container, false);
        dice = (ImageView) view.findViewById(R.id.dice);
        return view;
    }

    /**
     * Interfaz que tendrá que usar la actividad que aloje a este Fragmento para comunicarse con él.
     */
    public interface OnChangeDice {
        public void onChangeDice();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallBack = (OnChangeDice) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " debe implementar OnChangeDice");
        }

    }

    /**
     * Método encargado de realizar la animación del dado y asignar la tirada correspondiente
     */
    public void changeDice() {
        Random random = new Random();
        final int number = random.nextInt(6 - 1 + 1) + 1;
        dice.setBackgroundResource(R.drawable.animation);
        diceAnimation = (AnimationDrawable) dice.getBackground();
        diceAnimation.start();
        Runnable mRunnable;
        Handler mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                diceAnimation.stop();
                switch (number) {
                    case 1:
                        dice.setBackgroundResource(R.drawable.one);
                        break;
                    case 2:
                        dice.setBackgroundResource(R.drawable.two);
                        break;
                    case 3:
                        dice.setBackgroundResource(R.drawable.three);
                        break;
                    case 4:
                        dice.setBackgroundResource(R.drawable.four);
                        break;
                    case 5:
                        dice.setBackgroundResource(R.drawable.five);
                        break;
                    case 6:
                        dice.setBackgroundResource(R.drawable.six);
                        break;
                    default:
                        break;
                }
            }
        };
        mHandler.postDelayed(mRunnable, ANIMATION_WAIT);

        ((OnePlayerGameActivity) getActivity()).setAcumulatedScore(number);
    }

    /**
     * Método encargado de realizar la animación del dado y asignar la tirada correspondiente
     * para el turno de la IA
     */
    public int changeDiceMachineTurn() {
        Random random = new Random();
        final int number = random.nextInt(6 - 1 + 1) + 1;
        dice.setBackgroundResource(R.drawable.animation);
        diceAnimation = (AnimationDrawable) dice.getBackground();
        diceAnimation.start();
        Runnable mRunnable;
        Handler mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                diceAnimation.stop();
                switch (number) {
                    case 1:
                        dice.setBackgroundResource(R.drawable.one);
                        break;
                    case 2:
                        dice.setBackgroundResource(R.drawable.two);
                        break;
                    case 3:
                        dice.setBackgroundResource(R.drawable.three);
                        break;
                    case 4:
                        dice.setBackgroundResource(R.drawable.four);
                        break;
                    case 5:
                        dice.setBackgroundResource(R.drawable.five);
                        break;
                    case 6:
                        dice.setBackgroundResource(R.drawable.six);
                        break;
                    default:
                        break;
                }
            }
        };
        mHandler.postDelayed(mRunnable, ANIMATION_WAIT);

        ((OnePlayerGameActivity) getActivity()).setAcumulatedScore(number);
        return number;
    }
}
