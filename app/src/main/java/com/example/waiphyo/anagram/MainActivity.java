package com.example.waiphyo.anagram;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private int userOverallScore;
    private int userCurrentSore;
    private int computerOverallScore;
    private int computerCurrentScore;

    private static final String SCORE_TEXT = "Your score: <U> computer score: <C>";
    private static final String USER_SCORE_PLACEHOLDER = "<U>";
    private static final String COMPUTER_SCORE_PLACEHOLDER = "<C>";
    private static final String USER_TURN_SCORE = "your turn score: <U>";
    private static final String COMPUTER_TURN_SCORE = "computer turn score: <C>";
    private static final int[] IMAGE_IDS = {R.drawable.dice1, R.drawable.dice2, R.drawable.dice3, R.drawable.dice4, R.drawable.dice5, R.drawable.dice6};
    private static final int DEAD_SCORE = 0;
    private static final int COMPUTER_TARGET = 20;
    private static final int WINNING_SCORE = 100;
    private static final int DEFAULT_DELAY = 1500;

    private Random random;
    private Handler timerHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        random = new Random();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Update entire score board with total scores
     */
    private void updateScoreBoard() {
        TextView updateScore = (TextView) findViewById(R.id.scoreTextView);
        updateScore.setText(getTotalScoreText());
    }

    /**
     * @return new score text
     */
    private String getTotalScoreText() {
        return SCORE_TEXT
                .replace(USER_SCORE_PLACEHOLDER, Integer.toString(userOverallScore))
                .replace(COMPUTER_SCORE_PLACEHOLDER, Integer.toString(computerOverallScore));
    }

    private String getUserCurrentScoreText() {
        return USER_TURN_SCORE
                .replace(USER_SCORE_PLACEHOLDER, Integer.toString(userCurrentSore));
    }

    private String getComputerCurrentScoreText() {
        return COMPUTER_TURN_SCORE
                .replace(COMPUTER_SCORE_PLACEHOLDER, Integer.toString(computerCurrentScore));
    }

    private void displayUserCurrentScore() {
        TextView updateScore = (TextView) findViewById(R.id.scoreTextView);
        updateScore.setText(getTotalScoreText() + "   " + getUserCurrentScoreText());
    }

    private void displayComputerCurrentScore() {
        TextView updateScore = (TextView) findViewById(R.id.scoreTextView);
        updateScore.setText(getTotalScoreText() + "   " + getComputerCurrentScoreText());
    }

    /**
     * TODO: update deprecated API
     * run random integer
     * update image view based on result.
     *
     * @return current dice value
     */
    private int rollDice() {
        int currentScore = random.nextInt(6);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageDrawable(getResources().getDrawable(IMAGE_IDS[currentScore]));
        return currentScore;
    }

    /**
     * User rolling dice event
     * Try to roll the dice
     * if the result is 1, update total scoreboard & switch to computer turn
     * if not, update current total score
     * @param view Unused
     */
    protected void rollClick(View view) {
        int currentScore = rollDice();
        if (currentScore == DEAD_SCORE) {
            userCurrentSore = 0;
            updateScoreBoard();
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    computerTurn(false);
                }
            }, DEFAULT_DELAY);
        } else {
            userCurrentSore += currentScore + 1;
            displayUserCurrentScore();
        }
    }

    /**
     * if computer previous did not roll 1 and current value is less than 20, roll again.
     * if not, update the scoreboard, check the winner, and give it back to user.
     *
     * @param isDeadEnd flag: did computer roll "1"
     */
    private void computerTurn(boolean isDeadEnd) {
        if (!isDeadEnd && computerCurrentScore < COMPUTER_TARGET) {
            computerLogic();
        } else {
            computerOverallScore += computerCurrentScore;
            computerCurrentScore = 0;
            updateScoreBoard();
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    checkWinner(false);
                }
            }, DEFAULT_DELAY);
        }
    }

    /**
     * diable buttons
     * roll the dice.
     * if result is "1", reset.
     * if not, update current turn.
     */
    private void computerLogic() {
        toggleButtons(false);
        int currentScore = rollDice();
        if (currentScore == DEAD_SCORE) {
            computerCurrentScore = 0;
            updateScoreBoard();
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    computerTurn(true);
                }
            }, DEFAULT_DELAY);
        } else {
            computerCurrentScore += currentScore + 1;
            displayComputerCurrentScore();
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    computerTurn(false);
                }
            }, DEFAULT_DELAY);
        }
        toggleButtons(true);
    }

    /**
     * User stop rolling.
     * update user score,
     * check the winner.
     * @param view
     */
    protected void holdClick(View view) {
        userOverallScore += userCurrentSore;
        userCurrentSore = 0;
        updateScoreBoard();
        timerHandler.postDelayed(new Runnable() {
            public void run() {
                checkWinner(true);
            }
        }, DEFAULT_DELAY);
    }

    /**
     * disable or enable 3 buttons
     * @param enabled flag
     */
    private void toggleButtons(boolean enabled) {
        Button roll = (Button) findViewById(R.id.rollButton);
        Button hold = (Button) findViewById(R.id.holdButton);
        roll.setEnabled(enabled);
        hold.setEnabled(enabled);
    }

    /**
     * check if anyone wins.
     * if someone wins, reset the game, and disable if it is computer's turn.
     * if not, if it is computer's turn, run the computer's turn.
     * @param isComputerTurn
     */
    private void checkWinner(boolean isComputerTurn) {
        TextView scoreBoard = (TextView) findViewById(R.id.scoreTextView);
        if (userOverallScore >=  WINNING_SCORE) {
            scoreBoard.setText("User Wins");
            isComputerTurn = false;
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    resetClick(null);
                }
            }, DEFAULT_DELAY);
        } else if (computerOverallScore >= WINNING_SCORE) {
            scoreBoard.setText("Computer Wins");
            isComputerTurn = false;
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    resetClick(null);
                }
            }, DEFAULT_DELAY);
        }

        if (userOverallScore < WINNING_SCORE && computerCurrentScore < WINNING_SCORE && isComputerTurn) {
            timerHandler.postDelayed(new Runnable() {
                public void run() {
                    computerTurn(false);
                }
            }, DEFAULT_DELAY);
        }
    }

    /**
     * reset all score values to 0
     * @param view Unused
     */
    protected void resetClick(View view) {
        userOverallScore = 0;
        userCurrentSore = 0;
        computerCurrentScore = 0;
        computerOverallScore = 0;
        updateScoreBoard();
    }
}
