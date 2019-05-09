package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.myfirstapp.DisplayMessageActivity.EXTRA_MESSAGE1;
import static com.example.myfirstapp.DisplayMessageActivity.EXTRA_MESSAGE2;



public class MainActivity extends AppCompatActivity {
    static final String STATE_USER = "user";
    private String mUser;

    public static final String EXTRA_MESSAGE = "com.example.MainActivity.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mUser = savedInstanceState.getString(STATE_USER);
        } else {
            mUser = "NewUser";
        }
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            double score = extras.getDouble(EXTRA_MESSAGE1);
            String name = extras.getString(EXTRA_MESSAGE2);

            TextView textName1 = findViewById(R.id.textName1);
            TextView textScore1 = findViewById(R.id.textScore1);

            String compare = textScore1.getText().toString();
            double oldScore = Double.parseDouble(compare);

            if (score > oldScore) {
                textScore1.setText(Double.toString(score));
            }

            textName1.setText(name);
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(STATE_USER, mUser);
        super.onSaveInstanceState(savedInstanceState);

    }


    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}

