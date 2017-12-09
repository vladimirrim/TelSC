package ru.spbau.mit.telsc.model.core;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.ArrayList;

import static ru.spbau.mit.telsc.model.core.pixels.Pixels.setTransparent;

/**
 * Created by mikhail on 09.12.17.
 */

public class Eraser {
    /**
     * Method to erase array of points in image.
     * @param image to apply erase action.
     * @param curve set by array of points.
     * @return erased bitmap image.
     */
    public static Bitmap erase(Bitmap image, ArrayList<Point> curve) {
        for (Point point : curve)
            setTransparent(image, point.x, point.y);

        return image;
    }
}
