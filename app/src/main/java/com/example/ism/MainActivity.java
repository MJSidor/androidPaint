package com.example.ism;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private DrawView drawView; // pole zawierające widok do rysowania

    ConstraintLayout cLayout;
    LinearLayout lLayout;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        cLayout = findViewById(R.id.cLayout);

        lLayout = new LinearLayout(this);

        // utworzenie i dodanie DrawView jako widoku w constraintLayout
        drawView = new DrawView(this);
        cLayout.addView(drawView);

        addButtons();

    }

    public void addButtons() {
        lLayout = new LinearLayout(this);

        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );


        Button buttonRed = new Button(this);
        Button buttonBlue = new Button(this);
        Button buttonYellow = new Button(this);
        Button buttonGreen = new Button(this);
        Button buttonClear = new Button(this);

        buttonRed.setLayoutParams(param);
        buttonBlue.setLayoutParams(param);
        buttonYellow.setLayoutParams(param);
        buttonGreen.setLayoutParams(param);
        buttonClear.setLayoutParams(param);

        buttonRed.setBackgroundColor(Color.RED);
        buttonBlue.setBackgroundColor(Color.BLUE);
        buttonYellow.setBackgroundColor(Color.YELLOW);
        buttonGreen.setBackgroundColor(Color.GREEN);
        buttonClear.setText("X");

        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor("red");
            }
        });

        buttonBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor("blue");
            }
        });

        buttonYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor("yellow");
            }
        });

        buttonGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor("green");
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clearScreen();
            }
        });

        lLayout.addView(buttonRed);
        lLayout.addView(buttonBlue);
        lLayout.addView(buttonYellow);
        lLayout.addView(buttonGreen);
        lLayout.addView(buttonClear);


        cLayout.addView(lLayout);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable("bitmap", drawView.getmBitmap());
        super.onSaveInstanceState(outState);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Bitmap bmp = savedInstanceState.getParcelable("bitmap");
        drawView.setmBitmap(bmp);
        drawView.restored = true;
        super.onRestoreInstanceState(savedInstanceState);
    }
}