package com.example.ism;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.util.Size;
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

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.datatype.Duration;

public class MainActivity extends Activity {

    private DrawView drawView; // pole zawierające widok do rysowania

    ConstraintLayout cLayout;
    LinearLayout lLayout;
    Bitmap bmp;
    int brushWidth;
    boolean fileOpen = false;
    boolean camera = false;

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
        Button buttonOpenFile = new Button(this);
        Button buttonSaveImageToFile = new Button(this);
        Button buttonShape = new Button(this);
        Button buttonCamera = new Button(this);

        // ustawienie parametrów przycisków
        buttonStrokeColor.setLayoutParams(param);
        buttonClearScreen.setLayoutParams(param);
        buttonStrokeWidth.setLayoutParams(param);
        buttonTool.setLayoutParams(param);
        buttonOpenFile.setLayoutParams(param);
        buttonSaveImageToFile.setLayoutParams(param);
        buttonShape.setLayoutParams(param);
        buttonCamera.setLayoutParams(param);

        // ustawienie kolorów/tesktu przycisków
        buttonStrokeColor.setText("C");
        buttonClearScreen.setText("X");
        buttonStrokeWidth.setText("W");
        buttonTool.setText("T");
        buttonOpenFile.setText("O");
        buttonSaveImageToFile.setText("S");
        buttonShape.setText("Sh");
        buttonCamera.setText("Cam");


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

                showToolPicker();
            }
        });

        buttonOpenFile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                performFileSearch();
                Toast.makeText(MainActivity.this, "Choose an image to edit", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSaveImageToFile.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                saveTempBitmap(drawView.getmBitmap());
                Toast.makeText(MainActivity.this, "Saving image to file...", Toast.LENGTH_SHORT).show();

                /*
                try {
                    saveImageToFile(drawView.getmBitmap());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        });

        buttonShape.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                showShapePicker();

            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                dispatchTakePictureIntent();

            }
        });

        // dodanie przycisków do layoutu
        lLayout.addView(buttonStrokeColor);
        lLayout.addView(buttonClearScreen);
        lLayout.addView(buttonStrokeWidth);
        lLayout.addView(buttonTool);
        lLayout.addView(buttonOpenFile);
        lLayout.addView(buttonSaveImageToFile);
        lLayout.addView(buttonShape);
        lLayout.addView(buttonCamera);

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

    public void showToolPicker() {
        String[] tools = {"Brush", "Filler", "Stroke and fill"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select a tool")
                .setItems(tools, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        drawView.setStyle(which);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showShapePicker() {
        final String[] tools = {"Line", "Rectangle", "Oval"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select a shape")
                .setItems(tools, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        drawView.setShape(tools[which]);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
        this.camera=true;
    }


    /**
     * Metoda wywoływana przy obrocie urządzenia
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (!camera) {
            outState.putInt("strokeWidth", drawView.getStrokeWidth());
            outState.putInt("strokeColor", drawView.getStrokeColor());
            outState.putInt("strokeStyle", drawView.getStyle());
        }

        if (!fileOpen && !camera) {

            // zapisz obecną bitmapę
            outState.putParcelable("bitmap", drawView.getmBitmap());
        }

        super.onSaveInstanceState(outState);
    }

    /**
     * Metoda wywoływana po obróceniu urządzenia
     *
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        if (!fileOpen && !camera) {

            int strokeWidth = savedInstanceState.getInt("strokeWidth");
            int strokeColor = savedInstanceState.getInt("strokeColor");
            int strokeStyle = savedInstanceState.getInt("strokeStyle");

            drawView.setStrokeColor(strokeColor);
            drawView.setStrokeWidth(strokeWidth);
            drawView.setStyle(strokeStyle);
            Bitmap bmp = savedInstanceState.getParcelable("bitmap");
            // ustaw zapisaną bitmapę w obiekcie drawView
            drawView.setmBitmap(bmp);

            // ustaw zmienną logiczną drawView dotyczącą przywracania bitmapy po obrocie
            drawView.restored = true;
        }


        super.onRestoreInstanceState(savedInstanceState);
    }


    private static final int READ_REQUEST_CODE = 42;

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    public void performFileSearch() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        intent.setType("image/*");

        startActivityForResult(intent, READ_REQUEST_CODE);

        this.fileOpen = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();

                try {
                    drawView.loadBitmapFromFile(getBitmapFromUri(uri));
                    this.fileOpen = false;

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = resultData.getExtras();
            Bitmap photoBitmap = (Bitmap) extras.get("data");
            drawView.loadBitmapFromFile(photoBitmap);
            this.camera=false;
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void saveImageToFile(Bitmap bmp) throws IOException {

        File file = new File(getGalleryPath(), "image.png");
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

            // out.write(bmp);
            out.flush();
            out.close();

            Toast.makeText(MainActivity.this, "Image saved successfully", Toast.LENGTH_SHORT).show();

            // MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public String getGalleryPath() {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if (!folder.exists()) {
            folder.mkdir();
        }

        return folder.getAbsolutePath();
    }

    public void saveTempBitmap(Bitmap bitmap) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap);
        } else {
            //prompt the user or do something
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "Shutta_" + timeStamp + ".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


}