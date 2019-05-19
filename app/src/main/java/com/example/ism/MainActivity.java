package com.example.ism;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

import javax.xml.datatype.Duration;

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
        Button buttonStrokeColor = new Button(this);
        Button buttonClearScreen = new Button(this);
        Button buttonStrokeWidth = new Button(this);
        Button buttonTool = new Button(this);

        // ustawienie parametrów przycisków
        buttonStrokeColor.setLayoutParams(param);
        buttonClearScreen.setLayoutParams(param);
        buttonStrokeWidth.setLayoutParams(param);
        buttonStrokeWidth.setLayoutParams(param);

        // ustawienie kolorów/tesktu przycisków
        buttonStrokeColor.setText("C");
        buttonClearScreen.setText("X");
        buttonStrokeWidth.setText("W");
        buttonTool.setText("T");


        // ustawienie listenerów na przyciskach
        buttonStrokeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showColorPicker();
            }
        });

        buttonClearScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.clearScreen();
            }
        });

        buttonStrokeWidth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                showWidthDialog();
            }
        });

        buttonTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] tools = {"Brush", "Filler", "Stroke and fill"};

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Select a tool")
                        .setItems(tools, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        drawView.setStrokeStyle(Paint.Style.STROKE);
                                        break;

                                    case 1:
                                        drawView.setStrokeStyle(Paint.Style.FILL);
                                        break;

                                    case 2:
                                        drawView.setStrokeStyle(Paint.Style.FILL_AND_STROKE);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        // dodanie przycisków do layoutu
        lLayout.addView(buttonStrokeColor);
        lLayout.addView(buttonClearScreen);
        lLayout.addView(buttonStrokeWidth);
        lLayout.addView(buttonTool);

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

    public void showColorPicker() {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Choose color")
                .initialColor(Color.RED)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //Toast.makeText(MainActivity.this, "onColorSelected: 0x" + Integer.toHexString(selectedColor), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        drawView.setStrokeColor(selectedColor);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
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
        outState.putInt("strokeWidth", drawView.getStrokeWidth());
        outState.putInt("strokeColor", drawView.getStrokeColor());
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
        int strokeWidth = savedInstanceState.getInt("strokeWidth");
        int strokeColor = savedInstanceState.getInt("strokeColor");

        // ustaw zapisaną bitmapę w obiekcie drawView
        drawView.setmBitmap(bmp);

        // ustaw zmienną logiczną drawView dotyczącą przywracania bitmapy po obrocie
        drawView.restored = true;

        drawView.setStrokeColor(strokeColor);
        drawView.setStrokeWidth(strokeWidth);
        super.onRestoreInstanceState(savedInstanceState);
    }
}