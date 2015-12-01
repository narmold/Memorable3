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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GeneratorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String[] SEPARATORS = new String[]
            {
                    "@", "_", ".", "_", " "
            };

    private Button mGeneratorButton;
    private Button mNewQButton;
    private Button mSavePasswordButton;
    private TextView mGenerateTextView;
    private Random generator = new Random(System.currentTimeMillis());

    private List<String> mQuestionBank = new ArrayList<>();
    private List<TextView> mTextViews = new ArrayList<>();
    private List<EditText> mTextFields = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        for(String s : getResources().getStringArray(R.array.questions_for_gen))
        {
            mQuestionBank.add(s);
        }

        mGeneratorButton = (Button) findViewById(R.id.generate_button);
        mNewQButton = (Button) findViewById(R.id.new_question_button);
        mGenerateTextView = (TextView) findViewById(R.id.generate_text);
        mSavePasswordButton = (Button) findViewById(R.id.save_password_button);

        mTextViews.add((TextView)findViewById(R.id.question1));
        mTextViews.add((TextView)findViewById(R.id.question2));
        mTextViews.add((TextView)findViewById(R.id.question3));
        mTextViews.add((TextView)findViewById(R.id.question4));
        mTextViews.add((TextView)findViewById(R.id.question5));
        mTextViews.add((TextView)findViewById(R.id.question6));

        mTextFields.add((EditText)findViewById(R.id.input1));
        mTextFields.add((EditText)findViewById(R.id.input2));
        mTextFields.add((EditText)findViewById(R.id.input3));
        mTextFields.add((EditText)findViewById(R.id.input4));
        mTextFields.add((EditText)findViewById(R.id.input5));
        mTextFields.add((EditText) findViewById(R.id.input6));

        //Set generated pass to default
        mGenerateTextView.setText("");
        findNewQuestions();

        mGeneratorButton.setOnClickListener(this);
        mNewQButton.setOnClickListener(this);
        mSavePasswordButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generate_button:
                generate();
                break;
            case R.id.new_question_button:
                findNewQuestions();
                break;
            case R.id.save_password_button:
                boolean isGenerated = !mGenerateTextView.getText().toString().isEmpty();
                if(!isGenerated)
                {
                    isGenerated = generate();
                }

                if(isGenerated) {
                    Intent intent = new Intent(this, CheckerActivity.class);
                    Bundle b = new Bundle();
                    b.putString("password", mGenerateTextView.getText().toString());
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    private void findNewQuestions()
    {
        Set indexes = new HashSet<>();

        for(EditText edit : mTextFields)
        {
            edit.setText("");
        }

        for(TextView view : mTextViews)
        {
            int index = generator.nextInt(mQuestionBank.size());
            while(!indexes.add(index))
            {
                index = generator.nextInt(mQuestionBank.size());
            }
            view.setText(mQuestionBank.get(index));
        }
    }

    private boolean generate()
    {
        boolean error = false;
        List<String> possibleWords = new ArrayList<String>();
        for(EditText edit : mTextFields)
        {
            String text = edit.getText().toString();
            if(text.isEmpty())
            {
                error = true;
                edit.setError("You must answer this question!");
            }
            else
            {
                possibleWords.add(text);
            }
        }

        if(!error) {

            int attempts = 0;
            String result;
            do {

                int index1 = generator.nextInt(possibleWords.size());
                int index2 = generator.nextInt(possibleWords.size());
                while (index2 == index1) {
                    index2 = generator.nextInt(possibleWords.size());
                }
                String separator = SEPARATORS[generator.nextInt(SEPARATORS.length)];
                result = possibleWords.get(index1).toLowerCase() + separator.toLowerCase() + possibleWords.get(index2).toLowerCase();

            } while(attempts++ < 10 && (result.length() < 8 || result.length() > 64));

            mGenerateTextView.setText(changeLettersToNumbers(randomizeCase(result)));
        }

        return !error;
    }

    private static String changeLettersToNumbers(String field)
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

    private String randomizeCase(String str) {

        StringBuilder sb = new StringBuilder(str.length());

        for (char c : str.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(generator.nextBoolean()
                        ? Character.toLowerCase(c)
                        : Character.toUpperCase(c));
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }
}
