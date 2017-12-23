package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import ru.spbau.mit.telsc.model.core.polygon.Polygon;

/**
 * Created by mikhail on 30.11.17.
 */

public class Curve {
    /**
     * Draws curve on image.
     * @param image bitmap where to draw.
     * @param curve sequence of points color of which to change.
     * @param color new color.
     * @return bitmap with drawn curve.
     */
    public static Bitmap drawCurve(Bitmap image, ArrayList<Point> curve, int color) {
        for (Point point : curve)
            image.setPixel(point.x, point.y, color);

        return image;
    }
}
