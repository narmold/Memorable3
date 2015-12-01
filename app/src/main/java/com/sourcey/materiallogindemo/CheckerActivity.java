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
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckerActivity extends AppCompatActivity implements View.OnClickListener, SpellCheckerSessionListener {

    private static Pattern lowercaseCharPattern;
    private static Pattern uppercaseCharPattern;
    private static Pattern numberCharPattern;
    private static Pattern specialCharPattern;
    private static Pattern fullWordPattern;
    private static Pattern segmentPattern;
    private static final int MINIMUM_PASSWORD_LENGTH = 8;
    private static final int MAXIMUM_PASSWORD_LENGTH = 64;
    private static final int CHECKABLE_SEGMENT_LENGTH = 4;
    private static final int DANGER_PROGRESS = 25;
    private static final int CAUTION_PROGRESS = 50;
    private static final double UNIQUENESS_MINIMUM_PERCENTAGE = 0.25;

    static
    {
        lowercaseCharPattern = Pattern.compile("(.*)[a-z](.*)");
        uppercaseCharPattern = Pattern.compile("(.*)[A-Z](.*)");
        numberCharPattern = Pattern.compile("(.*)[0-9](.*)");
        specialCharPattern = Pattern.compile("(.*)[" + Pattern.quote("@#$%&*+-_(),':;?.![]") + "\\s\\/](.*)");
        fullWordPattern = Pattern.compile("[A-Za-z][a-z]+");
        segmentPattern = Pattern.compile("([A-Za-z]+)|([0-9]+)");
    }

    String[] menu;
    DrawerLayout dLayout;
    ListView dList;
    ArrayAdapter<String> adapter;

    TextView titleView;
    EditText passwordView;
    Map<Integer, CheckedTextView> requirementViewMap = new HashMap<>();
    ProgressBar passwordStrengthView;
    Button saveButtonView;

    String account;
    String site;
    String user;


    Map<Integer, Predicate<String>> requirementsMap = new HashMap<>();
    SpellCheckerSession spellCheckerSession;
    final Object spellingLock = new Object();
    Double spelledCorrectlyPercentage;
    final Object progressLock = new Object();
    Integer progressValue;
    DatabaseHelper database;
    Pattern passwordPattern;
    Pattern infoPattern;


    /**
     * Generates a matching pattern for past and current passwords.
     *
     * @param database the database to use.
     * @param account the username currently logged in.
     * @return the compiled regular expression pattern to match passwords.
     */
    private static Pattern generatePasswordPattern(DatabaseHelper database, String account)
    {
        boolean hasSinglePassword = false;
        StringBuilder passwordRegex = new StringBuilder("(.*)(");
        for(PasswordInfo password : database.selectPasswords(account))
        {
            hasSinglePassword = true;
            String pswd = password.getPassword();

            passwordRegex.append(Pattern.quote(pswd));
            passwordRegex.append("|");

            Matcher matcher = segmentPattern.matcher(pswd);
            while(matcher.find())
            {
                String segment = matcher.group();
                if(segment.length() >= CHECKABLE_SEGMENT_LENGTH && !segment.equals(pswd)) {
                    passwordRegex.append(Pattern.quote(segment));
                    passwordRegex.append("|");
                }
            }
        }
        passwordRegex.deleteCharAt(passwordRegex.length() - 1); //Delete extra '|'
        passwordRegex.append(")(.*)");

        return hasSinglePassword ? Pattern.compile(passwordRegex.toString()) : Pattern.compile("a^");
    }

    /**
     * Generates a matching pattern for past and current passwords.
     *
     * @param database the database to use.
     * @param account the username currently logged in.
     * @return the compiled regular expression pattern to match passwords.
     */
    private static Pattern generateInfoPattern(DatabaseHelper database, String account)
    {
        StringBuilder infoRegex = new StringBuilder("(.*)(");
        infoRegex.append(Pattern.quote(account));
        infoRegex.append("|");

        Matcher matcher = segmentPattern.matcher(account);
        while(matcher.find())
        {
            String segment = matcher.group();
            if(segment.length() >= CHECKABLE_SEGMENT_LENGTH && !segment.equals(account)) {
                infoRegex.append(Pattern.quote(segment));
                infoRegex.append("|");
            }
        }

        for(PasswordInfo password : database.selectPasswords(account))
        {
            String site = password.getWebsite();
            infoRegex.append(Pattern.quote(site));
            infoRegex.append("|");
            matcher = segmentPattern.matcher(site);
            while(matcher.find())
            {
                String segment = matcher.group();
                if(segment.length() >= CHECKABLE_SEGMENT_LENGTH && !segment.equals(site)) {
                    infoRegex.append(Pattern.quote(segment));
                    infoRegex.append("|");
                }
            }

            String username = password.getUsername();
            infoRegex.append(Pattern.quote(username));
            infoRegex.append("|");
            matcher = segmentPattern.matcher(username);
            while(matcher.find())
            {
                String segment = matcher.group();
                if(segment.length() >= CHECKABLE_SEGMENT_LENGTH && !segment.equals(username)) {
                    infoRegex.append(Pattern.quote(segment));
                    infoRegex.append("|");
                }
            }
        }
        infoRegex.deleteCharAt(infoRegex.length() - 1); //Delete extra '|'
        infoRegex.append(")(.*)");

        return Pattern.compile(infoRegex.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checker);

        account = getApplicationContext().getSharedPreferences("Preferences", 0).getString("account_name", "Broken");
        database = new DatabaseHelper(getApplicationContext());
        passwordPattern = generatePasswordPattern(database, account);
        infoPattern = generateInfoPattern(database, account);

        titleView = (TextView) findViewById(R.id.titleText);
        passwordView = (EditText) findViewById(R.id.passwordInputField);
        passwordStrengthView = (ProgressBar) findViewById(R.id.passwordStrengthBar);
        saveButtonView = (Button) findViewById(R.id.savePasswordButton);

        Intent intent = getIntent();
        if(intent != null) {
            Bundle data = intent.getExtras();
            if (data != null) {
                passwordView.setText(data.getString("password", ""));
                site = data.getString("website", null);
                user = data.getString("username", null);
            }
        }

        final TextServicesManager tsm = (TextServicesManager) getSystemService(
                Context.TEXT_SERVICES_MANAGER_SERVICE);
        spellCheckerSession = tsm.newSpellCheckerSession(null, null, this, true);
        spelledCorrectlyPercentage = 0.0;
        progressValue = 0;

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
                return s.length() >= MINIMUM_PASSWORD_LENGTH && s.length() <= MAXIMUM_PASSWORD_LENGTH;
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

                if(s.length() > 0) {
                    Matcher matcher = fullWordPattern.matcher(s);
                    List<TextInfo> matches = new ArrayList<>();
                    while (matcher.find()) {
                        matches.add(new TextInfo(matcher.group()));
                    }
                    if(matches.size() > 0) {
                        TextInfo[] array = new TextInfo[matches.size()];
                        spellCheckerSession.getSentenceSuggestions(matches.toArray(array), 1);
                    }
                }

                synchronized (spellingLock) {
                    return spelledCorrectlyPercentage <= UNIQUENESS_MINIMUM_PERCENTAGE;
                }
            }
        });
        requirementsMap.put(R.id.infoRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return !infoPattern.matcher(s).matches();
            }
        });
        requirementsMap.put(R.id.similarityRequirementText, new Predicate<String>() {
            @Override
            public boolean apply(String s) {
                return !passwordPattern.matcher(s).matches();
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

        synchronized (progressLock) {
            progressValue = (int) (((double) accepted / (double) requirementsMap.size()) * 100);
            passwordStrengthView.setProgress(progressValue);

            if (passwordStrengthView.getProgress() <= DANGER_PROGRESS) {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else if (passwordStrengthView.getProgress() <= CAUTION_PROGRESS) {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            } else {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.savePasswordButton:

                    if(!requirementsMap.get(R.id.lengthRequirementText).apply(passwordView.getText().toString()))
                    {
                        passwordView.setError("The password must be between 8 and 64 characters in length.");
                    }
                    else if(passwordStrengthView.getProgress() <= CAUTION_PROGRESS)
                    {
                        passwordView.setError("You must have more than " + CAUTION_PROGRESS + "% password strength to use a password.");
                    }
                    else if(site != null && user != null) {
                        database.modifySitePassword(account, site, user, passwordView.getText().toString());
                        Intent i = new Intent();
                        Bundle b = new Bundle();
                        b.putString("password", passwordView.getText().toString());
                        i.putExtras(b);
                        //finish the activity and return the edit password screen
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Intent intent = new Intent(this, NewEntry.class);
                        intent.putExtra("password", passwordView.getText().toString());
                        startActivity(intent);
                    }
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

        boolean checked;
        synchronized (spellingLock) {
            spelledCorrectlyPercentage = 1.0 - ((double) totalMisspelled / (double) totalWords);
            checked = spelledCorrectlyPercentage <= UNIQUENESS_MINIMUM_PERCENTAGE;
        }
        CheckedTextView textView = requirementViewMap.get(R.id.uniqueRequirementText);
        boolean newlyChecked = !textView.isChecked() && checked;
        boolean newlyUnchecked = textView.isChecked() && !checked;
        textView.setChecked(checked);

        synchronized (progressLock) {
            if(newlyChecked) {
                progressValue += (int) ((1.0 / (double) requirementsMap.size()) * 100);
            } else if (newlyUnchecked) {
                progressValue -= (int) ((1.0 / (double) requirementsMap.size()) * 100);
            }
            passwordStrengthView.setProgress(progressValue);

            if (passwordStrengthView.getProgress() <= DANGER_PROGRESS) {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            } else if (passwordStrengthView.getProgress() <= CAUTION_PROGRESS) {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            } else {
                passwordStrengthView.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            }
        }
    }
}