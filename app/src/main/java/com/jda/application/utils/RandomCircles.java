package com.jda.application.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RandomCircles extends View {

    public int height;
    Context context;
    private Paint mPaint;

    private static final int MAX_SIZE = 200;
    private List<Data> dataList = new ArrayList<>();
    private boolean isFirst = true;

    public RandomCircles(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(4f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int wi = 0; wi <= w; wi++) {
            for (int he = 0; he <= h; he++) {
                int size = (int) (Math.random() * MAX_SIZE);

                int outCount = 0;
                for (int p = 0; p < dataList.size(); p++) {
                    int v = (wi - dataList.get(p).x) ^ 2;
                    int b = (he - dataList.get(p).y) ^ 2;
                    int u = (dataList.get(p).size) ^ 2;

                    if (v + b > u) {
                        outCount++;
                    }
                }
                if (isFirst) {
                    addCircle(wi, he, size);
                    isFirst = false;
                }

                if (outCount == dataList.size()) {
                    addCircle(wi, he, size);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Data data : dataList) {
            int size = data.size;
            // mPaint.setAlpha(255 - size);
            canvas.drawCircle(data.x, data.y, data.size, mPaint);
        }
    }

    private void addCircle(int x, int y, int size) {
        dataList.add(new Data(x, y, size));
    }


    class Data {
        public int x;
        public int y;
        public int size;

        public Data(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
}