package com.anshul.android.collagemaker;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anshul on 27/06/15.
 */
public class CollageView extends View {
    private int mCenterY;
    private int mCenterX;
    private Paint mImagePaint, mSelectedPaint, mDeleteBackground, mBackgrounPaint;

    private LinkedList<CustomImage> mImageList;
    private CustomImage mDragingCustomImage;
    private boolean isDragging = false;
    private float downY, downX;
    private boolean isSelected = true;
    private int mBackgroundColor = -1;

    public CollageView(Context context) {
        super(context);
        init();
    }

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CollageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CollageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mImageList = new LinkedList<>();
        mImagePaint = new Paint();
        mSelectedPaint = new Paint();
        mSelectedPaint.setColor(getResources().getColor(R.color.border_color));
        mSelectedPaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.border_stroke_width));
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        mDeleteBackground = new Paint(mSelectedPaint);
        mDeleteBackground.setStyle(Paint.Style.FILL);

        mBackgrounPaint = new Paint();
        mBackgrounPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth;
        int desiredHeight;
        Point point = Utils.getdisplayMatrix(getContext());
        desiredWidth = point.x;
        desiredHeight = point.y;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBackgroundColor != -1) {
            canvas.drawColor(mBackgroundColor);
        }
        for (int i = mImageList.size() - 1; i >= 0; i--) {
            mImageList.get(i).doDraw(canvas, mImagePaint, mSelectedPaint, mDeleteBackground, (i == 0 && isSelected ? true : false));
        }
    }

    public void addImage(String selectedImagePath) {
        CustomImage customImage = new CustomImage(getContext(), selectedImagePath);
        mImageList.addFirst(customImage);
        isSelected = true;
        invalidate();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (getImageFromView(event)) {
                    isDragging = true;
                    isSelected = true;
                    downX = event.getX();
                    downY = event.getY();
                    invalidate();
                    return true;
                } else {
                    isSelected = false;
                    invalidate();
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float dx = event.getX() - downX;
                    float dy = event.getY() - downY;
                    downX = event.getX();
                    downY = event.getY();
                    mImageList.getFirst().addDelta(dx, dy);
                    invalidate();
                    return true;
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    isDragging = false;
                    checkIfDeletePressed(event);
                    invalidate();
                    return true;

                }
                return false;
        }
        return super.onTouchEvent(event);
    }

    private void checkIfDeletePressed(MotionEvent event) {
        if (mImageList != null && mImageList.size() > 0) {
            if (mImageList.getFirst().isDeleteArea(event.getX(), event.getY())) {
                mImageList.removeFirst();
                isSelected = false;
            }
        }
    }

    private boolean getImageFromView(MotionEvent event) {
        for (int i = 0; i < mImageList.size(); i++) {
            if (mImageList.get(i).isImageArea(event.getX(), event.getY())) {
                mDragingCustomImage = mImageList.remove(i);
                mImageList.addFirst(mDragingCustomImage);
                return true;

            }
        }

        return false;
    }

    public boolean canAddMoreImage() {
        if (mImageList != null && mImageList.size() >= 10) {
            return false;
        }
        return true;
    }

    public void save() {
        isSelected = false;
        invalidate();

        setDrawingCacheEnabled(true);
        setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = getDrawingCache();
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(path + "/random_collage_" + System.currentTimeMillis() + ".png");
        FileOutputStream ostream;
        try {
            file.createNewFile();
            ostream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
            Toast.makeText(getContext(), "image saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        } finally {

        }
    }

    public void setcolor(int color) {
        mBackgrounPaint.setColor(color);
        mBackgroundColor = color;
        invalidate();
    }
}
