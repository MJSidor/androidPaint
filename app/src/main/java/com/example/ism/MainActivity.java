package com.example.ism;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private DrawView drawView; // pole zawierające widok do rysowania

    ConstraintLayout cLayout;
    LinearLayout lLayout;
    Bitmap bmp;
    int brushWidth;

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

    /**
     * Metoda pomocnicza dodająca przyciski, ich właściwości oraz layout do istniejącego layoutu
     */
    public void addButtons() {

        // nowy layout (kontener na przyciski)
        lLayout = new LinearLayout(this);

        // uniwersalne parametry dla przycisków - width,height,weight
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        // tworzenie przycisków
        Button buttonRed = new Button(this);
        Button buttonBlue = new Button(this);
        Button buttonYellow = new Button(this);
        Button buttonGreen = new Button(this);
        Button buttonClear = new Button(this);
        Button buttonWidth = new Button(this);

        // ustawienie parametrów przycisków
        buttonRed.setLayoutParams(param);
        buttonBlue.setLayoutParams(param);
        buttonYellow.setLayoutParams(param);
        buttonGreen.setLayoutParams(param);
        buttonClear.setLayoutParams(param);
        buttonWidth.setLayoutParams(param);

        // ustawienie kolorów/tesktu przycisków
        buttonRed.setBackgroundColor(Color.RED);
        buttonBlue.setBackgroundColor(Color.BLUE);
        buttonYellow.setBackgroundColor(Color.YELLOW);
        buttonGreen.setBackgroundColor(Color.GREEN);
        buttonClear.setText("X");
        buttonWidth.setText("W");


        // ustawienie listenerów na przyciskach
        buttonRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setPaintColor("red"); // zmiana koloru paint w obiekcie DrawView
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

        buttonWidth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                showWidthDialog();

            }
        });

        // dodanie przycisków do layoutu
        lLayout.addView(buttonRed);
        lLayout.addView(buttonBlue);
        lLayout.addView(buttonYellow);
        lLayout.addView(buttonGreen);
        lLayout.addView(buttonClear);
        lLayout.addView(buttonWidth);

        // zagnieżdżenie (dodanie) layoutu z przyciskami w layoucie głównym
        cLayout.addView(lLayout);
    }


    /**
     * Metoda wyświetlająca dialog z edycją grubości pędzla
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showWidthDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Current value: " + Integer.toString(drawView.getStrokeWidth()))
                .setTitle("Adjust brush width");

        SeekBar widthSeekBar = new SeekBar(this);
        widthSeekBar.setMax(100);
        widthSeekBar.setMin(1);
        widthSeekBar.setKeyProgressIncrement(1);
        widthSeekBar.setProgress(drawView.getStrokeWidth());

        builder.setView(widthSeekBar);

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushWidth = progress;
                builder.setMessage("Current value: " + Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                drawView.setStrokeWidth(brushWidth);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Metoda wywoływana przy obrocie urządzenia
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // zapisz obecną bitmapę
        outState.putParcelable("bitmap", drawView.getmBitmap());
        super.onSaveInstanceState(outState);
    }

    /**
     * Metoda wywoływana po obróceniu urządzenia
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        Bitmap bmp = savedInstanceState.getParcelable("bitmap");

        // ustaw zapisaną bitmapę w obiekcie drawView
        drawView.setmBitmap(bmp);

        // ustaw zmienną logiczną drawView dotyczącą przywracania bitmapy po obrocie
        drawView.restored = true;
        super.onRestoreInstanceState(savedInstanceState);
    }
}