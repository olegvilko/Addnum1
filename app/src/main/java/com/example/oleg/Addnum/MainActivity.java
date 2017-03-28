package com.example.oleg.Addnum;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.PaintDrawable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.oleg.pipes2.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity  implements View.OnTouchListener {

    RelativeLayout layoutMenu;
    LinearLayout layoutForLine;
    LinearLayout layoutX;
    LinearLayout layoutGame;
    TextView textStatus;
    EditText editTextWorld;
    Random rand;
    Button button;
    ImageButton imageButton;
    ImageView imageView;
    int imageViewID;
    int fieldX;
    int fieldY;
    float touchX;
    float touchY;
    float posFieldX;
    float posFieldY;
    float dragX = 0;
    float dragY = 0;
    int live;
    int statusPoint;
    int colorLive;
    int colorGold;
    int colorZ;
    int colorBorder;
    int gold;
    int world;
    int worldMax;
    int level;
    int targetX;
    int targetY;
    int cellXY;

    String saveLevel;
    String saveWorld;
    String saveLive;
    String savePoints;

    SharedPreferences mSettings;

    Button buttonNewGame;
    Button buttonContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        layoutMenu = (RelativeLayout) findViewById(R.id.layoutMenu);
        layoutForLine = (LinearLayout) findViewById(R.id.layoutForLine);
        layoutGame = (LinearLayout) findViewById(R.id.layoutGame);
        textStatus = (TextView) findViewById(R.id.textStatus);
        editTextWorld = (EditText) findViewById(R.id.editTextWorld);

        buttonNewGame = (Button) findViewById(R.id.buttonNewGame);
        buttonContinue = (Button) findViewById(R.id.buttonСontinue);

        buttonContinue.setWidth(400);

        colorLive = Color.YELLOW;
        colorGold = Color.YELLOW;
        colorZ = Color.RED;
        colorBorder=Color.GRAY;
        saveLevel = "level";
        saveWorld = "world";
        saveLive = "live";
        savePoints = "points";
        mSettings = getSharedPreferences(saveLevel, Context.MODE_PRIVATE);

        worldMax=2147483647;
        cellXY=130;
        imageViewID=1000000;

       // editTextWorld.setText(String.valueOf(1));
    }

    public void saveGame() {

        SharedPreferences.Editor ed = mSettings.edit();
        ed.putInt(saveLevel, level);
        ed.putInt(saveWorld, world);
        ed.putInt(saveLive, live);
        ed.putInt(savePoints, statusPoint);
        ed.commit();
    }

    public void buttonContinue(View view) {
        newGame();

        level = mSettings.getInt(saveLevel, 0);
        world = mSettings.getInt(saveWorld, 0);
        live = mSettings.getInt(saveLive, 0);
        statusPoint = mSettings.getInt(savePoints, 0);
        //    }
       // layoutMenu.setVisibility(View.INVISIBLE);
        //layoutGame.setVisibility(View.VISIBLE);
        gameMenuVisible();
        newField();
        statusBar();
    }

    public void newGame() {

        posFieldX = 0;
        posFieldY = 0;

        live = 20;
        gold = 0;
        level = 1;
        statusPoint = 0;


    }

    public void statusBar() {
        textStatus.setText("World:" + world + "  Level:" + level + "  Gold:" + gold + "  Live:" + live + "  Points:" + statusPoint);
    }

    public boolean checkNearButtons(int target) {
        targetY = target / fieldX;
        targetX = target - targetY * fieldX;
        if (checkButton(targetX + 1, targetY) || checkButton(targetX, targetY + 1) ||
                checkButton(targetX - 1, targetY) || checkButton(targetX, targetY - 1)) {
            return true;
        }
        return false;
    }

    public boolean checkButton(int x, int y) {
        if (x > 0 && x < fieldX-1 && y > 0 && y < fieldY-1) {
            int xy = x + y * fieldX;
            Button btNear = (Button) findViewById(xy);
            if (btNear.getText() == "") {
                return true;
            }
        }
        return false;
    }

    public void addPoint()
    {
        statusPoint++;
        if (statusPoint == 10) {
            live += 1;
            statusPoint = 0;
        }
    }

    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragX = posFieldX;
                dragY = posFieldY;

                touchX = event.getRawX();
                touchY = event.getRawY();


                break;
            case MotionEvent.ACTION_UP:
            if (v.getId()<imageViewID) {
                if (Math.abs(dragX - posFieldX) < 20 && Math.abs(dragY - posFieldY) < 20) {

                    button = (Button) findViewById(v.getId());

                    if (button.getText() != "" && button.getText() != " ") {
                        if (checkNearButtons(v.getId())) {

                            if (button.getText() == "*") {
                                button.setBackgroundColor(colorLive);
                                button.setText("");
                                addPoint();
                                gold--;
                                if (gold == 0) {
                                    endLevel();
                                }

                            } else {
                                button.setBackgroundColor(colorLive);
                                addPoint();

                                if (button.getTextColors().getDefaultColor() == colorGold) {

                                    live += Integer.parseInt(button.getText().toString());
                                    button.setText("");
                                } else {
                                    live -= Integer.parseInt(button.getText().toString());
                                    if (live < 0) {
                                        gameOver();
                                    }
                                    button.setText("");
                                }

                            }

                        }
                    }
                    statusBar();
                }
            }
                break;
            case MotionEvent.ACTION_MOVE:
                posFieldX = dragX - (int) (event.getRawX() - touchX);
                posFieldY = dragY - (int) (event.getRawY() - touchY);

                layoutForLine.setScrollX((int) posFieldX);
                layoutForLine.setScrollY((int) posFieldY);
                break;
        }
        return true;
    }

    public void gameOver() {
        toast("Game over");
        layoutForLine.removeAllViews();
        layoutGame.setVisibility(View.INVISIBLE);
        layoutMenu.setVisibility(View.VISIBLE);
        newGame();
    }

    public void endLevel() {
        layoutForLine.removeAllViews();
        newLevel();
    }

    public void toast(String str) {
        Toast toast = Toast.makeText(getApplicationContext(),
                str,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public void newLevel() {
        level++;
        toast("Level " + level);
        fieldX = fieldX + 1;
        fieldY = fieldY + 1;
        saveGame();
        newField();
    }


    public void buttonNewGame(View view) {
        try {
            world = Integer.parseInt(editTextWorld.getText().toString());

        } catch (NumberFormatException e) {
            world = worldMax;
        }
        newGame();
        gameMenuVisible();
        newField();
        statusBar();
    }

    public void buttonRandomWorld(View view) {
        rand=new Random();
        int rnd=rand.nextInt(worldMax);
        editTextWorld.setText(String.valueOf(rnd));
    }

    public void buttonHelp(View view) {
        String str = "Target: go to red field of green. Near green field can be set new green field. Black numeral take away live, blue add to. 10 open field (point) +1 live";
        Toast toast = Toast.makeText(getApplicationContext(),
                str,
                Toast.LENGTH_LONG);
        toast.show();
    }

    public void buttonExit(View view) {
        finish();
    }

    public void newField() {
        layoutForLine.removeAllViews();
        fieldX = level + 6;
        fieldY = level + 6;
        rand = new Random(world + level);
        int startX = 1 + rand.nextInt(fieldX - 2);
        int startY = 1 + rand.nextInt(fieldY - 2);
        int buttonID = 0;
        for (int y = 0; y != fieldY; y++) {
            layoutX = new LinearLayout(this);
            layoutForLine.addView(layoutX);

                for (int x = 0; x != fieldX; x++) {

                    if (x == 0 || y==0 || x==fieldX-1 || y==fieldY-1) {


                      imageView=new ImageView(this);
                        imageView.setImageResource(R.drawable.patt);
                        imageView.setMaxHeight(cellXY);
                        imageView.setMaxWidth(cellXY);
                        imageView.setMinimumHeight(cellXY);
                        imageView.setMinimumWidth(cellXY);
                        layoutX.addView(imageView);
                        imageView.setId(imageViewID+buttonID);
                        buttonID++;
                        imageView.setOnTouchListener(this);

                    } else {

                        button = new Button(this);
                        button.setHeight(cellXY);
                        button.setWidth(cellXY);
                        button.setId(buttonID++);
                        layoutX.addView(button);


                    int rnd = rand.nextInt(25);
                    if (x == startX && y == startY) {
                        button.setBackgroundColor(colorLive);

                    } else if (rnd < 2) {
                        button.setText("*");
                        button.setTextColor(colorZ);
                        gold++;
                    } else {

                        rnd = rand.nextInt(9) + 1;
                        button.setText(String.valueOf(rnd));
                        rnd = rand.nextInt(1000);
                        float fLevel = level;
                        fLevel = (1 / fLevel) * 200;
                        if (rnd > (int) (900 - fLevel)) {
                            button.setTextColor(colorGold);

                        } else {

                        }


                    }
                        button.setOnTouchListener(this);
                }

            }
        }

    }

    public void gameMenuVisible(){
        if (level==0)
        {
            finish();
        }
        else {
            if (layoutGame.getVisibility() == View.VISIBLE) {
                layoutMenu.setVisibility(View.VISIBLE);
                layoutGame.setVisibility(View.INVISIBLE);
            } else {
                layoutMenu.setVisibility(View.INVISIBLE);
                layoutGame.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        gameMenuVisible();
    }

}
