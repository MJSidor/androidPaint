package com.example.ism;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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

        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStyle(Paint.Style.STROKE);
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
            restored = false;
        }
        // utwórz kanwę na podstawie bitmapy
        mCanvas = new Canvas(mBitmap);
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
     * Metoda pomocnicza resetująca współrzędne ścieżki do rysownia
     * oraz rysująca w miejscu dotknięcia okrąg (końce linii)
     *
     * @param x
     * @param y
     */
    private void resetPathAndDrawCircle(float x, float y) {
        mPath.reset();
        mCanvas.drawCircle(x, y, 10, mPaint);
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
                resetPathAndDrawCircle(event.getX(), event.getY());
                mPath.moveTo(event.getX(), event.getY()); //ustal współrzędne początkowe ściężki na miejsce dotknięcia
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // rysuj linię po ścieżce ruchu palca
                drawLine(event.getX(), event.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // po oderwaniu palca od ekranu - zresetuj współrzędne ścieżki oraz narysuj okrąg
                resetPathAndDrawCircle(event.getX(), event.getY());
                invalidate();
                break;
        }
        return true;
    }

    /**
     * Metoda ustawiająca kolor pędzla
     *
     * @param color
     */
    public void setPaintColor(int color) {

        mPaint.setColor(color);

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