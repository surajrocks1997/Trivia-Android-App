package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.controller.AppController;
import com.example.trivia.data.AnswerListAsyncResponse;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;
import com.example.trivia.model.Score;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private TextView counterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton prevButton;
    private ImageButton nextButton;
    private TextView score_textView;

    private int scoreCounter = 0;
    private Score score;

    private int currentQuestionIndex = 0;

    private List<Question> questionList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();

        score_textView = findViewById(R.id.score_text);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        counterTextView = findViewById(R.id.counter_text);
        questionTextView = findViewById(R.id.question_textview);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        score_textView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                counterTextView.setText(currentQuestionIndex + " / " + questionArrayList.size());
                Log.d("Inside", "processFinished: " + questionArrayList);
            }
        });
        Log.d("Main", "onCreate: " + questionList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prev_button:
                if(currentQuestionIndex > 0){
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_button:
                currentQuestionIndex = (currentQuestionIndex + 1 ) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void checkAnswer(boolean userChoice) {
        boolean answerIsTrue = questionList.get(currentQuestionIndex).isAnswerTrue();
        int toastMessageID = 0;
        if(userChoice == answerIsTrue){
            fadeView();
            addPoints();
            toastMessageID = R.string.correct_answer;
        }
        else{
            shakeAnimation();
            deductPoints();
            toastMessageID = R.string.wrong_answer;
        }

        Toast.makeText(MainActivity.this, toastMessageID, Toast.LENGTH_SHORT).show();
    }

    private void addPoints(){
        scoreCounter += 100;
        score.setScore(scoreCounter);
        score_textView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));

        Log.d("Score", "addPoints: " + score.getScore());
    }

    private void deductPoints(){
        scoreCounter -= 100;
        if (scoreCounter > 0){
            score.setScore(scoreCounter);
            score_textView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
        }
        else{
            scoreCounter = 0;
            score.setScore(scoreCounter);
            score_textView.setText(MessageFormat.format("Current Score: {0}", String.valueOf(score.getScore())));
            Log.d("Score Bad", "deductPoints: " + score.getScore());
        }

        Log.d("Score", "addPoints: " + score.getScore());
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        counterTextView.setText(currentQuestionIndex + " / " + questionList.size());
        questionTextView.setText(question);
    }

    private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation(){
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_animation);
        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);

        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
