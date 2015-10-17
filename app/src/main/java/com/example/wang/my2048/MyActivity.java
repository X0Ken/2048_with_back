package com.example.wang.my2048;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;


public class MyActivity extends ActionBarActivity implements GestureDetector.OnGestureListener, View.OnTouchListener {

    private TextView[] allTextView;
    private int[] numData;
    private String[] colorData;
    private TextView score;
    SQLiteDatabase db;
    int scoreNum = 0;
    boolean isDoAdd;

    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        init();
        mGestureDetector = new GestureDetector(this, this);
        LinearLayout gameView = (LinearLayout) findViewById(R.id.gameView);
        score = (TextView) findViewById(R.id.score);
        score.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                backStateFromDb();
                return false;
            }
        });
        gameView.setOnTouchListener(this);
        gameView.setClickable(true);
    }

    @Override
    protected void onPause() {
        saveStateToDb();
        db.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        backStateFromDb();
    }


    private void gameStart() {
        this.scoreNum = 0;
        this.setScore(0);
        for (int i = 0; i < 16; i++) {
            setNum(i, 1);
        }
        addOneNum();
        addOneNum();
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }


    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float v, float v2) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        isDoAdd = false;
        saveStateToDb();
        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            if (velocityX > 0) {
                moveRight();
            } else {
                moveLeft();
            }
        } else {
            if (velocityY > 0) {
                moveDown();
            } else {
                moveTop();
            }
        }
        if (isDoAdd) {
            addOneNum();
        } else {
            deleteOneDataFromDb();
        }
        return false;
    }


    private void addOneNum() {
        int[] nullCell = new int[16];
        int count = 0;
        for (int i = 0; i < 16; i++) {
            if (numData[i] == 1) {
                nullCell[count] = i;
                count++;
            }
        }
        if (count == 0) {
            return;
        }
        Random r = new Random();
        int index = r.nextInt(count);
        int data = r.nextInt(10);
        data = data == 0 ? 1 : 0;
        setNum(nullCell[index], (data + 1) * 2);
    }

    private void moveDown() {
        //Toast.makeText(getApplicationContext(),"下",Toast.LENGTH_SHORT).show();
        for (int y = 0; y < 4; y++) {
            for (int x = 3; x > 0; x--) {
                int p = (x * 4) + y;
                for (int z = 1; z < 4; z++) {
                    int q = (x - z) * 4 + y;
                    if (q < y) {
                        break;
                    }
                    if (numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] != 1) {
                        setNum(p, numData[q]);
                        setNum(q, 1);
                        continue;
                    } else if (numData[p] != numData[q]) {
                        break;
                    } else {
                        setNum(p, numData[p] * 2);
                        setScore(numData[p] * 2);
                        setNum(q, 1);
                        break;
                    }
                }
            }
        }
    }

    private void moveTop() {
        //Toast.makeText(getApplicationContext(),"上",Toast.LENGTH_SHORT).show();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 3; x++) {
                int p = (x * 4) + y;
                for (int z = 1; z < 4; z++) {
                    int q = (x + z) * 4 + y;
                    if (q > 12 + y) {
                        break;
                    }
                    if (numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] != 1) {
                        setNum(p, numData[q]);
                        setNum(q, 1);
                        continue;
                    } else if (numData[p] != numData[q]) {
                        break;
                    } else {
                        setNum(p, numData[p] * 2);
                        setScore(numData[p] * 2);
                        setNum(q, 1);
                        break;
                    }
                }
            }
        }
    }

    private void moveLeft() {
        // Toast.makeText(getApplicationContext(),"左",Toast.LENGTH_SHORT).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 3; y++) {
                int p = (x * 4) + y;
                for (int z = 1; z < 4; z++) {
                    int q = p + z;
                    if (q > (x + 1) * 4 - 1) {
                        break;
                    }
                    if (numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] != 1) {
                        setNum(p, numData[q]);
                        setNum(q, 1);
                        continue;
                    } else if (numData[p] != numData[q]) {
                        break;
                    } else {
                        setNum(p, numData[p] * 2);
                        setScore(numData[p] * 2);
                        setNum(q, 1);
                        break;
                    }
                }
            }
        }
    }

    private void moveRight() {
        //Toast.makeText(getApplicationContext(),"右",Toast.LENGTH_SHORT).show();
        for (int x = 0; x < 4; x++) {
            for (int y = 3; y > 0; y--) {
                int p = (x * 4) + y;
                for (int z = 1; z < 4; z++) {
                    int q = p - z;
                    if (q < x * 4) {
                        break;
                    }
                    if (numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] == 1) {
                        continue;
                    } else if (numData[p] == 1 && numData[q] != 1) {
                        setNum(p, numData[q]);
                        setNum(q, 1);
                        continue;
                    } else if (numData[p] != numData[q]) {
                        break;
                    } else {
                        setNum(p, numData[p] * 2);
                        setScore(numData[p] * 2);
                        setNum(q, 1);
                        break;
                    }
                }
            }
        }
    }


    private void setScore(int s) {
        scoreNum += s;
        score.setText(scoreNum + "");
    }

    private void init() {
        dbCreate();
        findAllTextView();
        setColorData();
        numData = new int[16];
    }

    private void dbCreate() {
        db = openOrCreateDatabase("My2048Database.db", Context.MODE_PRIVATE, null);
        db.execSQL("create table if not exists GameRecord(" +
                "Id    INTEGER PRIMARY KEY,\n" +
                "    Game  VARCHAR NOT NULL,\n" +
                "    Score INTEGER NOT NULL )");
    }

    private void saveStateToDb() {
        String gameNumState = "";
        for (int i = 0; i < 16; i++) {
            gameNumState += (numData[i] + ",");
        }
        db.execSQL("insert into GameRecord(Game,Score) values('" +
                gameNumState + "','" +
                scoreNum + "')");
        Log.d("DB:", "Save");
    }

    private void backStateFromDb() {
        Cursor c = db.rawQuery("select * from GameRecord order by Id desc limit 1", null);
        if (!c.moveToNext()) {
            gameStart();
            return;
        }
        int id = c.getInt(c.getColumnIndex("Id"));
        String gameStateNnm = c.getString(c.getColumnIndex("Game"));
        int score = c.getInt(c.getColumnIndex("Score"));
        this.scoreNum = 0;
        this.setScore(score);
        Log.d("DB:", "Score-" + score);
        Log.d("DB:", "gameDb-" + gameStateNnm);
        String[] numString = gameStateNnm.split(",");
        for (int i = 0; i < 16; i++) {
            this.setNum(i, Integer.parseInt(numString[i]));
        }
        Log.d("DB:", "dbId-" + id + "");
        db.delete("GameRecord", "Id = ?", new String[]{id + ""});
        Log.d("DB:", "Back");
    }

    private void deleteOneDataFromDb() {
        Cursor c = db.rawQuery("select * from GameRecord order by Id desc limit 1", null);
        if (!c.moveToNext()) {
            return;
        }
        int id = c.getInt(c.getColumnIndex("Id"));
        db.delete("GameRecord", "Id = ?", new String[]{id + ""});
        Log.d("DB:", "DeleteOneNoUse");
    }

    private void setNum(int index, int num) {
        numData[index] = num;
        int colorIndex = (int) Math.log((double) num);
        if (colorIndex > 15) {
            colorIndex = 15;
        }
        if (num == 1) {
            allTextView[index].setText("");
            allTextView[index].setBackgroundColor(Color.parseColor(colorData[colorIndex]));
        } else {
            allTextView[index].setText("" + num);
            allTextView[index].setBackgroundColor(Color.parseColor(colorData[colorIndex]));
            if (num > 512) {
                allTextView[index].setTextSize(20);
            } else {
                allTextView[index].setTextSize(30);
            }
        }
        isDoAdd = true;
    }

    private void setColorData() {
        colorData = new String[16];
        colorData[0] = "#cdc1b4";
        colorData[1] = "#eee4da";
        colorData[2] = "#ede0c8";
        colorData[3] = "#ece0c8";
        colorData[4] = "#ecd0c8";
        colorData[5] = "#ebe0c8";
        colorData[6] = "#ede0c0";
        colorData[7] = "#ede0c8";
        colorData[8] = "#aae0c8";
        colorData[9] = "#edaac8";
        colorData[10] = "#ede0aa";
        colorData[11] = "#99e0c8";
        colorData[12] = "#88e0c8";
        colorData[13] = "#77e0c8";
        colorData[14] = "#66e0c8";
        colorData[15] = "#55e0c8";
    }

    private void findAllTextView() {
        allTextView = new TextView[16];
        allTextView[0] = (TextView) findViewById(R.id.t0);
        allTextView[1] = (TextView) findViewById(R.id.t1);
        allTextView[2] = (TextView) findViewById(R.id.t2);
        allTextView[3] = (TextView) findViewById(R.id.t3);
        allTextView[4] = (TextView) findViewById(R.id.t4);
        allTextView[5] = (TextView) findViewById(R.id.t5);
        allTextView[6] = (TextView) findViewById(R.id.t6);
        allTextView[7] = (TextView) findViewById(R.id.t7);
        allTextView[8] = (TextView) findViewById(R.id.t8);
        allTextView[9] = (TextView) findViewById(R.id.t9);
        allTextView[10] = (TextView) findViewById(R.id.t10);
        allTextView[11] = (TextView) findViewById(R.id.t11);
        allTextView[12] = (TextView) findViewById(R.id.t12);
        allTextView[13] = (TextView) findViewById(R.id.t13);
        allTextView[14] = (TextView) findViewById(R.id.t14);
        allTextView[15] = (TextView) findViewById(R.id.t15);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            try {
                db.execSQL("delete from GameRecord");
            } catch (Exception e) {
                Log.d("DB:", e.getMessage());
            }

            Log.d("DB:", "Restart");
            gameStart();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        return mGestureDetector.onTouchEvent(motionEvent);
    }
}
