package com.example.acpha.memorygame;

import android.widget.Button;

//Create tiles for the memory game
public class Tile{

    public int x;
    public int y;
    public Button button;

    //Tiles are buttons
    public Tile(Button button, int x,int y) {
        this.x = x;
        this.y=y;
        this.button=button;
    }


}
