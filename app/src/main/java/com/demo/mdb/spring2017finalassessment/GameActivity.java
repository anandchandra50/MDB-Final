package com.demo.mdb.spring2017finalassessment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Future;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    TextView phraseTextView;
    TextView accuracyTextView;
    ImageView doneImageView;
    Chronometer timer;
    EditText typedEditText;

    String phrase;
    String[] splitPhrase;
    boolean gameStarted = false;
    String typedString;

    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        phraseTextView = (TextView) findViewById(R.id.phraseTextView);
        accuracyTextView = (TextView) findViewById(R.id.wpmTextView);
        doneImageView = (ImageView) findViewById(R.id.doneImageView);
        timer = (Chronometer) findViewById(R.id.timer);
        typedEditText = (EditText) findViewById(R.id.typedEditText);

        doneImageView.setOnClickListener(this);

        /* TODO Part 4
         * call getRandomPhrase in a new thread. Use the result of this to set the phrase variable,
         * set the phraseTextView to that phrase, and set splitPhrase to phrase.split("\\s")
         */

        // right now there's a slight issue with this
        // it takes time to fetch phrase, and we may try to access it before it's fetched
        // so I'm using a progress bar to try to solve that

        final ProgressDialog nDialog;
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Creating Account");
        nDialog.setIndeterminate(true);
        nDialog.setCancelable(false);
        nDialog.show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Future<String> futurePhrase = Utils.getRandomPhrase();
                    phrase = futurePhrase.get();
                    phraseTextView.setText(phrase);
                    splitPhrase = phrase.split("\\s");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                nDialog.hide();
            }
        }.execute();

        typedEditText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        typedEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!gameStarted) {
                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                    gameStarted = true;
                }
                typedString = s.toString();
                accuracyTextView.setText(getAccuracyPercentage() + "%");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.doneImageView:
                clickDone();
                break;
        }
    }

    public void clickDone() {
        timer.stop();
        int accuracyPercentage = getAccuracyPercentage();
        int wpm = getWPM();
        accuracyTextView.setText(accuracyPercentage + "% with " + wpm + " WPM");

        /* TODO Part 6
         * Add a game to the database. Keep in mind that we also want to know which users are
         * attached to which games, so we should be changing two nodes of the database here, "games"
         * and "users". We know how to generate random keys for adding games, but how can we add
         * that game to our user's node? We can't generate a random key, because we want it to go
         * to the same node every time for a given user.
         */
        // create a random id (key) for game with push
        // store info for that game under games.child(key)
        // store key for that game under user.child(key)
        DatabaseReference ref = firebaseDatabase.getReference();
        DatabaseReference gameRef = ref.child("games");
        final DatabaseReference userRef = ref.child(mAuth.getCurrentUser().getUid());

        final String key = gameRef.push().getKey();

        // create game to store
        Game game = new Game(phrase, typedString, accuracyPercentage, wpm);
        gameRef.child(key).setValue(game).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // successfully set to game ref, set to user ref
                    userRef.child(key).setValue(key);
                } else {
                    Utils.displayError(getApplicationContext(), "Couldn't save game");
                }
            }
        });


        typedEditText.setEnabled(false);
        doneImageView.setClickable(false);
    }

    private int getAccuracyPercentage() {
        String[] typedStringWords = typedString.split("\\s");
        double correctCount = 0;
        for (int i = 0; i < typedStringWords.length; i++) {
            if (i < splitPhrase.length && typedStringWords[i].equals(splitPhrase[i])) {
                correctCount++;
            }
        }
        return (int) (100 * correctCount / typedStringWords.length);
    }

    private int getWPM() {
        double minutes = ((SystemClock.elapsedRealtime() - timer.getBase()) / 1000.0) / 60.0;
        return (int) (typedString.split("\\s").length / minutes);
    }
}
