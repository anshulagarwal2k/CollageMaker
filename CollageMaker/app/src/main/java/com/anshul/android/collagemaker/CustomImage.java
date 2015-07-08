package com.anshul.android.collagemaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

/**
 * Created by Anshul on 07/07/15.
 */
public class CustomImage {
    Context mContext;
    String mImagePath;

    PointF pointF;
    Bitmap mBitmap;
    float mDeleteAreaWidth;

    public CustomImage(Context context, String path) {
        mContext = context;
        mImagePath = path;
        pointF = new PointF(50, 50);
        mDeleteAreaWidth = mContext.getResources().getDimensionPixelSize(R.dimen.delete_area_width);
        createBitmapFromPath();
    }

    private void createBitmapFromPath() {

        if (TextUtils.isEmpty(mImagePath)) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImagePath, options);
        options.inSampleSize = calculateInSampleSize(options, mContext.getResources().getDimensionPixelSize(R.dimen.default_image_width), mContext.getResources().getDimensionPixelSize(R.dimen.default_image_height));
        options.inJustDecodeBounds = false;
        mBitmap = BitmapFactory.decodeFile(mImagePath, options);

    }

    public PointF getPointF() {
        return pointF;
    }

    public void setPointF(PointF pointF) {
        this.pointF = pointF;
    }

    public void addDelta(float dx, float dy) {
        pointF.x = pointF.x + dx;
        pointF.y = pointF.y + dy;

        this.pointF = pointF;

    }

    public void doDraw(Canvas canvas, Paint bitmapPaint, Paint selectedPaint, Paint deletePaint, boolean isSelected) {
        if (mBitmap == null) {
            return;
        }
        canvas.save();
        canvas.translate(pointF.x, pointF.y);
        canvas.drawBitmap(mBitmap, 0, 0, bitmapPaint);
        if (isSelected) {
            canvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), selectedPaint);
            //todo Add delete drawable
            canvas.rotate(45f);
            Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus);
            canvas.drawCircle(bitmap.getWidth()/ 2, bitmap.getHeight() / 2, mDeleteAreaWidth/2, deletePaint);
            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
            bitmap = null;
        }
        canvas.restore();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public boolean isImageArea(float x, float y) {

        if (x >= pointF.x && x <= pointF.x + mBitmap.getWidth()) {
            if (y >= pointF.y && y <= pointF.y + mBitmap.getHeight()) {
                return true;
            }
        }

        return false;
    }

    public boolean isDeleteArea(float x, float y) {

        if (x >= pointF.x && x <= pointF.x + mDeleteAreaWidth) {
            if (y >= pointF.y && y <= pointF.y + mDeleteAreaWidth) {
                return true;
            }
        }

        return false;
    }

}
