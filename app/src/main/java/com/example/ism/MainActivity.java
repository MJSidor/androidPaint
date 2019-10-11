package com.example.ism;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.FloatMath;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import javax.xml.datatype.Duration;

public class MainActivity extends Activity implements SensorEventListener {

    private DrawView drawView; // pole zawierające widok do rysowania

    ConstraintLayout cLayout;
    LinearLayout lLayout;
    Bitmap bmp;
    int brushWidth;
    boolean fileOpen = false;
    boolean camera = false;
    boolean saving = false;
    Uri photoUri = null;
    AlertDialog.Builder builder;
    AlertDialog dialog = null;
    private SensorManager sensorManager;
    Sensor accelerometer;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        setContentView(R.layout.activity_main);
        cLayout = findViewById(R.id.cLayout);

        lLayout = new LinearLayout(this);

        // utworzenie i dodanie DrawView jako widoku w constraintLayout
        drawView = new DrawView(this);
        cLayout.addView(drawView);

        addButtons();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

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

        ImageButton buttonTool = new ImageButton(this);
        ImageButton buttonShape = new ImageButton(this);
        ImageButton buttonClearScreen = new ImageButton(this);
        ImageButton buttonCamera = new ImageButton(this);
        ImageButton buttonOpenFile = new ImageButton(this);
        ImageButton buttonSaveImageToFile = new ImageButton(this);
        ImageButton buttonStrokeColor = new ImageButton(this);
        ImageButton buttonStrokeWidth = new ImageButton(this);

        buttonSaveImageToFile.setImageResource(R.drawable.ic_save_dark);
        buttonOpenFile.setImageResource(R.drawable.ic_open_dark);
        buttonCamera.setImageResource(R.drawable.ic_camera_dark);
        buttonStrokeColor.setImageResource(R.drawable.ic_color_dark);
        buttonClearScreen.setImageResource(R.drawable.ic_clear_dark);
        buttonStrokeWidth.setImageResource(R.drawable.ic_width_dark);
        buttonTool.setImageResource(R.drawable.ic_tool_dark);
        buttonShape.setImageResource(R.drawable.ic_shape_dark);

        // ustawienie parametrów przycisków
        buttonStrokeColor.setLayoutParams(param);
        buttonClearScreen.setLayoutParams(param);
        buttonStrokeWidth.setLayoutParams(param);
        buttonTool.setLayoutParams(param);
        buttonOpenFile.setLayoutParams(param);
        buttonSaveImageToFile.setLayoutParams(param);
        buttonShape.setLayoutParams(param);
        buttonCamera.setLayoutParams(param);


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

                saveImage();

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
        lLayout.addView(buttonTool);
        lLayout.addView(buttonShape);
        lLayout.addView(buttonStrokeWidth);
        lLayout.addView(buttonStrokeColor);
        lLayout.addView(buttonOpenFile);
        lLayout.addView(buttonCamera);
        lLayout.addView(buttonSaveImageToFile);

        lLayout.addView(buttonClearScreen);

        // zagnieżdżenie (dodanie) layoutu z przyciskami w layoucie głównym
        cLayout.addView(lLayout);
    }


    /**
     * Metoda wyświetlająca dialog z edycją grubości pędzla
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showWidthDialog() {

        builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage(Integer.toString(drawView.getStrokeWidth()) + "%")
                .setTitle("Width");

        final SeekBar widthSeekBar = new SeekBar(this);
        widthSeekBar.setMax(101);
        widthSeekBar.setMin(1);
        widthSeekBar.setKeyProgressIncrement(1);
        widthSeekBar.setProgress(drawView.getStrokeWidth());

        builder.setView(widthSeekBar);

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brushWidth = progress;
                dialog.setMessage(Integer.toString(progress) + "%");
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

        dialog = builder.create();
        dialog.show();
    }

    public void showColorPicker() {
        ColorPickerDialogBuilder
                .with(MainActivity.this)
                .setTitle("Colors")
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
        // String[] tools = {"Brush", "Filler", "Fill the drawn shape", "Eraser"};

        final String [] items = new String[] {"Brush", "Filler", "Fill the drawn shape", "Eraser"};
        final Integer[] icons = new Integer[] {R.drawable.ic_tool_brush, R.drawable.ic_tool_filler,R.drawable.ic_tool_fill_the_drawn_shape, R.drawable.ic_tool_eraser};
        ListAdapter adapter = new ArrayAdapterWithIcon(MainActivity.this, items, icons);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Tools")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (which != 3) drawView.setStyle(which);
                        if (which == 3) drawView.enableEraser();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showShapePicker() {
        // final String[] tools = {"Line", "Rectangle", "Oval"};

        final String [] items = new String[] {"Line", "Rectangle", "Oval"};
        final Integer[] icons = new Integer[] {R.drawable.ic_shape_line,R.drawable.ic_shape_rectangle,R.drawable.ic_shape_oval};
        ListAdapter adapter = new ArrayAdapterWithIcon(MainActivity.this, items, icons);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Shapes")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        drawView.setShape(items[which]);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private String currentPhotoPath;

    private File createTempImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "mPAINT_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void saveImage() {

        this.saving = true;
        File photoFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "mPAINT_" + timeStamp + ".jpg";
        // File storageDir = getExternalFilesDir("mPaint");

        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "mPaint");
        if (!dir.mkdirs()) {
            Log.e(imageFileName, "Directory not created");
        }

        photoFile = new File(dir, imageFileName);

        try {
            FileOutputStream out = new FileOutputStream(photoFile);
            Bitmap bmp = drawView.getmBitmap();
            bmp.setHasAlpha(true);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            addImageToGallery(photoFile.getAbsolutePath(), this);
            Toast.makeText(MainActivity.this, "Saving image to file...", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            this.saving = false;
        }
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createTempImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                this.photoUri = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
        this.camera = true;
    }


    /**
     * Metoda wywoływana przy obrocie urządzenia
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if (!camera && !saving && !fileOpen) {
            outState.putInt("strokeWidth", drawView.getStrokeWidth());
            outState.putInt("strokeColor", drawView.getStrokeColor());
            outState.putInt("strokeStyle", drawView.getStyle());
            outState.putString("shape", drawView.getShape());
        }

        if (!fileOpen && !camera && !saving) {

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

        if (!fileOpen && !camera && !saving) {

            int strokeWidth = savedInstanceState.getInt("strokeWidth");
            int strokeColor = savedInstanceState.getInt("strokeColor");
            int strokeStyle = savedInstanceState.getInt("strokeStyle");
            String shape = savedInstanceState.getString("shape");

            drawView.setStrokeColor(strokeColor);
            drawView.setStrokeWidth(strokeWidth);
            drawView.setStyle(strokeStyle);
            drawView.setShape(shape);

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

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            try {
                drawView.loadBitmapFromFile(getBitmapFromUri(this.photoUri));
                this.fileOpen = false;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                deleteFile(this.photoUri);
                this.photoUri = null;
                this.camera = false;
            }


        }

    }

    public void deleteFile(Uri uri) {

        final ContentResolver contentResolver = this.getApplicationContext().getContentResolver();

        File file = new File(uri.getPath());

        final String pathone = MediaStore.MediaColumns.DATA + "=?";
        final String[] selectedArgs = new String[]{
                file.getAbsolutePath()
        };

        contentResolver.delete(uri, pathone, selectedArgs);
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return image;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = Math.sqrt(x*x + y*y + z*z);
            double delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta;

            if(mAccel > 30){
                drawView.clearScreen();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

}