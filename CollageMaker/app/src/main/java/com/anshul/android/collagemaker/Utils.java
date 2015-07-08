package com.anshul.android.collagemaker;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by Anshul on 27/06/15.
 */
public class Utils {

    public static Point getdisplayMatrix(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
       return size;
    }



}
