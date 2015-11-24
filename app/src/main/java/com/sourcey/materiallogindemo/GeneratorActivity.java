package com.sourcey.materiallogindemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GeneratorActivity extends AppCompatActivity {

    private Button mGeneratorButton;
    private Button mNewQButton;
    private TextView mGenerateTextView;
    private TextView mQuestionTextView;
    private int mCurrentIndex;
    private String generation;


    private Question[] mQuestionBank = new Question[]
            {
                new Question(R.string.question1),new Question(R.string.question2),new Question(R.string.question3),
                    new Question(R.string.question4),new Question(R.string.question5),new Question(R.string.question6),
                    new Question(R.string.question7),new Question(R.string.question8)
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);



        //Set generated pass to default
        mGenerateTextView = (TextView) findViewById(R.id.generate_text);
        mGenerateTextView.setText(generation);

        //Set question text view
        mQuestionTextView = (TextView) findViewById(R.id.question_text);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);


        //If new question is clicked? show a new question
        mNewQButton = (Button) findViewById(R.id.new_question_button);
        mNewQButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                int question = mQuestionBank[mCurrentIndex].getTextResId();
                mQuestionTextView.setText(question);
            }
        });





        //If generate button is clicked generate a strong password
        mGeneratorButton = (Button) findViewById(R.id.generate_button);
        mGeneratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText passField = (EditText)findViewById(R.id.question_field);
                String generation = generate(passField);
                mGenerateTextView.setText(generation);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_generator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String generate(EditText passField)
    {
        String generation;
        String question = passField.getText().toString();
        question = changeLettersToNumbers(question);




        return generation;
    }

    public String changeLettersToNumbers(String field)
    {
        field.replaceAll("(?i)a", "4")
                .replaceAll("(?i)e", "3")
                .replaceAll("(?i)i", "1")
                .replaceAll("(?i)o", "0");

        return field;
    }
}
