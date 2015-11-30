package com.sourcey.materiallogindemo;

import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.textservice.TextInfo;
import android.view.textservice.TextServicesManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import android.view.textservice.SpellCheckerSession;
import android.view.textservice.SpellCheckerSession.SpellCheckerSessionListener;
import android.view.textservice.SuggestionsInfo;
import android.view.textservice.SentenceSuggestionsInfo;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckerActivity extends AppCompatActivity implements View.OnClickListener, SpellCheckerSessionListener {

    private static Pattern lowercaseCharPattern;
    private static Pattern uppercaseCharPattern;
    private static Pattern numberCharPattern;
    private static Pattern specialCharPattern;
    private static Pattern fullWordPattern;

    static
    {
        lowercaseCharPattern = Pattern.compile("(.*)[a-z](.*)");
        uppercaseCharPattern = Pattern.compile("(.*)[A-Z](.*)");
        numberCharPattern = Pattern.compile("(.*)[0-9](.*)");
        specialCharPattern = Pattern.compile("(.*)[@#$%&*+\\-_(),':;?.!\\[\\]\\s\\/](.*)");
        fullWordPattern = Pattern.compile("[A-Za-z]+");
    }

    String[] menu;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapter;

    TextView titleView;
    EditText passwordView;
    Map<Integer, CheckedTextView> requirementViewMap = new HashMap<>();
    Map<Integer, Predicate<String>> requirementsMap = new HashMap<>();
    ProgressBar passwordStrengthView;
    Button saveButtonView;
    SpellCheckerSession spellCheckerSession;
    double spelledCorrectlyPercentage;
    Semaphore spellCheckLock = new Semaphore(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        titleView = (TextView) findViewById(R.id.titleText);
        passwordView = (EditText) findViewById(R.id.passwordInputField);
        passwordStrengthView = (ProgressBar) findViewById(R.id.passwordStrengthBar);
        saveButtonView = (Button) findViewById(R.id.savePasswordButton);

        Intent intent = getIntent();
        if(intent != null) {
            Bundle data = intent.getExtras();
            if (data != null) {
                passwordView.setText(data.getString("password", ""));
            }
        }

        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        spellCheckerSession = tsm.newSpellCheckerSession(null, null, this, true);
        spelledCorrectlyPercentage = 0.0;

        requirementViewMap.put(R.id.lengthRequirementText, (CheckedTextView) findViewById(R.id.lengthRequirementText));
        requirementViewMap.put(R.id.lowerCaseRequirementText, (CheckedTextView) findViewById(R.id.lowerCaseRequirementText));
        requirementViewMap.put(R.id.upperCaseRequirementText, (CheckedTextView) findViewById(R.id.upperCaseRequirementText));
        requirementViewMap.put(R.id.numberRequirementText, (CheckedTextView) findViewById(R.id.numberRequirementText));
        requirementViewMap.put(R.id.specialRequirementText, (CheckedTextView) findViewById(R.id.specialRequirementText));
        requirementViewMap.put(R.id.uniqueRequirementText, (CheckedTextView) findViewById(R.id.uniqueRequirementText));
        requirementViewMap.put(R.id.infoRequirementText, (CheckedTextView) findViewById(R.id.infoRequirementText));
        requirementViewMap.put(R.id.similarityRequirementText, (CheckedTextView) findViewById(R.id.similarityRequirementText));

        requirementsMap.put(R.id.lengthRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return s.length() >= 8 && s.length() <= 64;
            }
        });
        requirementsMap.put(R.id.lowerCaseRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return lowercaseCharPattern.matcher(s).matches();
            }
        });
        requirementsMap.put(R.id.upperCaseRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return uppercaseCharPattern.matcher(s).matches();
            }
        });
        requirementsMap.put(R.id.numberRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return numberCharPattern.matcher(s).matches();
            }
        });
        requirementsMap.put(R.id.specialRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return specialCharPattern.matcher(s).matches();
            }
        });
        requirementsMap.put(R.id.uniqueRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {

                if(s.length() > 1) {
                    Matcher matcher = fullWordPattern.matcher(s);
                    List<TextInfo> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(new TextInfo(matcher.group()));
                    }
                    if(matches.size() > 0) {
                        TextInfo[] array = new TextInfo[matches.size()];
                        spellCheckerSession.getSentenceSuggestions(matches.toArray(array), 1);
                        spellCheckLock.acquireUninterruptibly();
                    }
                }

                return spelledCorrectlyPercentage <= 0.25;
            }
        });
        requirementsMap.put(R.id.infoRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return true;
            }
        });
        requirementsMap.put(R.id.similarityRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return true;
            }
        });

        saveButtonView.setOnClickListener(this);

        passwordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /* Don't need to do anything... */
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /* Don't need to do anything... */
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateRequirementViews(s.toString());
            }
        });

        updateRequirementViews(passwordView.getText().toString());
    }

    /**
     * Updates the requirement views to match the new password.
     */
    private void updateRequirementViews(String newText) {

        int accepted = 0;
        boolean isEmpty = newText.isEmpty();
        for (Map.Entry<Integer, Predicate<String>> entry : requirementsMap.entrySet())
        {
            if(isEmpty)
            {
                requirementViewMap.get(entry.getKey()).setChecked(false);
            }
            else if(entry.getValue().apply(newText)) {
                requirementViewMap.get(entry.getKey()).setChecked(true);
                accepted++;
            }
            else {
                requirementViewMap.get(entry.getKey()).setChecked(false);
            }
        }

        passwordStrengthView.setProgress((int) (((double) accepted / (double) requirementsMap.size()) * 100));

        if(passwordStrengthView.getProgress() <= 25) {
            passwordStrengthView.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        }
        else if(passwordStrengthView.getProgress() <= 50)
        {
            passwordStrengthView.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
        }
        else
        {
            passwordStrengthView.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.savePasswordButton:

                break;
        }
    }

    @Override
    public void onGetSuggestions(final SuggestionsInfo[] arg0) {
        //Deprecated.. Do not use...
    }

    @Override
    public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] arg0) {

        int totalWords = 0;
        int totalMisspelled = 0;

        for(SentenceSuggestionsInfo info : arg0)
        {
            for(int i = 0; i < info.getSuggestionsCount(); i++) {
                totalWords++;
                if ((info.getSuggestionsInfoAt(i).getSuggestionsAttributes() & SuggestionsInfo.RESULT_ATTR_IN_THE_DICTIONARY) != SuggestionsInfo.RESULT_ATTR_IN_THE_DICTIONARY) {
                    totalMisspelled++;
                }
            }
        }

        spelledCorrectlyPercentage = 1.0 - ((double)totalMisspelled / (double)totalWords);
        spellCheckLock.release();
    }
}