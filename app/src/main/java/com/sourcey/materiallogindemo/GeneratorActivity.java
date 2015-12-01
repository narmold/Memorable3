package com.sourcey.materiallogindemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

public class GeneratorActivity extends AppCompatActivity {

    private Button mGeneratorButton;
    private Button mNewQButton;
    private Button mAddBankButton;
    private TextView mGenerateTextView;
    private TextView mQuestionTextView;
    private int mCurrentIndex;
    private String generation;
    private String generation2;
    private GeneratorActivity reference;


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

        reference = this;

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

                EditText questionField = (EditText) findViewById(R.id.question_field);
                questionField.setText("");

            }
        });


        mAddBankButton = (Button) findViewById(R.id.add_to_bank_button);
        mAddBankButton.setEnabled(false);


        //If generate button is clicked generate a strong password
        mGeneratorButton = (Button) findViewById(R.id.generate_button);
        mGeneratorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddBankButton.setEnabled(true);
                EditText passField = (EditText) findViewById(R.id.question_field);
                generation2 = generate(passField);
                mGenerateTextView.setText(generation2);

            }
        });



        mAddBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("password", generation2);

                Intent intent = new Intent(reference, NewEntry.class);
                intent.putExtras(b);
                startActivity(intent);

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
        question = randomizeCase(question);

        generation = question;

        return generation;
    }

    public static String changeLettersToNumbers(String field)
    {
        field = field.replace('a','4');
        field = field.replace('e','3');
        field = field.replace('i','1');
        field = field.replace('o','0');
        field = field.replace('b','8');
        field = field.replace('g','9');
        field = field.replace('s','5');

        return field;
    }

    public static String randomizeCase(String str) {

        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(str.length());

        for (char c : str.toCharArray())
            sb.append(rnd.nextBoolean()
                    ? Character.toLowerCase(c)
                    : Character.toUpperCase(c));

        return sb.toString();
    }
}
