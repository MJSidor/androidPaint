package com.example.ism;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorLong;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private int strokeWidth;
    private int strokeColor;
    private Paint.Style strokeStyle;
    private String shape;
    private Rect rectangle = null;
    private RectF oval = null;
    private Paint eraserPaint;
    private Canvas tempCanva;
    private Bitmap tempBitmap;

    protected boolean restored = false; // zmienna pomocnicza służąca do określania,
    // czy urządzenie zostało właśnie obrócone

    public DrawView(Context context, Bitmap mBitmap) {
        super(context);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, Bitmap mBitmap) {
        super(context, attrs);
        init();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, Bitmap mBitmap) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DrawView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, Bitmap mBitmap) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public DrawView(Context c) {
        super(c);
        init();
    }

    /**
     * Metoda pomocnicza inicjalizująca zmienne widoku - ścieżka, paint + wartości
     * Używana w konstruktorach
     */
    public void init() {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        strokeWidth = 8;
        strokeColor = Color.BLUE;
        strokeStyle = Paint.Style.STROKE;

        shape = "Line";

        initEraserPaint();

        initPaint();
    }

    public void initPaint() {

        mPaint = new Paint();
        mPaint.setColor(strokeColor);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(strokeStyle);
    }

    public void initEraserPaint() {
        eraserPaint = new Paint(Paint.DITHER_FLAG);
        //eraserPaint.setColor(Color.TRANSPARENT);
        eraserPaint.setStrokeWidth(strokeWidth);
        eraserPaint.setColor(Color.BLACK);
        //eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }


    public void loadBitmapFromFile(Bitmap bmp) {
        // w innym wypadku ustaw bitmapę na podstawie przeskalowanej do nowego wymiaru bitmapy pomocniczej
        Bitmap bitmap = getResizedBitmap(bmp, getWidth(), getHeight());
        mBitmap = bitmap;
        restored = false;

        // utwórz kanwę na podstawie bitmapy
        mCanvas = new Canvas(mBitmap);
    }

    /**
     * Metoda wywoływana przy zmianie rozmiaru widoku, a więc też przy uruchomieniu aplikacji.
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // jeżeli bitmapa nie jest właśnie przywracana po obrocie -
        // utwórz nową bitmapę o wymiarach dopasowanych do nowego widoku
        if (!restored) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        } else {
            // w innym wypadku ustaw bitmapę na podstawie przeskalowanej do nowego wymiaru bitmapy pomocniczej
            Bitmap bmp = getResizedBitmap(mBitmap, w, h);
            mBitmap = bmp;

        }
        // utwórz kanwę na podstawie bitmapy
        mCanvas = new Canvas(mBitmap);
        if (!restored) mCanvas.drawColor(Color.WHITE);
        restored = false;

    }

    /**
     * Metoda pomocnicza służąca do uzyskania przeskalowanej bitmapy po obrocie urządzenia
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // oblicz współczynniki skalowania x,y
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // utwórz macierz
        Matrix matrix = new Matrix();

        // wprowadź do macierzy współczynniki skalowania x,y
        matrix.postScale(scaleWidth, scaleHeight);

        // utwórz nową, przeskalowaną na podstawie macierzy, bitmapę
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

    }

    /**
     * Metoda służąca do rysowania linii podczas przesuwania palca po ekranie
     *
     * @param x
     * @param y
     */
    private void drawLine(float x, float y) {
        mPath.lineTo(x, y);
        mCanvas.drawPath(mPath, mPaint);
    }

    private void createRectangle(float x, float y) {
        this.rectangle.top = (int) y;
        this.rectangle.left = (int) x;
    }

    private void updateRectangle(float x, float y) {
        this.rectangle.bottom = (int) y;
        this.rectangle.right = (int) x;
    }

    private RectF tempOval = null;

    private void createOval(float x, float y) {

        this.oval.top = (int) y;
        this.oval.left = (int) x;


        tempOval.top = y - 9;
        tempOval.left = x - 9;


    }

    private void updateOval(float x, float y) {

        float xChange = this.oval.right-x;
        float yChange = this.oval.bottom-y;

        tempOval.bottom = (float) (oval.bottom + (yChange*2));
        tempOval.right = (float) (oval.right + (xChange*2));;

        this.oval.bottom = (int) y;
        this.oval.right = (int) x;



    }


    /**
     * Metoda wywoływana podczas dotknięcia ekranu
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // w zależności od rodzaju eventu (początek/koniec dotknięcia lub przesuwanie palca po ekranie)
        switch (event.getAction()) {
            // początek dotknięcia
            case MotionEvent.ACTION_DOWN:
                mPath.reset();

                switch (this.shape) {
                    case "Line":
                        mPath.moveTo(event.getX(), event.getY()); //ustal współrzędne początkowe ściężki na miejsce dotknięcia
                        break;
                    case "Rectangle":
                        this.rectangle = new Rect();
                        createRectangle(event.getX(), event.getY());
                        break;
                    case "Oval":
                        this.oval = new RectF();
                        this.tempOval = new RectF();
                        createOval(event.getX(), event.getY());
                }

                invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                // rysuj linię po ścieżce ruchu palca
                if (this.shape == "Line") drawLine(event.getX(), event.getY());
                if (this.shape == "Rectangle") updateRectangle(event.getX(), event.getY());
                if (this.shape == "Oval") {

                    updateOval(event.getX(), event.getY());


                    //mCanvas.drawOval(tempOval, eraserPaint);

                    mCanvas.drawOval(this.oval, mPaint);


                }

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // po oderwaniu palca od ekranu - zresetuj współrzędne ścieżki oraz narysuj okrąg
                switch (this.shape) {

                    case "Rectangle":
                        updateRectangle(event.getX(), event.getY());
                        mCanvas.drawRect(this.rectangle, mPaint);

                        this.rectangle = null;
                        break;
                    case "Oval":

                        /*
                        updateOval(event.getX(), event.getY());

                        mCanvas.drawOval(this.tempOval, mPaint);
                        mCanvas.drawOval(this.oval, mPaint);
                        */
                        this.oval = null;
                        this.tempOval=null;
                        break;
                }

                mPath.reset();
                invalidate();
                break;
        }
        return true;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        mPaint.setStrokeWidth(this.strokeWidth);
    }

    /**
     * Metoda zwracająca szerokość pędzla
     *
     * @return
     */
    public int getStrokeWidth() {
        return strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        mPaint.setColor(this.strokeColor);
        initPaint();
    }

    public int getStyle() {

        if (this.strokeStyle == Paint.Style.STROKE) return 0;
        if (this.strokeStyle == Paint.Style.FILL) return 1;
        if (this.strokeStyle == Paint.Style.FILL_AND_STROKE) return 2;

        else return -1;
    }

    public void setStyle(int style) {
        switch (style) {
            case 0:
                this.strokeStyle = Paint.Style.STROKE;
                break;
            case 1:
                this.strokeStyle = Paint.Style.FILL;
                break;
            case 2:
                this.strokeStyle = Paint.Style.FILL_AND_STROKE;
                break;
        }
        initPaint();
        mPaint.setStyle(this.strokeStyle);
    }

    public void enableEraser() {
        this.mPaint = this.eraserPaint;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
        initPaint();

    }

    /**
     * Metoda wykorzystywana do wyczyszczenia ekranu
     */
    public void clearScreen() {
        setDrawingCacheEnabled(false);
        onSizeChanged(getWidth(), getHeight(), getWidth(), getHeight());
        invalidate();
        setDrawingCacheEnabled(true);
    }

    /**
     * Getter dla bitmapy wykorzystywany do save/restoreInstanceState w mainActivity
     *
     * @return
     */
    public Bitmap getmBitmap() {
        return mBitmap;
    }

    /**
     * Setter dla bitmapy wykorzystywany do save/restoreInstanceState w mainActivity
     *
     * @param mBitmap
     */
    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }
}