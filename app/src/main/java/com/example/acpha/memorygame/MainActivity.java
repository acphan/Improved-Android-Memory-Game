package com.example.acpha.memorygame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity {

    //Creation of activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //Go to Play Screen when clicked
    public void clickPlay(View view) {
        Intent intentPlay = new Intent(this, DisplayGameActivity.class);
        startActivity(intentPlay);
    }

    //Go to Rules Screen when clicked
    public void readRules(View view) {
        Intent intentRules = new Intent(this, DisplayRulesActivity.class);
        startActivity(intentRules);
    }
}