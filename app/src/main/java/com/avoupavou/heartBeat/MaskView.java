package com.avoupavou.heartBeat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.core.graphics.PathParser;

import java.util.Scanner;
import java.util.Vector;

public class MaskView extends View {

    private Paint paint = null;

    public MaskView(Context context) {
        super(context);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        paint = new Paint();
        paint.setShader(null);

        float width = this.getWidth();
        float height = this.getHeight();

        // Starting point
        Path path = new Path();
        path.moveTo(width / 2, height / 5);

        // Upper left path
        path.cubicTo(5 * width / 14, 0,
                0, height / 15,
                width / 28, 2 * height / 5);

        // Lower left path
        path.cubicTo(width / 14, 2 * height / 3,
                3 * width / 7, 5 * height / 6,
                width / 2, height);

        // Lower right path
        path.cubicTo(4 * width / 7, 5 * height / 6,
                13 * width / 14, 2 * height / 3,
                27 * width / 28, 2 * height / 5);

        // Upper right path
        path.cubicTo(width, height / 15,
                9 * width / 14, 0,
                width / 2, height / 5);

        path.lineTo(width/2,0);
        path.lineTo(width,0);
        path.lineTo(width,height);
        path.lineTo(0,height);
        path.lineTo(0,0);
        path.lineTo(width/2,0);
        path.lineTo(width/2,height/5);

        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);

    }
}
