package com.example.acpha.memorygame;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayRulesActivity extends AppCompatActivity {

    //Creation of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_rules);
    }

    //Show rules when activity is started
    protected void onStart()
    {
        super.onStart();
        Resources res = getResources(); //Get resources, namely the array "game_rules"
        String[] rules = res.getStringArray(R.array.game_rules);
        TextView text = (TextView)findViewById(R.id.textView2);
        int arraySize = rules.length;
        for (int i = 0; i < arraySize; i++)
        {
            text.append(rules[i]);
            text.append("\n");
            text.append("\n");
        }

    }
}
