package com.fethbita.wordfinding;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void newWord_onClick(View v) {
        final TextView foundLetterCount = (TextView) findViewById(R.id.foundLetterCount);

        // Words go here
        final TextView lifeLeft = (TextView) findViewById(R.id.lifeLeft);
        lifeLeft.setText("6");
        String names[] = getResources().getStringArray(R.array.words);

        // Choose a random word
        Random rand = new Random();
        int randomWord = rand.nextInt(names.length);
        final String selectedWord = names[randomWord];
        foundLetterCount.setText("0 / " + Integer.toString(selectedWord.length()));

        // The words turn to underscores here
        final TextView word = (TextView) findViewById(R.id.word);
        word.setText(new String(new char[selectedWord.length()]).replace("\0", "_ "));

        GridLayout gridLayout = new GridLayout(getApplicationContext());
        GridLayout.Spec titleTxtSpecColumn = GridLayout.spec(2, GridLayout.BASELINE);
        GridLayout.Spec titleRowSpec = GridLayout.spec(0);
        TextView titleText = new TextView(getApplicationContext());
        titleText.setText("Title");
        gridLayout.addView(titleText, new GridLayout.LayoutParams(titleRowSpec , titleTxtSpecColumn));

        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.activity_main);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        String alphabet = getString(R.string.alphabet);

        final int[] buttonIds = new int[alphabet.length()];
        final Locale locale;
        int n;
        if (getResources().getString(R.string.locale).equals("tr")) {
            locale = new Locale("tr", "TR");
            n = 10;
        }
        else {
            locale = new Locale("en");
            n = 9;
        }

        for (int i = 0; i < alphabet.length(); i++) {
            final Button letter = new Button(this);
            buttonIds[i] = View.generateViewId();
            letter.setId(buttonIds[i]);
            letter.setText(Character.toString(alphabet.charAt(i)));
            layout.addView(letter);
            if (i / n == 0) {
                set.connect(buttonIds[i], ConstraintSet.TOP, R.id.newWord, ConstraintSet.BOTTOM, 200);
            } else {
                set.connect(buttonIds[i], ConstraintSet.TOP, buttonIds[i - n], ConstraintSet.BOTTOM, 0);
            }
            /*
            if (i % n == 0) {
                set.connect(buttonIds[i], ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 85);
            } else {
                set.connect(buttonIds[i], ConstraintSet.LEFT, buttonIds[i - 1], ConstraintSet.RIGHT, 0);
            }*/
            letter.setMinHeight(0);
            letter.setMinimumHeight(0);
            letter.setMinWidth(0);
            letter.setMinimumWidth(0);
            set.constrainHeight(buttonIds[i], ConstraintSet.WRAP_CONTENT);
            set.constrainWidth(buttonIds[i], ConstraintSet.WRAP_CONTENT);
            letter.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));

            letter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    letter.setEnabled(false);
                    String selectedLetter = letter.getText().toString();

                    // If lives are less than 0, do nothing.
                    if (Integer.parseInt(lifeLeft.getText().toString()) <= 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.lostTheGame), Toast.LENGTH_LONG).show();
                        return;
                    }
                    // If letter found, put it in the word
                    if (selectedWord.toUpperCase(locale).contains(selectedLetter)) {
                        Toast.makeText(getApplicationContext(), selectedLetter + " " + getString(R.string.found), Toast.LENGTH_SHORT).show();
                        int j = 0;
                        int foundLetters = Integer.parseInt(foundLetterCount.getText().toString().split(" / ")[0]);

                        for (char ch : selectedWord.toUpperCase(locale).toCharArray()) {
                            if (ch == selectedLetter.charAt(0)) {
                                char[] newWord = word.getText().toString().toCharArray();
                                newWord[j * 2] = selectedWord.charAt(j);
                                word.setText(String.valueOf(newWord));
                                foundLetters++;
                            }
                            j++;
                        }
                        foundLetterCount.setText(Integer.toString(foundLetters) + " / " + Integer.toString(selectedWord.length()));
                        if (foundLetters == selectedWord.length()) {
                            Toast.makeText(getApplicationContext(), getString(R.string.wonTheGame), Toast.LENGTH_LONG).show();
                            for (int k = 0; k < buttonIds.length; k++) {
                                (findViewById(buttonIds[k])).setVisibility(View.GONE);
                            }
                        }
                    }
                    // Else, lower the life
                    else {
                        lifeLeft.setText(Integer.toString(Integer.parseInt(lifeLeft.getText().toString()) - 1));
                        if (Integer.parseInt(lifeLeft.getText().toString()) <= 0) {
                            Toast.makeText(getApplicationContext(), getString(R.string.lostTheGame), Toast.LENGTH_LONG).show();
                            // Clear the list if lives are 0
                            for (int j = 0; j < buttonIds.length; j++) {
                                (findViewById(buttonIds[j])).setVisibility(View.GONE);
                            }
                            char[] newWord = word.getText().toString().toCharArray();
                            for (int j = 0; j < selectedWord.toUpperCase(locale).toCharArray().length; j++) {
                                newWord[j * 2] = selectedWord.charAt(j);
                            }
                            word.setText(String.valueOf(newWord));

                        }
                    }
                }
            });

        }
        for (int i = 0; i < alphabet.length(); i += n) {
            set.createHorizontalChain(
                    ConstraintSet.PARENT_ID, ConstraintSet.LEFT,
                    ConstraintSet.PARENT_ID, ConstraintSet.RIGHT,
                    Arrays.copyOfRange(buttonIds, i, (i + n > alphabet.length()) ? alphabet.length() : i + n),
                    null ,
                    ConstraintSet.CHAIN_SPREAD);
        }
        set.applyTo(layout);

        /*// Turkish alphabet
        String[] alphabet = getString(R.string.alphabet).split("");
        final ArrayList<String> list = new ArrayList<String>(Arrays.asList(alphabet));

        // Connect it to spinner
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner letters = (Spinner) findViewById(R.id.letters);
        letters.setAdapter(adapter);

        // On letter changed on spinner
        letters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Locale trlocale = new Locale("tr", "TR");
                String selectedLetter = adapterView.getSelectedItem().toString();
                // If the first item, do nothing
                if (selectedLetter.equals("")) {
                    return;
                }
                // If lives are less than 0, do nothing.
                if (Integer.parseInt(lifeLeft.getText().toString()) <= 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.lostTheGame), Toast.LENGTH_LONG).show();
                    return;
                }
                // If letter found, put it in the word
                if (selectedWord.toUpperCase(trlocale).contains(selectedLetter)) {
                    Toast.makeText(getApplicationContext(), selectedLetter + " " + getString(R.string.found), Toast.LENGTH_SHORT).show();
                    int i = 0;
                    int foundLetters = Integer.parseInt(foundLetterCount.getText().toString());

                    for (char ch : selectedWord.toUpperCase(trlocale).toCharArray()) {
                        if (ch == selectedLetter.charAt(0)) {
                            char[] newWord = word.getText().toString().toCharArray();
                            newWord[i * 2] = selectedWord.charAt(i);
                            word.setText(String.valueOf(newWord));
                            foundLetters++;
                        }
                        i++;
                    }
                    foundLetterCount.setText(Integer.toString(foundLetters));
                    if (foundLetters == selectedWord.length()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.wonTheGame), Toast.LENGTH_LONG).show();
                        list.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                // Else, lower the life
                else {
                    lifeLeft.setText(Integer.toString(Integer.parseInt(lifeLeft.getText().toString()) - 1));
                    if (Integer.parseInt(lifeLeft.getText().toString()) <= 0) {
                        Toast.makeText(getApplicationContext(), getString(R.string.lostTheGame), Toast.LENGTH_LONG).show();
                        // Clear the list if lives are 0
                        list.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }
                }
                // Remove the selected letter
                list.remove(position);
                adapter.notifyDataSetChanged();
                letters.setSelection(0);
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });*/
    }


}
