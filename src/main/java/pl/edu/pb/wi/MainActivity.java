package pl.edu.pb.wi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_INDEX = "currentIndex";
    private static final String ANSWERS_BITMAP = "answers";
    public static final String KEY_EXTRA_ANSWER = "pl.edu.pb.wi.Quiz.correctAnswer";
    private static final int REQUEST_CODE_PROMPT = 0;

    private TextView questionTextView;
    private Button trueButton;
    private Button falseButton;
    private Button nextButton;
    private Button promptButton;

    private Question[] questions = new Question[]{
            new Question(R.string.q_activity, true),
            new Question(R.string.q_find_resources, false),
            new Question(R.string.q_listener, true),
            new Question(R.string.q_resources, true),
            new Question(R.string.q_version, false)
    };

    private boolean answers[];

    private int currentIndex = 0;
    private boolean answerWasShown = false;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("myDebug", "Called onStart");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)return;
        if(requestCode == REQUEST_CODE_PROMPT)
        {
            if(data == null)return;
            answerWasShown = data.getBooleanExtra(PromptActivity.KEY_EXTRA_ANSWER_SHOWN, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("myDebug", "Called onSaveInstanceState");
        outState.putInt(KEY_CURRENT_INDEX, currentIndex);
        int bitmap = 0;
        int bits = 1;
        for(int i=0; i<questions.length; i++)
        {
            bitmap += bits * (answers[i] ? 1 : 0);
            bits *= 2;
        }
        outState.putInt(ANSWERS_BITMAP, bitmap);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("myDebug", "Called onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("myDebug", "Called onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("myDebug", "Called onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("myDebug", "Called onStart");
    }

    private int correctAnswers = 0;

    private void checkAnswerCorrectness(boolean userAnswer)
    {
        boolean correctAnswer = questions[currentIndex].isTrueAnswer();
        int resultMessageId = 0;
        if(answerWasShown)
        {
            resultMessageId = R.string.answer_was_shown;
        }
        else if(correctAnswer == userAnswer)
        {
            resultMessageId = R.string.corrcet_answer;
            {
            if(!answers[currentIndex])
                correctAnswers++;
                answers[currentIndex] = true;
            }
        }
        else
        {
            resultMessageId = R.string.incorrcet_answer;
            if(answers[currentIndex])
            {
                correctAnswers--;
                answers[currentIndex] = false;
            }
        }
        if(currentIndex != questions.length - 1)
        {
            Toast.makeText(this, resultMessageId, Toast.LENGTH_SHORT).show();
        }
        else
        {
            String finalResultMessage = getString(R.string.final_result_message, correctAnswers, questions.length);
            Toast.makeText(this, finalResultMessage, Toast.LENGTH_LONG).show();
        }
    }

    private void setNextQuestion()
    {
        questionTextView.setText(questions[currentIndex].getQuestionId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.question_text_view);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        promptButton = findViewById(R.id.hint_button);

        answers = new boolean[questions.length];

        if(savedInstanceState != null)
        {
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
            int bitmap = savedInstanceState.getInt(ANSWERS_BITMAP);
            int bits = 1;
            correctAnswers = 0;
            for(int i=0; i<questions.length; i++)
            {
                if((bitmap & bits) != 0)
                {
                    answers[i] = true;
                    correctAnswers++;
                }
                else
                {
                    answers[i] = false;
                }
                bits *= 2;
            }
        }

        trueButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswerCorrectness(true);
                    }
                }
        );

        falseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkAnswerCorrectness(false);
                    }
                }
        );

        nextButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentIndex = (currentIndex + 1) % questions.length;
                        answerWasShown = false;
                        setNextQuestion();
                    }
                }
        );

        promptButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, PromptActivity.class);
            boolean correctAnswer = questions[currentIndex].isTrueAnswer();
            intent.putExtra(KEY_EXTRA_ANSWER, correctAnswer);
            startActivityForResult(intent, REQUEST_CODE_PROMPT);
        });

        setNextQuestion();

    }
}