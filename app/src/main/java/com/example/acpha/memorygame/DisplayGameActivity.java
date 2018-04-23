package com.example.acpha.memorygame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DisplayGameActivity extends Activity{
        private static int ROW_COUNT = 4;   //# of rows
        private static int COL_COUNT = 5;   //# of columns
        private Context context;    //What is inside screen where the tiles will be
        private Drawable tileCover;     //Cover picture of the tiles before flip
        private int [] [] tiles;    //array of tiles
        private List<Drawable> tileImages;  //array of the images that will be used
        private Tile tileOne;   //first tile picked
        private Tile tileTwo;   //second tile picked
        private ButtonListener buttonListener;  //what to do when clicked
        private static Object lock = new Object();
        private TableLayout mainTable;  //table where the tiles will be
        private UpdateTilesHandler handler; //handle the comparison of tiles
        private ArrayList<Integer> list;    //used to randomize the tile images

        @BindView(R.id.TableLayout03) TableLayout mTable;
        @BindView(R.id.mainTable) TableRow gTable;
        @BindView(R.id.pointDisp) TextView points;
        @BindView(R.id.Spinner01) Spinner spinner1;

        //Creation of activity including the main table, points, spinner, tile picture, etc.
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ButterKnife.bind(this);
            handler = new UpdateTilesHandler();
            loadImages();   //get images
            setContentView(R.layout.activity_display_game);
            tileCover = getResources().getDrawable(R.drawable.mystery); //draw tile cover picture
            buttonListener = new ButtonListener();
            mainTable = mTable;
            context = mainTable.getContext();
            Spinner spin = spinner1;  //spinner to choose pattern
            ArrayAdapter adapter = ArrayAdapter.createFromResource(
                    this, R.array.type, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adapter);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                @Override
                public void onItemSelected(
                        android.widget.AdapterView<?> arg0,
                        View arg1, int pos, long arg3){
                    spinner1.setSelection(0);
                    int col,row;    //possible patterns for 20 tiles
                    switch (pos) {
                        case 1:
                            col=5;row=4;
                            break;
                        case 2:
                            col=4;row=5;
                            break;
                        default:
                            return;
                    }
                        newGame(col, row);

                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            //Load saved state

        }


        //Create new pattern of tiles, all covered
        public void newGame(int col, int row) {
            ROW_COUNT = row;
            COL_COUNT = col;
            ButterKnife.bind(this);
            tiles = new int [COL_COUNT] [ROW_COUNT];


            TableRow tr = gTable;
            tr.removeAllViews();
            mainTable = new TableLayout(context);
            tr.addView(mainTable);
            for (int y = 0; y < ROW_COUNT; y++) {
                mainTable.addView(createRow(y));
            }
            tileOne=null;
            loadTiles();
            TextView score = points;
            score.setText("Points: 0");
        }


        //Load the images from Resources
        public void loadImages() {
            tileImages = new ArrayList<Drawable>();

            //add all images in resources into an array
            tileImages.add(getResources().getDrawable(R.drawable.serk));
            tileImages.add(getResources().getDrawable(R.drawable.chrono));
            tileImages.add(getResources().getDrawable(R.drawable.dare));
            tileImages.add(getResources().getDrawable(R.drawable.druid));
            tileImages.add(getResources().getDrawable(R.drawable.herald));
            tileImages.add(getResources().getDrawable(R.drawable.reap));
            tileImages.add(getResources().getDrawable(R.drawable.scrap));
            tileImages.add(getResources().getDrawable(R.drawable.temp));
            tileImages.add(getResources().getDrawable(R.drawable.drag));
            tileImages.add(getResources().getDrawable(R.drawable.gw2));
        }

        //Load in the tiles
        public void loadTiles(){
                int size = ROW_COUNT*COL_COUNT;
                list = new ArrayList<Integer>();

                //Count the amount of tiles needed
                for(int i=0;i<size;i++){
                    list.add(new Integer(i));
                }

                //Randomize, mix, shuffle the tiles
                Random r = new Random();
                for(int i=size-1;i>=0;i--){
                    int t=0;
                    if(i>0){
                        t = r.nextInt(i);
                    }
                    t=list.remove(t).intValue();
                    tiles[i%COL_COUNT][i/COL_COUNT]=t%(size/2);
                }
            }

        //Create the rows for the main table, add buttons
        private TableRow createRow(int y){
            TableRow row = new TableRow(context);
            row.setHorizontalGravity(Gravity.CENTER);

            //Create buttons for the columns as well
            for (int x = 0; x < COL_COUNT; x++) {
                row.addView(createImageButton(x,y));
            }
            return row;
        }

        //Create the tiles with the pictures for the game, generate button ids
        private View createImageButton(int x, int y){
            Button button = new Button(context);
            button.setBackgroundDrawable(tileCover);
            button.setId(100*x+y);
            button.setOnClickListener(buttonListener);
            return button;
        }

        //Active when a tile/button is clicked
        class ButtonListener implements OnClickListener {
            @Override
            public void onClick(View view) {

                synchronized (lock) {
                    if(tileOne!=null && tileTwo != null){
                        return;
                    }
                    //Get button ids and location
                    int id = view.getId();
                    int x = id/100;
                    int y = id%100;
                    flipTile((Button)view,x,y);
                }

            }

            //flip the selected tile and assign to either first or second tile
            private void flipTile(Button button,int x, int y) {
                button.setBackgroundDrawable(tileImages.get(tiles[x][y]));

                //If no previously selected tile, it is the first
                if(tileOne==null){
                    tileOne = new Tile(button,x,y);
                    YoYo.with(Techniques.Wave)
                            .duration(2000)
                            .playOn(tileOne.button);
                }
                else{
                    if(tileOne.x == x && tileOne.y == y){
                        return;
                    }

                    //Select second tile
                    tileTwo = new Tile(button,x,y);
                    YoYo.with(Techniques.Wave)
                            .duration(2000)
                            .playOn(tileTwo.button);

                    //Timer to prevent infinite wait,stuck on the two selected tiles
                    TimerTask tt = new TimerTask() {
                        @Override
                        public void run() {
                            try{
                                synchronized (lock) {
                                    handler.sendEmptyMessage(0);
                                }
                            }
                            catch (Exception e) {
                                Log.e("error", e.getMessage());
                            }
                        }
                    };

                    Timer t = new Timer(false);
                    t.schedule(tt, 200);
                }


            }

        }

        //Deal with comparison, make invisible if yes, and add a point, if no, reset the chosen tiles
        class UpdateTilesHandler extends Handler{

            int points = 0;

            @Override
            public void handleMessage(Message msg) {
                synchronized (lock) {
                    compareTiles();
                }
            }

            //Compare tiles if they have the same images. Set to invis if true and add point
            public void compareTiles(){
                if(tiles[tileTwo.x][tileTwo.y] == tiles[tileOne.x][tileOne.y]){
                    tileOne.button.setVisibility(View.INVISIBLE);
                    tileTwo.button.setVisibility(View.INVISIBLE);
                    points = points + 1;
                   TextView score = (TextView) findViewById(R.id.pointDisp);
                    score.setText("Points: " + points);
                }
                // "flip" back into cover picture and null the selected tiles
                else {
                    tileTwo.button.setBackgroundDrawable(tileCover);
                    tileOne.button.setBackgroundDrawable(tileCover);
                }
                tileOne=null;
                tileTwo=null;
            }
        }
    }


